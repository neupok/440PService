package ru.binbank.fnsservice;

import ru.binbank.fnsservice.contracts.ZSVRequest;

import java.util.Collection;

/**
 * Подключение к источнику запросов. *
 */
public class RequestConnector {
    private final int batchSize;

    /**
     *
     * @param batchSize
     */
    public RequestConnector(int batchSize) {
        this.batchSize = batchSize;

    }

    public Collection<ZSVRequest> fetchRequests()
    {
        return null;
    }
}
