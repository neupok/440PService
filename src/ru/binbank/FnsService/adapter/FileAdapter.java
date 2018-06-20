package ru.binbank.fnsservice.adapter;

import ru.binbank.fnsservice.utils.ConfigHandler;
import ru.binbank.fnsservice.RequestConnector;
import ru.binbank.fnsservice.ResponseConnector;
import ru.binbank.fnsservice.contracts.CITREQUEST;

import javax.xml.bind.JAXBException;
import java.util.*;


public class FileAdapter implements FnsInterface {

    private RequestConnector requestConnector;

    // Возвращает коллекцию запросов
    @Override
    public Collection<CITREQUEST> getCitRequests(ConfigHandler configHandler) {

        // Чтение входящих сообщений
        RequestConnector requestConnector = new ru.binbank.fnsservice.RequestConnector(configHandler);

        // Получение запросов
        Collection<CITREQUEST> citrequests = requestConnector.fetchRequests(configHandler);

        return citrequests;
    }


    // Запись ответов
    @Override
    public void writeResponses(Collection<CITREQUEST> responses, ConfigHandler configHandler) throws JAXBException {

        ResponseConnector responseConnector = new ru.binbank.fnsservice.ResponseConnector(configHandler);
        responseConnector.writeResponses(responses, configHandler);

    }

}