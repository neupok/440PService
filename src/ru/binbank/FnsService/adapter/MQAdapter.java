package ru.binbank.fnsservice.adapter;

import ru.binbank.fnsservice.contracts.CITREQUEST;
import ru.binbank.fnsservice.mqservice.MQJMSReceiver;
import ru.binbank.fnsservice.mqservice.MQJMSSender;

import javax.xml.bind.JAXBException;
import java.util.Collection;


public class MQAdapter implements FnsInterface {

    private final int batchSize;
    private final String host;
    private final int port;
    private final String channel;
    private final String queueManagerName;
    private final String queueName;


    public MQAdapter(int batchSize, String host, int port, String channel, String queueManagerName, String queueName) {
        this.batchSize = batchSize;
        this.host = host;
        this.port = port;
        this.channel = channel;
        this.queueManagerName = queueManagerName;
        this.queueName = queueName;
    }

    // Возвращает коллекцию запросов
    public Collection<CITREQUEST> getCitRequests() {
        MQJMSReceiver MQReceiver = new MQJMSReceiver(batchSize, host, port,
                                                     channel, queueManagerName, queueName);
        MQReceiver.createConnection();

        // Получение запросов
        Collection<CITREQUEST> requests = null;
        try {
            requests = MQReceiver.doReceive();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        MQReceiver.closeConnection();
        return requests;
    }

    // Запись ответов
    public void writeResponses(Collection<CITREQUEST> responses) throws JAXBException {
        MQJMSSender MQSender = new MQJMSSender(host, port,
                                               channel, queueManagerName, queueName);
        MQSender.createConnection();

        MQSender.doSend(responses);

        MQSender.closeConnection();
    }
}
