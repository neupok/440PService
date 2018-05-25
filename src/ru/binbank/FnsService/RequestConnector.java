package ru.binbank.fnsservice;

import ru.binbank.fnsservice.contracts.ZSVRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
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

    public Collection<ZSVRequest> fetchRequests() {
        ArrayList<ZSVRequest> requests = new ArrayList<>();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ZSVRequest.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            File dir = new File(".");
            for (File f: dir.listFiles()) {
                ZSVRequest zsvRequest = (ZSVRequest)jaxbUnmarshaller.unmarshal(f);
                requests.add(zsvRequest);
                System.out.println(zsvRequest);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return requests;
    }
}