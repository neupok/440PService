package ru.binbank.fnsservice.adapter;

import ru.binbank.fnsservice.contracts.CITREQUEST;

import javax.xml.bind.JAXBException;
import java.util.Collection;


public interface FnsInterface {

    // Возвращает коллекцию запросов
    Collection<CITREQUEST> getCitRequests();

    // Запись ответов
    void writeResponses(Collection<CITREQUEST> responses) throws JAXBException;
}
