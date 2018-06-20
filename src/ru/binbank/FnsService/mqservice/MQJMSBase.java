package ru.binbank.fnsservice.mqservice;

import ru.binbank.fnsservice.utils.ConfigHandler;
import com.ibm.mq.jms.*;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.xml.bind.JAXBException;

import java.util.Collection;

import static com.ibm.msg.client.wmq.compat.jms.internal.JMSC.MQJMS_TP_CLIENT_MQ_TCPIP;

public abstract class MQJMSBase {
    private String host;             //  = "msk-wscore1-dev.corp.icba.biz";
    private int port;                //  = 1415                           ;
    private String channel;          //  = "HADOOP.SVRCONN"               ;
    private String queueManagerName; //  = "CORE01.DEV.QM"                ;
    private String queueName;        //  = "HADOOP.IN"                    ;


    protected MQQueue destination = null;
    protected MQQueueSession session = null;

    protected MQQueueConnection connection = null;
    // статус выполнения приложения
    protected static int status = 1;

    public void createConnection() {
        //Создание фабрики очередей
        MQQueueConnectionFactory cf;
        try {
            cf = new MQQueueConnectionFactory();
            cf.setHostName(host);
            cf.setPort(port);
            cf.setChannel(channel);
            cf.setTransportType(MQJMS_TP_CLIENT_MQ_TCPIP);
            cf.setQueueManager(queueManagerName);

            connection  = (MQQueueConnection) cf.createQueueConnection("mqm", null);

            session     = (MQQueueSession)connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = (MQQueue)session.createQueue(queueName);

            // Старт подключения
            connection.start();

        } catch (JMSException jmsex) {
            recordFailure(jmsex);
        }
    }


    public void closeConnection() {
        try {
            if (session != null)
                session.close();
            if (connection != null)
                connection.close();
        } catch (JMSException jmsex) {
            System.err.println("" + jmsex.getMessage());
            recordFailure(jmsex);
        }

    }


    /**
     * Record this run as failure.
     * @param ex исключение
     */
    protected void recordFailure(Exception ex)
    {
        if (ex != null) {
            if (ex instanceof JMSException) {
                processJMSException((JMSException) ex);
            } else {
                System.err.println(ex);
            }
        }
        System.err.println("FAILURE");
        status = -1;
        return;
    }

    /**
     * Обработка JMSException и внутренних ошибок
     * @param jmse Exception
     */
    protected void processJMSException(JMSException jmse)
    {
        System.err.println(jmse);
        Throwable innerException = jmse.getLinkedException();
        if (innerException != null) {
            System.err.println("Inner exception :");
        }
        while (innerException != null) {
            System.err.println(innerException);
            innerException = innerException.getCause();
        }
        return;
    }

    //public abstract Collection<ru.binbank.fnsservice.contracts.CITREQUEST> doAction() throws JAXBException;

    public MQJMSBase(ConfigHandler configHandler) throws JAXBException {
        this.host = configHandler.getHost();
        this.port = configHandler.getPort();
        this.channel = configHandler.getChannel();
        this.queueManagerName = configHandler.getQueueManagerName();
        this.queueName = configHandler.getQueueName();

    }

}
