package ru.ru.binbank.FnsService.adapter;

import ru.binbank.fnsservice.utils.ConfigHandler;
import ru.binbank.fnsservice.RequestConnector;
import ru.binbank.fnsservice.contracts.CITREQUEST;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import ru.binbank.fnsservice.utils.ConfigHandler;

public class FileAdapter extends FnsAdapter implements FnsInterface {

    private RequestConnector requestConnector;
    //private ConfigHandler configHandler;

    /*
    // Инициализация основных параметров значениями из config-файла
    @Override
    public void setConfig(String configFile) {

        configHandler = null;
        try {
            configHandler = new ru.binbank.fnsservice.utils.ConfigHandler(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */

    // Возвращает коллекцию запросов
    @Override
    public Collection<CITREQUEST> getCitRequests(ConfigHandler configHandler) {

        // Чтение входящих сообщений
        ru.binbank.fnsservice.RequestConnector requestConnector = new ru.binbank.fnsservice
                .RequestConnector(
                configHandler.getBatchSize(),
                configHandler.getInputDir(),
                configHandler.getProcessedDir()
                );

        // Получение запросов
        Collection<ru.binbank.fnsservice.contracts.CITREQUEST> citrequests = requestConnector.fetchRequests();

        return citrequests;
    }



    /*
    // Получение ответов
    @Override
    public LinkedList<CITREQUEST> getResponses(Collection<CITREQUEST> citrequests) {

        LinkedList<ru.binbank.fnsservice.contracts.CITREQUEST> responses = new LinkedList<>();

        ru.binbank.fnsservice.ZSVEngine zsvEngine = new ru.binbank.fnsservice.ZSVEngine(configHandler.getHiveConfig());
        Collection<ru.binbank.fnsservice.contracts.ZSVResponse> zsvResponses = null;

        try {
            Collection<ru.binbank.fnsservice.contracts.ZSVRequest> requests = citrequests.stream().map(citrequest -> citrequest.getDATA()
                    .getRequest()).collect(Collectors.toList());
            // Соответствие ответа и запроса
            Map<ru.binbank.fnsservice.contracts.ZSVResponse, ru.binbank.fnsservice.contracts.ZSVRequest> zsvResponseZSVRequestMap = new HashMap<>();
            zsvResponses = zsvEngine.getResult(requests, zsvResponseZSVRequestMap);

            // Формирование ответов с заголовками
            for (ru.binbank.fnsservice.contracts.ZSVResponse zsvResponse: zsvResponses) {
                // Поиск запроса по ответу
                ru.binbank.fnsservice.contracts.CITREQUEST citRequest = null;
                ru.binbank.fnsservice.contracts.ZSVRequest request = zsvResponseZSVRequestMap.get(zsvResponse);
                for(ru.binbank.fnsservice.contracts.CITREQUEST citrequest : citrequests)
                    if (citrequest.getDATA().getRequest() == request)
                    {
                        citRequest = citrequest;
                        break;
                    }
                // Установка в ответ ссылки на номер запроса
                //
                zsvResponse.setZapros(citRequest.getSYSTEM().getMSGID().getValue());

                ru.binbank.fnsservice.contracts.CITREQUEST response = new ru.binbank.fnsservice.contracts.CITREQUEST();
                response.setSYSTEM(new ru.binbank.fnsservice.contracts.CITREQUEST.SYSTEM());

                fillResponseHeader(response);

                response.setDATA(new ru.binbank.fnsservice.contracts.CITREQUEST.DATA());
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

        return responses;

    }
    */

    // Запись ответов
    @Override
    //public void writeResponses(Collection<ru.binbank.fnsservice.contracts.CITREQUEST> responses) {
    public void writeResponses(Collection<CITREQUEST> responses, ConfigHandler configHandler) {

        ru.binbank.fnsservice.ResponseConnector responseConnector = new ru.binbank.fnsservice.ResponseConnector(configHandler.getOutputDir());
        responseConnector.writeResponses(responses);

    }

    /*
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
    */
}
