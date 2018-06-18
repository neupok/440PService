package ru.ru.binbank.FnsService.adapter;

import java.util.Collection;
import java.util.LinkedList;

import ru.binbank.fnsservice.utils.ConfigHandler;

public interface FnsInterface {

    // Инициализация основных параметров значениями из config-файла
    //void setConfig(String configFile);

    // Возвращает коллекцию запросов
    Collection<ru.binbank.fnsservice.contracts.CITREQUEST> getCitRequests(ConfigHandler configHandler);

    // Получение ответов
    //LinkedList<ru.binbank.fnsservice.contracts.CITREQUEST> getResponses(Collection<ru.binbank.fnsservice.contracts.CITREQUEST> citrequests);

    // Запись ответов
    void writeResponses(Collection<ru.binbank.fnsservice.contracts.CITREQUEST> responses, ConfigHandler configHandler);

}
