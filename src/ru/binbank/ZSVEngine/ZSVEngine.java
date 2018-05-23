package ru.binbank.ZSVEngine;
//import ru.binbank.FnsService.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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


    public static void hive_connect(String str_connect, String login, String pass) throws SQLException {
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


    public static void hive_disconnect() throws SQLException {
        stmt.close();
        con.close();
    }

    // основной метод
    public static void get_result(ArrayList<ZSVRequest> col) throws SQLException {

        Date maxdate;
        Date mindate;

        // Определяем временной интервал
        ArrayList<Date> alldates = new ArrayList<Date>();

        for (int i = 0; i < col.size(); i++) {
            //alldates.add()
            alldates.set(i,            col.get(i).getOperdateBeg() );
            alldates.set(i+col.size(), col.get(i).getOperdateEnd() );
        }

        Collections.sort(alldates);
        mindate = alldates.get(0);
        maxdate = alldates.get(alldates.size() - 1);


        System.out.println(mindate); // kvd
        System.out.println(maxdate); // kvd

        // Определяем перечень счетов
        // ...

        // Формируем общий запрос
        // ...

    }


    /*
    public static void hive_query_1(String query, String path) throws SQLException {
        // query - текст запроса: "show tables in 440_p",
        // path  - путь, по которому записывать файл с результатом: "c:\\Users\\KleymenovV\\IdeaProjects\\ZSVEngine\\out\\statsTest1.txt"
        System.out.println("Running: " + query);
        ResultSet res = stmt.executeQuery(query);

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));

            while (res.next()) {
                writer.write(res.getString(1));
                writer.write("\r\n");
            }

            writer.close();

        } catch (FileNotFoundException e) {
            System.err.println("Problem opening of the file statsTest1.txt");
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest1.txt");
        }

        res.close();
    }
    */

    /*
    public static void hive_query_2(String query, String path) throws SQLException {
        // query - текст запроса: "select * from 440_p.client where idclient in (2970018041, 2970018376)",
        // path  - путь, по которому записывать файл с результатом: "c:\\Users\\KleymenovV\\IdeaProjects\\ZSVEngine\\out\\statsTest2.txt"
        System.out.println("Running: " + query);
        ResultSet res = stmt.executeQuery(query);

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));

            while (res.next()) {
                writer.write(res.getString(2));
                writer.write("\t");
                writer.write(res.getString(3));
                writer.write("\t");
                writer.write(res.getString(4));
                writer.write("\t");
                writer.write(res.getString(5));
                writer.write("\r\n");
            }

            writer.close();

        } catch (FileNotFoundException e) {
            System.err.println("Problem opening of the file statsTest2.txt");
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest2.txt");
        }

        res.close();
    }
    */

}
