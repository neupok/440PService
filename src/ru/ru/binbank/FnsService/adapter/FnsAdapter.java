package ru.ru.binbank.FnsService.adapter;

import ru.binbank.fnsservice.contracts.CITREQUEST;

import java.util.Collection;
import java.util.LinkedList;

import ru.binbank.fnsservice.utils.ConfigHandler;

public abstract class FnsAdapter implements FnsInterface {

    /*
    // Инициализация основных параметров значениями из config-файла
    @Override
    public abstract void setConfig(String configFile);
    */

    // Возвращает коллекцию запросов
    @Override
    public abstract Collection<CITREQUEST> getCitRequests(ConfigHandler configHandler);

    // Получение ответов
    /*
    @Override
    public abstract LinkedList<CITREQUEST> getResponses(Collection<CITREQUEST> citrequests);
    */

    // Запись ответов
    @Override
    public abstract void writeResponses(Collection<CITREQUEST> responses, ConfigHandler configHandler);

}
