package ru.binbank.fnsservice.adapter;

import java.util.Collection;
import ru.binbank.fnsservice.utils.ConfigHandler;

import javax.xml.bind.JAXBException;


public interface FnsInterface {

    // Возвращает коллекцию запросов
    Collection<ru.binbank.fnsservice.contracts.CITREQUEST> getCitRequests(ConfigHandler configHandler);

    // Запись ответов
    void writeResponses(Collection<ru.binbank.fnsservice.contracts.CITREQUEST> responses, ConfigHandler configHandler) throws JAXBException;

}
