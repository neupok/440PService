package ru.binbank.fnsservice;

import ru.binbank.fnsservice.utils.Command;
import ru.binbank.ZSVRequest.ZSVRequest;
//import ru.binbank.FnsService.utils. ; //  .fnsservice.ZSVEngin;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class FnsSrv {

    /**
     * Точка входа в программу.
     */
    public static void main(String[] args) throws SQLException, ParseException {
        // Разбор параметров командной строки
        Command command = new Command(args);

        // kvd
        ru.binbank.fnsservice.ZSVEngine zsvEngine = new ru.binbank.fnsservice.ZSVEngine();

        zsvEngine.hiveConnect(
                "jdbc:hive2://msk-hadoop01:10000/default",
                "root",
                "GoodPwd1234"
        );


        //zsvEngine.hive_query_1( "show tables in 440_p", "c:\\Users\\KleymenovV\\IdeaProjects\\440PService\\out\\statsTest1.txt");

        // для даты
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy hh:mm:ss");

        /*
        ****************   Запрос 1   *************************
        */
        ZSVRequest zsvRequest1 = new ZSVRequest();

        zsvRequest1.setClientINN("5408117935");

        // Заполнение счетов
        ArrayList<String> selectedAccounts1 = new ArrayList<String>();
        selectedAccounts1.add("40702810600070039067"); //zsvRequest1.setCode("40702810600070039067"); // idaccount = 43725172
        zsvRequest1.setSelectedAccounts(selectedAccounts1);

        // дата начала интервала
        String date1Beg = "07.12.2008 00:00:00";
        Date operdate1Beg = format.parse(date1Beg);
        zsvRequest1.setOperdateBeg(operdate1Beg);

        // дата окончания интервала
        String date1End = "09.12.2008 00:00:00";
        Date operdate1End = format.parse(date1End);
        zsvRequest1.setOperdateEnd(operdate1End);

        /*
        ****************   Запрос 2   *************************
        */
        ZSVRequest zsvRequest2 = new ZSVRequest();

        zsvRequest2.setClientINN("5408117935");

        // Заполнение счетов
        ArrayList<String> selectedAccounts2 = new ArrayList<String>();
        selectedAccounts2.add("40702810700120033363"); //zsvRequest2.setCode("40702810700120033363"); // idaccount = 409289014
        zsvRequest2.setSelectedAccounts(selectedAccounts2);

        String date2Beg = "08.12.2008 00:00:00";

        Date operdate2Beg = format.parse(date2Beg);

        zsvRequest2.setOperdateBeg(operdate2Beg);

        String date2End = "11.12.2008 00:00:00";
        Date operdate2End = format.parse(date2End);
        zsvRequest2.setOperdateEnd(operdate2End);

        // Формируем коллекцию запросов
        ArrayList<ZSVRequest> requests = new ArrayList<ZSVRequest>();

        requests.add(zsvRequest1);
        requests.add(zsvRequest2);

        zsvEngine.getResult(requests);


        zsvEngine.hiveDisconnect();

    }
}