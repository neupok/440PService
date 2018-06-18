package ru.ru.binbank.FnsService.adapter;

import ru.binbank.fnsservice.contracts.CITREQUEST;

import java.util.Collection;
import java.util.LinkedList;

import ru.binbank.fnsservice.utils.ConfigHandler;

//public class MQAdapter extends FnsAdapter {
public class MQAdapter extends FnsAdapter implements FnsInterface {

    /*
    // Инициализация основных параметров значениями из config-файла
    @Override
    public void setConfig(String configFile) {

    }
    */

    // Возвращает коллекцию запросов
    @Override
    public Collection<CITREQUEST> getCitRequests(ConfigHandler configHandler) {
        //TODO: нужна реализация класса RequestConnectorMQ
        return null;
    }

    /*
    // Получение ответов
    @Override
    public LinkedList<CITREQUEST> getResponses(Collection<CITREQUEST> citrequests) {
        return null;
    }
    */

    // Запись ответов
    @Override
    public void writeResponses(Collection<CITREQUEST> responses, ConfigHandler configHandler) {
        //TODO: нужна реализация класса ResponseConnectorMQ
    }
}
