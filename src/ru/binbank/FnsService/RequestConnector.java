package ru.binbank.fnsservice;

import org.apache.log4j.Logger;
import ru.binbank.FnsService.MQService.MQJMSReceiver;
import ru.binbank.fnsservice.contracts.CITREQUEST;
import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.utils.ConfigHandler;

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
    private int batchSize;
    private String inputDir;
    private String processedDir;

    private String host;
    private int port;
    private String channel;
    private String queueManagerName;
    private String queueName;

    private static final Logger log = Logger.getLogger(RequestConnector.class);

    // Соответствие запроса и файла
    private HashMap<CITREQUEST, String> requestFiles;

    /**
     *
     *
     */
    public RequestConnector(ConfigHandler configHandler) {

        requestFiles = new HashMap<>();

        if ("file".equals(configHandler.getConfigType())) {
            this.batchSize = configHandler.getBatchSize();
            this.inputDir = configHandler.getInputDir();
            this.processedDir = configHandler.getProcessedDir();
        } else if ("MQ".equals(configHandler.getConfigType())) {
            this.batchSize = configHandler.getBatchSize();
            this.host = configHandler.getHost();
            this.port = configHandler.getPort();
            this.channel = configHandler.getChannel();
            this.queueManagerName = configHandler.getQueueManagerName();
            this.queueName = configHandler.getQueueName();
        }
     }

    public Collection<CITREQUEST> fetchRequests(ConfigHandler configHandler) {
        log.info(String.format("Fetching requests from [%s] (max=%d)", inputDir, batchSize));

        Collection<CITREQUEST> requests = new ArrayList<>();
        try {
            if ("file".equals(configHandler.getConfigType())) {

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

            } else if ("MQ".equals(configHandler.getConfigType())) {

                MQJMSReceiver MQReceiver = new MQJMSReceiver(configHandler);
                MQReceiver.createConnection();

                requests = MQReceiver.doAction();

                MQReceiver.closeConnection();

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