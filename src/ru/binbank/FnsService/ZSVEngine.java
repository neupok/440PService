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
import sun.awt.image.ImageWatched;

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
                .concat(")");

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
     * @return Массив найденных ИНН
     */
    private Map<String, Long> selectExistingInn(Collection<String> inns, Long idBank) throws SQLException {
        // Формирование текста запроса
        String query = "select inn, idclient from 440_p.inn where inn in ("
                .concat(inns.stream().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(","))) // quote
                .concat(") and idbank=").concat(idBank.toString());


        // Выполнение запроса
        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Разбор результата
        HashMap<String, Long> result = new HashMap<>();

        while (resultSet.next()) {
            result.put(resultSet.getString("inn"), resultSet.getLong("idclient"));
        }

        resultSet.close();
        stmt.close();

        return result;
    }

    private Map<String, Map<String, Object> > selectAccounts(Collection<String> accCodes, Collection<Long> idClients, Long idBank) throws SQLException {
        // Формирование текста запроса
        String query = "select idacc, idclient, code from 440_p.account where code in ("
                .concat(accCodes.stream().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(","))) // quote
                .concat(") and idbank=").concat(idBank.toString()).concat("\n")
                .concat("union all\n")
                .concat("select idacc, idclient, code from 440_p.account where idclient in (")
                .concat(idClients.stream().map(aLong -> aLong.toString()).collect(Collectors.joining(",")))
                .concat(") and idbank=").concat(idBank.toString());

        // Выполнение запроса
        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Разбор результата
        HashMap<String, Map<String, Object> > result = new HashMap<>();

        while (resultSet.next()) {
            HashMap<String, Object> rowMap = new HashMap<>();
            // Заполняем значениями атрибутов
            rowMap.put("idacc", resultSet.getLong("idacc"));
            rowMap.put("idclient", resultSet.getLong("idclient"));

            result.put(resultSet.getString("code"), rowMap);
        }

        resultSet.close();
        stmt.close();

        return result;
    }

    private  Map<Long, Map<String, Object> > selectRest(Collection<Long> idAccs, Date minDate, Date maxDate, Long idBank) throws SQLException {
        // Форматируем даты
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringMindate = format.format(minDate);
        String stringMaxdate = format.format(maxDate);

        // Формирование текста запроса
        String query = "select amount, idaccount, dt from rest where idaccount in ("
                .concat(idAccs.stream().map(n -> n.toString()).collect(Collectors.joining(","))) // quote
                .concat(") and idbank=").concat(idBank.toString())
                .concat(" and dt between cast('").concat(stringMindate).concat("' as date)")
                .concat(" and cast('").concat(stringMaxdate).concat("' as date)");

        // Выполнение запроса
        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Разбор результата
        HashMap<String, Map<String, Object> > result = new HashMap<>();

        while (resultSet.next()) {
            HashMap<Long, Object> rowMap = new HashMap<>();
            // Заполняем значениями атрибутов
            rowMap.put("dt", resultSet.getDate("dt"));
            rowMap.put("amount", resultSet.getLong("amount"));

            result.put(resultSet.getLong("idaccount"), rowMap);
        }

        resultSet.close();
        stmt.close();

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
                "  from zsv_lines_parquet a" +
                " inner join ( select * from account where code in (";

        query = query.concat(requests.stream().map(x -> x.getZapnoVipis().getpoUkazannim().stream().map(y -> "'" + y.getNomSch() + "'").collect(Collectors.joining(","))).collect(Collectors.joining(",")));

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
        // Соответствие запросов и ответов
        HashMap<ZSVRequest, List<ZSVResponse> > respMap = new HashMap<>();
        // Для каждого запроса создать пустой список ответов
        for (ZSVRequest r: requests)
            respMap.put(r, new LinkedList<>());

        // Запросы, по которым еще не сформирован ответ
        Iterable<ZSVRequest> requestsToProcess;

        // Определение идентификаторов банков
        LinkedList<String> bics = new LinkedList<>();
        for (ZSVRequest r: requests) {
            bics.add(r.getZapnoVipis().getsvBank().getBIK());
        }
        Map<String, Long> banks = selectBankIdByBIC(bics);
        // Проверка, что банки найдены
        for (ZSVRequest r: requests) {
            if (!banks.containsKey(r.getZapnoVipis().getsvBank().getBIK()))
            {
                // Формирование ответа об отсутствии банка
                ZSVResponse response = new ZSVResponse();
                // TODO: 01.06.2018 формирование ответа с ошибкой    
                respMap.get(r).add(response);
            }
        }
        // Запросы, по которым уже есть ответы, не обрабатываем
        requestsToProcess = requests.stream().filter(zsvRequest -> respMap.get(zsvRequest).isEmpty()).collect(Collectors.toList());

        // TODO: 30.05.2018 сделать правильную начитку банка
        Long idBank = (Long) banks.values().toArray()[0];

        // Поиск клиентов
        LinkedList<String> inns = new LinkedList<>();
        // Обрабатываются только запросы, по которым еще нет ответа
        for (ZSVRequest r: requestsToProcess) {
            inns.add(r.getZapnoVipis().getSvPl().getPlUl().getINNUL());
        }
        Map<String, Long> existingInns = selectExistingInn(inns, idBank);
        // Разбор найденных клиентов
        for (ZSVRequest r: requestsToProcess) {
            if (!existingInns.containsKey(r.getZapnoVipis().getSvPl().getPlUl().getINNUL())) {
                // Формирование ответа об отсутствии клиента
                ZSVResponse response = new ZSVResponse();
                // TODO: 01.06.2018 формирование ответа с ошибкой
                respMap.get(r).add(response);
            }
        }
        // Запросы, по которым уже есть ответы, не обрабатываем
        requestsToProcess = requests.stream().filter(zsvRequest -> respMap.get(zsvRequest).isEmpty()).collect(Collectors.toList());

        // Клиентов, по которым пришел запрос по всем счетам, выделяем в отдельный список
        LinkedList<Long> allAccsClients = new LinkedList<>();
        for (ZSVRequest r: requestsToProcess) {
            if (r.getZapnoVipis().getpoVsem() != null)
                allAccsClients.add(existingInns.get(r.getZapnoVipis().getSvPl().getPlUl().getINNUL()));
        }

        // Поиск счетов
        LinkedList<String> accCodes = new LinkedList<>();
        for (ZSVRequest r: requestsToProcess) {
            for (ZSVRequest.ZapnoVipis.poUkazannim poUkazannim: r.getZapnoVipis().getpoUkazannim()) {
                accCodes.add(poUkazannim.getNomSch());
            }
        }
        Map<String, Map<String, Object> > accounts = selectAccounts(accCodes, allAccsClients, idBank);
        // Разбор найденных счетов
        for (ZSVRequest r: requestsToProcess) {
            // Если запрос "по всем" счетам, то поиск соответствия делать не надо
            if (r.getZapnoVipis().getpoVsem() != null) {
                // Если по запрошенному счету счет не найден, то нужно делать отдельный ответ с кодом 42 (счет не найден)
                for (ZSVRequest.ZapnoVipis.poUkazannim poUkazannim: r.getZapnoVipis().getpoUkazannim()) {
                    if (!accounts.containsKey(poUkazannim.getNomSch())) {
                        // Запрошенный счет не найден. Формируем ответ
                        ZSVResponse response = new ZSVResponse();
                        // TODO: 01.06.2018 Добавить формирование ответа
                        respMap.get(r).add(response);
                    }
                }
            }
        }

        // Запрос остатков
        // Сбор всех запрашиваемых счетов в список
        LinkedList<Long> idAccs = new LinkedList<>();
        for (Map<String, Object> val: accounts.values()) {
            idAccs.add((Long) val.get("idacc"));
        }
        // Определение минимальной и максимальной дат
        LinkedList<Date> datesFrom = new LinkedList<>();
        LinkedList<Date> datesTo = new LinkedList<>();
        for (ZSVRequest r: requests) {
            ZSVRequest.ZapnoVipis.ZaPeriod zaPeriod = r.getZapnoVipis().getzaPeriod();
            datesFrom.add(zaPeriod.getDateBeg().toGregorianCalendar().getTime());
            datesTo.add(zaPeriod.getDateEnd().toGregorianCalendar().getTime());
        }
        Date minDate = datesFrom.stream().min(Date::compareTo).get();
        Date maxDate = datesFrom.stream().max(Date::compareTo).get();

        Map<Long, Map<String, Object> > rest = selectRest(idAccs, minDate, maxDate, idBank);

        // TODO: 30.05.2018 Запрос операций


        // Все, что ниже - старое


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