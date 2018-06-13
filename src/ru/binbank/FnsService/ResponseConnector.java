package ru.binbank.fnsservice;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import ru.binbank.fnsservice.contracts.CITREQUEST;
import ru.binbank.fnsservice.contracts.ZSVResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.Collection;

/**
 * Запись ответов
 */
public class ResponseConnector {

    private final String outputDir;
    private static final Logger log = Logger.getLogger(ResponseConnector.class);

    public ResponseConnector(String outputDir) {
        this.outputDir = outputDir;
    }

    public void writeResponses(Collection<CITREQUEST> responses)
    {
        int i = 0;
        try {
            log.info(String.format("Writing %d responses to [%s]", responses.size(), outputDir));

            JAXBContext jaxbContext = JAXBContext.newInstance(CITREQUEST.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            /* set this flag to true to format the output */
            jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );


            for (CITREQUEST r: responses) {
                jaxbMarshaller.marshal(r, new File(outputDir + "/" + RandomStringUtils.randomAlphanumeric(12) + ".xml"));
                i++;
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        finally {
            log.info(String.format("Wrote %d responses to [%s]", i, outputDir));
        }
    }
}
