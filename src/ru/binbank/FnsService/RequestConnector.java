package ru.binbank.fnsservice;

import org.apache.log4j.Logger;
import ru.binbank.fnsservice.contracts.CITREQUEST;
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

    private static final Logger log = Logger.getLogger(RequestConnector.class);

    // Соответствие запроса и файла
    private HashMap<CITREQUEST, String> requestFiles;

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

    public Collection<CITREQUEST> fetchRequests() {
        log.info(String.format("Fetching requests from [%s] (max=%d)", inputDir, batchSize));

        ArrayList<CITREQUEST> requests = new ArrayList<>();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CITREQUEST.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            int i = 0; // счетчик
            File dir = new File(inputDir);
            for (File f: dir.listFiles()) {
                CITREQUEST citrequest = (CITREQUEST) jaxbUnmarshaller.unmarshal(f);
                requests.add(citrequest);
                // Сохранение связи запроса и файла
                requestFiles.put(citrequest, f.getAbsolutePath());
                // Если достигнут лимит пакета, то выход
                if (++i >= batchSize)
                    break;
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        log.info(String.format("Fetched %d requests", requests.size()));

        return requests;
    }

    public void moveToProcessedFolder(Collection<CITREQUEST> requests) {
        for (CITREQUEST r: requests) {
            if (requestFiles.containsKey(r)) {
                File f = new File(requestFiles.get(r));
                String newFileName = processedDir + "/" + f.getName();
                f.renameTo(new File(newFileName));
            }
        }
    }
}