package ru.binbank.fnsservice;

import ru.binbank.fnsservice.contracts.CITREQUEST;
import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.contracts.ZSVResponse;
import ru.binbank.fnsservice.utils.Command;
import ru.binbank.fnsservice.utils.ConfigHandler;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class FnsSrv {

    /**
     * Точка входа в программу.
     */
    public static void main(String[] args) throws DatatypeConfigurationException {
        // Разбор параметров командной строки
        Command command = new Command(args);
        String config = command.getConfigOpt();

        ConfigHandler configHandler = null;
        try {
            configHandler = new ConfigHandler(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Чтение входящих сообщений
        ru.binbank.fnsservice.RequestConnector requestConnector = new ru.binbank.fnsservice
                .RequestConnector(configHandler.getBatchSize(),
                                  configHandler.getInputDir(),
                                  configHandler.getProcessedDir());
        Collection<CITREQUEST> citrequests = requestConnector.fetchRequests();

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

        // Запись ответов
        ru.binbank.fnsservice.ResponseConnector responseConnector = new ru.binbank.fnsservice.ResponseConnector(configHandler.getOutputDir());
        responseConnector.writeResponses(responses);
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
        // // TODO: 09.06.2018 Сформировать message id
        msgid.setValue(new Long(new Random().nextLong()).toString());
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