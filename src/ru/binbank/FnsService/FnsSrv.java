package ru.binbank.fnsservice;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import ru.binbank.FnsService.adapter.AdapterFactory;
import ru.binbank.FnsService.adapter.FnsInterface;
import ru.binbank.fnsservice.contracts.CITREQUEST;
import ru.binbank.fnsservice.utils.Command;
import ru.binbank.fnsservice.contracts.ZSVResponse;
import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.ru.binbank.FnsService.adapter.FnsInterface;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.*;

public class FnsSrv {
    private static final Logger log = Logger.getLogger(FnsSrv.class);

    /**
     * Точка входа в программу.
     */
    public static void main(String[] args) throws DatatypeConfigurationException, JAXBException {
        StopWatch stopWatch = new StopWatch(); stopWatch.start();

        // Разбор параметров командной строки
        Command command = new Command(args);
        String config = command.getConfigOpt();

        // Разбор параметров из config-файла:
        ru.binbank.fnsservice.utils.ConfigHandler configHandler = null;
        try {
            configHandler = new ru.binbank.fnsservice.utils.ConfigHandler(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // В зависимости от config-файла определяем адаптер (файл или очередь)
        AdapterFactory adapterFactory = new AdapterFactory(configHandler);
        FnsInterface fnsAdapter = adapterFactory.getAdapter();

        log.info("FnsSrv started");

        // Возвращаем коллекцию запросов - используем Adapter
        Collection<CITREQUEST> citrequests = fnsAdapter.getCitRequests(configHandler);

        // debug
        //requestConnector.moveToProcessedFolder(requests);
        //exit(0);

        // Получение ответов

        LinkedList<CITREQUEST> responses = new LinkedList<>();

        ru.binbank.fnsservice.ZSVEngine zsvEngine = new ru.binbank.fnsservice.ZSVEngine(configHandler.getHiveConfig());
        Collection<ZSVResponse> zsvResponses = null;
        try {
            Collection<ZSVRequest> requests = citrequests.stream().map(citrequest -> citrequest.getDATA()
                                              .getRequest()).collect(Collectors.toList());
            // Соответствие ответа и запроса
            Map<ZSVResponse, ZSVRequest> zsvResponseZSVRequestMap = new HashMap<>();
            zsvResponses = zsvEngine.getResult(requests, zsvResponseZSVRequestMap);

            // Формирование ответов с заголовками
            for (ZSVResponse zsvResponse: zsvResponses) {
                // Поиск запроса по ответу
                CITREQUEST citRequest = null;
                ZSVRequest request = zsvResponseZSVRequestMap.get(zsvResponse);
                for(CITREQUEST citrequest : citrequests)
                    if (citrequest.getDATA().getRequest() == request)
                    {
                        citRequest = citrequest;
                        break;
                    }
                // Установка в ответ ссылки на номер запроса
                //
                zsvResponse.setZapros(citRequest.getSYSTEM().getMSGID().getValue());

                CITREQUEST response = new CITREQUEST();
                response.setSYSTEM(new CITREQUEST.SYSTEM());

                fillResponseHeader(response);

                response.setDATA(new CITREQUEST.DATA());
                response.getDATA().setResponse(zsvResponse);
                responses.add(response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Запись ответов - используем Adapter
        fnsAdapter.writeResponses(responses, configHandler);

        stopWatch.stop();
        log.info(String.format("Executed in %s", stopWatch));
    }


    private static void fillResponseHeader(CITREQUEST response) {
        CITREQUEST.SYSTEM.BPID bpid = new CITREQUEST.SYSTEM.BPID();
        bpid.setValue("TAX_GET_ZSN");
        response.getSYSTEM().setBPID(bpid);

        CITREQUEST.SYSTEM.CITVersion citVersion = new CITREQUEST.SYSTEM.CITVersion();
        citVersion.setValue("1.0");
        response.getSYSTEM().setCITVersion(citVersion);

        CITREQUEST.SYSTEM.ERR err = new CITREQUEST.SYSTEM.ERR();
        err.setValue("");
        response.getSYSTEM().setERR(err);

        CITREQUEST.SYSTEM.FORMAT format = new CITREQUEST.SYSTEM.FORMAT();
        format.setValue("XML");
        response.getSYSTEM().setFORMAT(format);

        CITREQUEST.SYSTEM.INTERFACERET interfaceret = new CITREQUEST.SYSTEM.INTERFACERET();
        interfaceret.setValue("");
        response.getSYSTEM().setINTERFACERET(interfaceret);

        CITREQUEST.SYSTEM.MAINID mainid = new CITREQUEST.SYSTEM.MAINID();
        mainid.setValue("");
        response.getSYSTEM().setMAINID(mainid);

        CITREQUEST.SYSTEM.MSGID msgid = new CITREQUEST.SYSTEM.MSGID();
        msgid.setValue(new Integer(Math.abs(new Random().nextInt())).toString());
        response.getSYSTEM().setMSGID(msgid);

        CITREQUEST.SYSTEM.SYNC sync = new CITREQUEST.SYSTEM.SYNC();
        sync.setValue("N");
        response.getSYSTEM().setSYNC(sync);

        CITREQUEST.SYSTEM.SYSID sysid = new CITREQUEST.SYSTEM.SYSID();
        sysid.setValue("APHENA_FNS");
        response.getSYSTEM().setSYSID(sysid);

        CITREQUEST.SYSTEM.TARID tarid = new CITREQUEST.SYSTEM.TARID();
        tarid.setValue("FSSP_MAIN");
        response.getSYSTEM().setTARID(tarid);

        CITREQUEST.SYSTEM.Version version = new CITREQUEST.SYSTEM.Version();
        version.setValue("002");
        response.getSYSTEM().setVersion(version);
    }

}