package ru.binbank.fnsservice;

import ru.binbank.fnsservice.contracts.ZSVRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Подключение к источнику запросов. *
 */
public class RequestConnector {
    private final int batchSize;
    private final String inputDir;
    private final String processedDir;

    // Соответствие запроса и файла
    private HashMap<ZSVRequest, String> requestFiles;

    /**
     *
     * @param batchSize
     */
    public RequestConnector(int batchSize, String inputDir, String processedDir) {
        this.batchSize = batchSize;
        requestFiles = new HashMap<>();
        this.inputDir = inputDir;
        this.processedDir = processedDir;
    }

    public Collection<ZSVRequest> fetchRequests() {
        ArrayList<ZSVRequest> requests = new ArrayList<>();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ZSVRequest.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            File dir = new File(inputDir);
            for (File f: dir.listFiles()) {
                ZSVRequest zsvRequest = (ZSVRequest)jaxbUnmarshaller.unmarshal(f);
                requests.add(zsvRequest);
                System.out.println(zsvRequest);

                // Сохранение связи запроса и файла
                requestFiles.put(zsvRequest, f.getAbsolutePath());
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public void moveToProcessedFolder(Collection<ZSVRequest> requests) {
        for (ZSVRequest r: requests) {
            if (requestFiles.containsKey(r)) {
                File f = new File(requestFiles.get(r));
                f.renameTo(new File(processedDir + f.getName()));
            }
        }
    }
}