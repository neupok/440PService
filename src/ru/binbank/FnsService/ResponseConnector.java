package ru.binbank.fnsservice;

import org.apache.commons.lang.RandomStringUtils;
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

    public ResponseConnector(String outputDir) {
        this.outputDir = outputDir;
    }

    public void writeResponses(Collection<CITREQUEST> responses)
    {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CITREQUEST.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            /* set this flag to true to format the output */
            jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

            for (CITREQUEST r: responses) {
                jaxbMarshaller.marshal(r, new File(outputDir + "/" + RandomStringUtils.randomAlphanumeric(12) + ".xml"));
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
