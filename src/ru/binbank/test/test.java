package ru.binbank.test;

//import ru.binbank.fnsservice.contracts.*;

//import ru.binbank.FnsService.contracts.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
//import java.util.Date;

import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.contracts.ZSVResponse;


public class test {
    private static ru.binbank.fnsservice.contracts.ZSVRequest zsvRequest1;
    private static ru.binbank.fnsservice.contracts.ZSVRequest zsvRequest2;

    /**
     * Точка входа в программу.
     */
    public static void main(String[] args) throws SQLException, ParseException, DatatypeConfigurationException, ClassNotFoundException {
        // Разбор параметров командной строки
        //ru.binbank.fnsservice.utils.Command command = new ru.binbank.fnsservice.utils.Command(args);

        // Инициализируем объект класса
        ru.binbank.fnsservice.ZSVEngine zsvEngine = new ru.binbank.fnsservice.ZSVEngine( "jdbc:hive2://msk-hadoop01:10000/default",  // строка соединения
                                                                                         "root",                                    // логин
                                                                                         "GoodPwd1234");                            // пароль

        // Формат даты
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy hh:mm:ss");


        /***********************************************************************************
        ****************                   Запрос 1                *************************
        ***********************************************************************************/

        zsvRequest1 = new ru.binbank.fnsservice.contracts.ZSVRequest();

        // Заполнение счетов (Запрос 1):
        ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis ZapnoVipis1 = new ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis();
        ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.poUkazannim poUkazannim1 = new ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.poUkazannim();
        poUkazannim1.setNomSch("40702810600070039067");

        zsvRequest1.setZapnoVipis(ZapnoVipis1);
        zsvRequest1.getZapnoVipis().getpoUkazannim().add(poUkazannim1);

        // Объект класса ZaPeriod (для дат)
        ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.ZaPeriod ZaPeriod1 = new ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.ZaPeriod(); /////
        zsvRequest1.getZapnoVipis().setzaPeriod(ZaPeriod1); /////

        // Дата начала интервала (Запрос 1):
        String date1Beg = "07.12.2008 00:00:00";
        Date operdate1Beg = format.parse(date1Beg);

        GregorianCalendar operdate1Beg_Greg = new GregorianCalendar();
        operdate1Beg_Greg.setTime(operdate1Beg);

        javax.xml.datatype.XMLGregorianCalendar operdate1Beg_XML = DatatypeFactory.newInstance().newXMLGregorianCalendar(operdate1Beg_Greg); //null; // = new javax.xml.datatype.XMLGregorianCalendar();
        zsvRequest1.getZapnoVipis().getzaPeriod().setDateBeg(operdate1Beg_XML);

        // Дата окончания интервала (Запрос 1):
        String date1End = "09.12.2008 00:00:00";
        Date operdate1End = format.parse(date1End);

        GregorianCalendar operdate1End_Greg = new GregorianCalendar();
        operdate1End_Greg.setTime(operdate1End);

        javax.xml.datatype.XMLGregorianCalendar operdate1End_XML = DatatypeFactory.newInstance().newXMLGregorianCalendar(operdate1End_Greg);
        zsvRequest1.getZapnoVipis().getzaPeriod().setDateEnd(operdate1End_XML);

        /***********************************************************************************
        ****************                   Запрос 2                *************************
        ***********************************************************************************/

        zsvRequest2 = new ru.binbank.fnsservice.contracts.ZSVRequest();

        // Заполнение счетов (Запрос 2)
        ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis ZapnoVipis2 = new ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis();
        ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.poUkazannim poUkazannim2 = new ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.poUkazannim();
        poUkazannim2.setNomSch("40702810700120033363");

        zsvRequest2.setZapnoVipis(ZapnoVipis2);
        zsvRequest2.getZapnoVipis().getpoUkazannim().add(poUkazannim2);

        // Объект класса ZaPeriod (для дат)
        ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.ZaPeriod ZaPeriod2 = new ru.binbank.fnsservice.contracts.ZSVRequest.ZapnoVipis.ZaPeriod(); /////
        zsvRequest2.getZapnoVipis().setzaPeriod(ZaPeriod2);

        // Дата начала интервала (Запрос 2)
        String date2Beg = "08.12.2008 00:00:00";
        Date operdate2Beg = format.parse(date2Beg);

        GregorianCalendar operdate2Beg_Greg = new GregorianCalendar();
        operdate2Beg_Greg.setTime(operdate2Beg);

        javax.xml.datatype.XMLGregorianCalendar operdate2Beg_XML = DatatypeFactory.newInstance().newXMLGregorianCalendar(operdate2Beg_Greg);
        zsvRequest2.getZapnoVipis().getzaPeriod().setDateBeg(operdate2Beg_XML);

        // Дата окончания интервала (Запрос 2)
        String date2End = "11.12.2008 00:00:00";
        Date operdate2End = format.parse(date2End);

        GregorianCalendar operdate2End_Greg = new GregorianCalendar();
        operdate2End_Greg.setTime(operdate2End);

        javax.xml.datatype.XMLGregorianCalendar operdate2End_XML = DatatypeFactory.newInstance().newXMLGregorianCalendar(operdate2End_Greg);
        zsvRequest2.getZapnoVipis().getzaPeriod().setDateEnd(operdate2End_XML);


        /***********************************************************************************
        ***************************   Формируем коллекцию запросов   ***********************
        ***********************************************************************************/

        ArrayList<ru.binbank.fnsservice.contracts.ZSVRequest> requests = new ArrayList<ru.binbank.fnsservice.contracts.ZSVRequest>();

        requests.add(zsvRequest1);
        requests.add(zsvRequest2);


        /***********************************************************************************
        ***************************           Вызов метода           ***********************
        ***********************************************************************************/

        Collection<ZSVResponse> responses = zsvEngine.getResult(requests);


        /***********************************************************************************
        *********************           Запись результата в файл           *****************
        ***********************************************************************************/
        /*
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\Users\\KleymenovV\\IdeaProjects\\440PService\\out\\Test_out.txt"), StandardCharsets.UTF_8));

            for (Iterator itResponses = responses.iterator(); itResponses.hasNext();) {
                ZSVResponse objectResponse = (ZSVResponse)itResponses.next();

                writer.write(format.format(objectResponse.getSvBank().getSvedenia().getOperacii(). .getOperdate()));
                writer.write("\t");

                writer.write(objectResponse.getCode() );
                writer.write("\t");

                writer.write(objectResponse.getAmountDeb() );
                writer.write("\t");

                writer.write(objectResponse.getAmountCred() );
                writer.write("\r\n");
            }

        } catch (FileNotFoundException e) {
            System.err.println("Problem opening of the file statsTest1.txt");
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest1.txt");
        }
        */

    }
}
