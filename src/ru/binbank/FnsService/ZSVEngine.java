package ru.binbank.fnsservice;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.contracts.ZSVResponse;

import javax.xml.datatype.DatatypeFactory;

public class ZSVEngine {
    private String driverName = "org.apache.hive.jdbc.HiveDriver";
    private Connection hiveConnection;
    //private Statement stmt;

    // Параметры подключения
    private String connectString;
    private String connectLogin;
    private String connectPassword;

    public ZSVEngine(String connectString, String connectLogin, String connectPassword) {
        this.connectString = connectString;
        this.connectLogin = connectLogin;
        this.connectPassword = connectPassword;
    }

    public void createHiveConnection() throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        hiveConnection = DriverManager.getConnection(
                connectString,     // строка соединения, например "jdbc:hive2://msk-hadoop01:10000/default"
                connectLogin,
                connectPassword
        );
    }

    /**
     * Поиск идентификаторов банков по их БИКам.
     * @param bics
     * @return
     */
    private Map<String, Long> selectBankIdByBIC(Collection<String> bics) throws SQLException {
        // Формирование текста запроса
        String query = "select idbank, bic from 440_p.bank where bic in ("
                .concat(bics.stream().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(","))) // quote
                .concat(");");

        // Выполнение запроса
        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Разбор результата
        HashMap<String, Long> result = new HashMap<String, Long>();

        while (resultSet.next()) {
            result.put(resultSet.getString("bic"), resultSet.getLong("idbank"));
        }

        resultSet.close();
        stmt.close();

        return result;
    }

    /**
     * Проверка, что ИНН существует в базе.
     * @param inns
     * @return
     */
    private Collection<String> selectExistingInn(Collection<String> inns, Long idBank) throws SQLException {
        // Формирование текста запроса
        String query = "select inn from 440_p.inn where inn in ("
                .concat(inns.stream().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(","))) // quote
                .concat(") and idbank = ".concat(String.valueOf(idBank)));

        // Выполнение запроса
        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Разбор результата
        ArrayList<String> result = new ArrayList<String>();

        while (resultSet.next()) {
            result.add(resultSet.getString("bic"));
        }

        return result;
    }



    public void closeHiveConnection() throws SQLException {
        hiveConnection.close();
    }


    public Statement getStatement() throws SQLException {
        return hiveConnection.createStatement();
    }


    public void closeStatement(Statement statement) throws SQLException {
        statement.close();
    }


    /**
     * Формирование текста запроса
     * @param requests
     * @throws SQLException
     */
    public String getHiveQuery(Collection<ZSVRequest> requests) {

        // Определяем временной интервал
        ArrayList<Date> alldates = new ArrayList<Date>();

        for (ZSVRequest r: requests) {
            alldates.add(r.getZapnoVipis().getzaPeriod().getDateBeg().toGregorianCalendar().getTime());
            alldates.add(r.getZapnoVipis().getzaPeriod().getDateEnd().toGregorianCalendar().getTime());
        }

        Date mindate = Collections.min(alldates);
        Date maxdate = Collections.max(alldates);

        // Форматируем даты
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringMindate = format.format(mindate);
        String stringMaxdate = format.format(maxdate);

        // Формируем общий запрос
        String query = "select a.dtoperdate, b.code, a.amountdeb, a.amountcre" +
                "  from 440_p.zsv_lines_parquet a" +
                " inner join ( select * from 440_p.account where code in (";

        // @todo - for (ZSVRequest r: requests)
        for (Iterator itRequests = requests.iterator(); itRequests.hasNext(); ) {
            ZSVRequest objectRequests = (ZSVRequest)itRequests.next();

            for (Iterator itPoUkazannim = objectRequests.getZapnoVipis().getpoUkazannim().iterator(); itPoUkazannim.hasNext(); ) {
                ZSVRequest.ZapnoVipis.poUkazannim objectPoUkazannim = (ZSVRequest.ZapnoVipis.poUkazannim)itPoUkazannim.next();
                query = query + "'" + objectPoUkazannim.getNomSch() + "'";

                if (itPoUkazannim.hasNext()) { query = query + ", "; }; //else { query = query + ")"; };
            }

            if (itRequests.hasNext()) { query = query + ", "; } else { query = query + ")"; };
        }

        query = query + ") b" +
                "   on a.idaccount = b.idacc and" +
                "      a.idbank = b.idbank " +
                "where a.dtoperdate between cast ('" + stringMindate + "' as date) and cast ('" + stringMaxdate + "' as date)";

        System.out.println(query); // kvd

        return query;
    }


    /**
     * Получение ответов на запросы ФНС.
     * @param requests
     * @throws SQLException
     */
    public Collection<ZSVResponse> getResult(Collection<ZSVRequest> requests) throws SQLException, ParseException, ClassNotFoundException {

        ArrayList<ZSVResponse> responses = new ArrayList<>();

        createHiveConnection();
        Statement stmt = getStatement();

        try {
            // Формировапние текста запроса
            String hiveQuery = getHiveQuery(requests);
            System.out.println(hiveQuery); // kvd

            // Выполнение запроса
            ResultSet resultSet = stmt.executeQuery(hiveQuery);

            // Заполнение массива строками результата
            SimpleDateFormat formatResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Группировка объектов по счетам
            Map opersByAcc = new HashMap<String, ArrayList<ZSVResponse.SvBank.Svedenia.Operacii>>();

            while (resultSet.next()) {
                ZSVResponse zsvResponse = new ZSVResponse();

                ZSVResponse.SvBank.Svedenia.Operacii operacii = new ZSVResponse.SvBank.Svedenia.Operacii();

                // a.dtoperdate
                ZSVResponse.SvBank.Svedenia.Operacii.RekvDoc rekvDoc = new ZSVResponse.SvBank.Svedenia.Operacii.RekvDoc();

                GregorianCalendar dataDocGreg = new GregorianCalendar();
                dataDocGreg.setTime(formatResponse.parse(resultSet.getString(1)));
                javax.xml.datatype.XMLGregorianCalendar dataDocGregXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataDocGreg);
                rekvDoc.setDataDoc(dataDocGregXML);

                // Номер счета
                String accountCode = resultSet.getString(2);

                // Суммы по операции
                ZSVResponse.SvBank.Svedenia.Operacii.SummaOper summaOper = new ZSVResponse.SvBank.Svedenia.Operacii.SummaOper();

                // a.amountdeb
                BigDecimal amountDebetBigDecimal = new BigDecimal(resultSet.getString(3));
                summaOper.setDebet( amountDebetBigDecimal);

                // a.amountcre
                BigDecimal amountCreditBigDecimal = new BigDecimal(resultSet.getString(4));
                summaOper.setCredit(amountCreditBigDecimal);

            // Сборка объекта
            operacii.setRekvDoc(rekvDoc);
            operacii.setSummaOper(summaOper);

            // Сохранение в разрезе счета
            if (!opersByAcc.containsKey(accountCode))
                opersByAcc.put(accountCode, new ArrayList<ZSVResponse.SvBank.Svedenia.Operacii>());

            ((ArrayList<ZSVResponse.SvBank.Svedenia.Operacii>)opersByAcc.get(accountCode)).add(operacii);

            /*
            zsvResponse.setSvBank();

            zsvResponse.setOperdate(formatResponse.parse(resultSet.getString(1)));
            zsvResponse.s .setCode(resultSet.getString(2));
            zsvResponse.setAmountDeb(resultSet.getString(3));
            zsvResponse.setAmountCred(resultSet.getString(4));*/

                responses.add(zsvResponse);
            }
        } catch (SQLException eSQL) {
            eSQL.printStackTrace();
        } catch (ParseException eParse) {
            eParse.printStackTrace();
        } finally {
            closeHiveConnection();
            closeStatement(stmt);
            return responses;
        }

    }

}