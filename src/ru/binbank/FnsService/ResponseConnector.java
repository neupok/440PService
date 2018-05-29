package ru.binbank.fnsservice;

import ru.binbank.fnsservice.contracts.ZSVResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.Collection;

/**
 * Запись ответов
 */
public class ResponseConnector {

    public ResponseConnector() {
    }

    public void writeResponses(Collection<ZSVResponse> responses)
    {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ZSVResponse.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            /* set this flag to true to format the output */
    //        jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            /* marshaling of java objects in xml (output to file and standard output) */
    //        jaxbMarshaller.marshal( spain, new File( "country.xml" ) );

            for (ZSVResponse r: responses) {
                jaxbMarshaller.marshal(r, System.out );
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
