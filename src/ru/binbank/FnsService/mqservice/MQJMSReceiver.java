package ru.binbank.fnsservice.mqservice;

import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.jms.MQQueueReceiver;
import ru.binbank.fnsservice.contracts.CITREQUEST;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;


public class MQJMSReceiver extends ru.binbank.fnsservice.mqservice.MQJMSBase {
    private final int batchSize;
    private final int timeout  = 3000;
    private MQQueueReceiver receiver = null;

    public Collection<CITREQUEST> doReceive() throws JAXBException {

        JMSTextMessage message;
        ArrayList<CITREQUEST> requests = new ArrayList<>();

        JAXBContext jaxbContext = JAXBContext.newInstance(CITREQUEST.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        int i = 0; // счетчик

        try {
            receiver = (MQQueueReceiver)session.createReceiver(destination);
            do {
                message = (JMSTextMessage)receiver.receive(timeout);
                if (message != null) {

                    java.io.Reader reader = new StringReader(message.getText());

                    CITREQUEST citrequest = (CITREQUEST) jaxbUnmarshaller.unmarshal( reader );
                    requests.add(citrequest);

                    if (++i >= batchSize)
                        break;

                }
            } while (message != null);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        try {
            if (receiver != null)
                receiver.close();
        } catch (JMSException jmsex) {
            recordFailure(jmsex);
        }

        return requests;
    }

    public MQJMSReceiver(int batchSize, String host, int port,
                         String channel, String queueManagerName, String queueName) {

        super(host, port, channel, queueManagerName, queueName);

        this.batchSize = batchSize;
    }

}
