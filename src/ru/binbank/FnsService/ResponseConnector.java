package ru.binbank.fnsservice;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import ru.binbank.FnsService.MQService.MQJMSReceiver;
import ru.binbank.FnsService.MQService.MQJMSSender;
import ru.binbank.fnsservice.contracts.CITREQUEST;
import ru.binbank.fnsservice.contracts.ZSVResponse;
import ru.binbank.fnsservice.utils.ConfigHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.Collection;

/**
 * Запись ответов
 */
public class ResponseConnector {

    private String outputDir;

    private String host;
    private int port;
    private String channel;
    private String queueManagerName;
    private String queueName;

    private static final Logger log = Logger.getLogger(ResponseConnector.class);

    public ResponseConnector(ConfigHandler configHandler) {

        if ("file".equals(configHandler.getConfigType())) {
            this.outputDir = configHandler.getOutputDir();
        } else if ("MQ".equals(configHandler.getConfigType())) {
            this.host = configHandler.getHost();
            this.port = configHandler.getPort();
            this.channel = configHandler.getChannel();
            this.queueManagerName = configHandler.getQueueManagerName();
            this.queueName = configHandler.getQueueName();
        }

    }


    public void writeResponses(Collection<CITREQUEST> responses, ConfigHandler configHandler) throws JAXBException {
        if ("file".equals(configHandler.getConfigType())) {
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
        else if ("MQ".equals(configHandler.getConfigType())) {
            MQJMSSender MQSender = new MQJMSSender(configHandler);
            MQSender.createConnection();

            MQSender.doAction(responses);

            MQSender.closeConnection();

        }

    }
}
