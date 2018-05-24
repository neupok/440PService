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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.google.common.primitives.Longs;
import ru.binbank.ZSVRequest.ZSVRequest;


public class ZSVEngine {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static Connection con;
    private static Statement stmt;


    public static void hiveConnect(String str_connect, String login, String pass) throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        con = DriverManager.getConnection(
                str_connect, // строка соединения "jdbc:hive2://msk-hadoop01:10000/default"
                login,       // логин "root"
                pass         // пароль "GoodPwd1234"
                //"jdbc:hive2://msk-hadoop01:10000/default", "root", "GoodPwd1234"
                );
        stmt = con.createStatement();
    }


    public static void hiveDisconnect() throws SQLException {
        stmt.close();
        con.close();
    }

    /**
     * Получение ответов на запросы ФНС.
     * @param requests
     * @throws SQLException
     */
    public static void getResult(ArrayList<ZSVRequest> requests) throws SQLException {

        Date maxdate;
        Date mindate;

        // Определяем временной интервал
        ArrayList<Date> alldates = new ArrayList<Date>();

        for (int i = 0; i < requests.size(); i++) {
            alldates.add(requests.get(i).getOperdateBeg());
            alldates.add(requests.get(i).getOperdateEnd());
        }

        mindate = Collections.min(alldates);
        maxdate = Collections.max(alldates);

        // Форматируем даты
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringMindate = format.format(mindate);
        String stringMaxdate = format.format(maxdate);

        System.out.println(mindate); // kvd
        System.out.println(maxdate); // kvd

        System.out.println(stringMindate); // kvd
        System.out.println(stringMaxdate); // kvd

        // Формируем общий запрос
        String query = "select a.dtoperdate, b.code, a.amountdeb, a.amountcre" +
                       "  from 440_p.zsv_lines_parquet a" +
                       " inner join ( select * from 440_p.account where code in (";

        for (int i = 0; i < requests.size(); i++) {
            for (int j = 0; j < requests.get(i).getSelectedAccounts().size() ; j++) {
                query = query + "'" + requests.get(i).getSelectedAccounts().get(j) + "'";
                if (j != requests.get(i).getSelectedAccounts().size()-1) { query = query + ", "; }
            }
            if (i != requests.size()-1) { query = query + ", "; } else { query = query + ")"; };
        }

        query = query + ") b" +
                        "   on a.idaccount = b.idacc and" +
                        "      a.idbank = b.idbank " +
                        "where a.dtoperdate between cast ('" + stringMindate + "' as date) and cast ('" + stringMaxdate + "' as date)";

        System.out.println(query); // kvd
    }

}
