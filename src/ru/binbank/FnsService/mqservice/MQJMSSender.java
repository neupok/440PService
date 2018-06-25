package ru.binbank.fnsservice.mqservice;

import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.jms.MQQueueSender;
import ru.binbank.fnsservice.contracts.CITREQUEST;
import ru.binbank.fnsservice.mqservice.MQJMSBase;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Collection;


public class MQJMSSender extends MQJMSBase {

    private MQQueueSender sender = null;


    public MQJMSSender(String host, int port,
                       String channel, String queueManagerName, String queueNameOut) {
        super(host, port, channel, queueManagerName, queueNameOut);
        try {
            if (sender != null)
                sender.close();
        } catch (JMSException jmse) {
            recordFailure(jmse);
        }
    }


    public void doSend(Collection<CITREQUEST> responses) throws JAXBException {
        ArrayList<CITREQUEST> requests = new ArrayList<>();

        try {
            sender = (MQQueueSender)session.createSender(destination);
            JMSTextMessage message = null;

            JAXBContext jaxbContext = JAXBContext.newInstance(CITREQUEST.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            /* set this flag to true to format the output */
            jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

            for (CITREQUEST response: responses) {

                java.io.Writer writer = new StringWriter();
                jaxbMarshaller.marshal(response, writer);

                message = (JMSTextMessage) session.createTextMessage( writer.toString() );

                sender.send(message);

            }
        } catch (JMSException e) {
            status = -2;
            recordFailure(e);
        }

        try {
            if (sender != null)
                sender.close();
        } catch (JMSException jmse) {
            recordFailure(jmse);
        }

    }

}
