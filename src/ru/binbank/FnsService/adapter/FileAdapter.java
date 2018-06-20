package ru.binbank.fnsservice.adapter;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import ru.binbank.fnsservice.contracts.CITREQUEST;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class FileAdapter implements FnsInterface {
    private final int batchSize;
    private final String inputDir;
    private final String processedDir;

    private static final Logger log = Logger.getLogger(FileAdapter.class);

    // Соответствие запроса и файла
    private HashMap<CITREQUEST, String> requestFiles;


    public FileAdapter(int batchSize, String inputDir, String processedDir) {
        this.batchSize = batchSize;
        this.inputDir = inputDir;
        this.processedDir = processedDir;
    }

    // Возвращает коллекцию запросов
    public Collection<CITREQUEST> getCitRequests() {
        log.info(String.format("Fetching requests from [%s] (max=%d)", inputDir, batchSize));

        requestFiles = new HashMap<>();

        Collection<CITREQUEST> requests = new ArrayList<>();
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

    // Запись ответов
    public void writeResponses(Collection<CITREQUEST> responses) throws JAXBException {
        int i = 0;
        try {
            log.info(String.format("Writing %d responses to [%s]", responses.size(), processedDir));

            JAXBContext jaxbContext = JAXBContext.newInstance(CITREQUEST.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            /* set this flag to true to format the output */
            jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );


            for (CITREQUEST r: responses) {
                jaxbMarshaller.marshal(r, new File(processedDir + "/" + RandomStringUtils.randomAlphanumeric(12) + ".xml"));
                i++;
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        finally {
            log.info(String.format("Wrote %d responses to [%s]", i, processedDir));
        }
    }

}