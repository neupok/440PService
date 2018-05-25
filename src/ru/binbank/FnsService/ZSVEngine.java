package ru.binbank.fnsservice;
//import ru.binbank.FnsService.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.contracts.ZSVResponse;


public class ZSVEngine {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static Connection hiveConnection;
    private static Statement stmt;


    public static void hiveConnect(String str_connect, String login, String pass) throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        hiveConnection = DriverManager.getConnection(
                str_connect, // строка соединения "jdbc:hive2://msk-hadoop01:10000/default"
                login,       // логин "root"
                pass         // пароль "GoodPwd1234"
                //"jdbc:hive2://msk-hadoop01:10000/default", "root", "GoodPwd1234"
        );
        stmt = hiveConnection.createStatement();
    }


    public static void hiveDisconnect() throws SQLException {
        stmt.close();
        hiveConnection.close();
    }

    /**
     * Формирование текста запроса
     * @param requests
     * @throws SQLException
     */
    public String hiveQuery(Collection<ZSVRequest> requests) {

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

        for (Iterator itRequests = requests.iterator(); itRequests.hasNext(); ) {
            ZSVRequest objectRequests = (ZSVRequest)itRequests.next();

            for (Iterator itPoUkazannim = objectRequests.getZapnoVipis().getpoUkazannim().iterator(); itPoUkazannim.hasNext(); ) {
                ZSVRequest.ZapnoVipis.poUkazannim objectPoUkazannim = (ZSVRequest.ZapnoVipis.poUkazannim)itPoUkazannim.next();
                query = query + "'" + objectPoUkazannim.getNomSch() + "'";

                if (itPoUkazannim.hasNext()) { query = query + ", "; } else { query = query + ")"; };

                //if (j != element.getSelectedAccounts().size()-1) { query = query + ", "; }
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
    public Collection<ZSVResponse> getResult(Collection<ZSVRequest> requests) throws SQLException {

        // Формировапние текста запроса
        String hiveQuery = hiveQuery(requests);
        System.out.println(hiveQuery); // kvd

        hiveConnect("jdbc:hive2://msk-hadoop01:10000/default", "root", "GoodPwd1234");

        ResultSet resultSet = stmt.executeQuery(hiveQuery);

        hiveDisconnect();

        // Заполнение массива строками результата
        ArrayList<ZSVResponse> answer = new ArrayList<>();

        return answer;
    }

}