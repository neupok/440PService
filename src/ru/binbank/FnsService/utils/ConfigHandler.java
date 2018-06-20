package ru.binbank.fnsservice.utils;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.helpers.Loader;

import java.net.URL;

/**
 * Класс для работы с файлом конфигурации
 *
 */
public class ConfigHandler {

    /**
     * Тип config-файла (для файлов или для очередей)
     */
    private String configType;

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }


    /**
     * Имя конфигурационного файла log4j
     */
    private String log4jFile;

    public void setLog4jFile(String log4jFile) {
        this.log4jFile = log4jFile;
    }

    public String getLog4jFile() {
        return log4jFile;
    }


    /**
     * Количество обрабатываемых за один раз запросов.
     */
    private int batchSize;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }


    /**
     * Директория со входящими запросами.
     */
    private String inputDir;

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }


    /**
     * Директория для ответов.
     */
    private String outputDir;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Директория, в которую перемещаются обработанные файлы
     */
    private String processedDir;

    public String getProcessedDir() {
        return processedDir;
    }

    public void setProcessedDir(String processedDir) {
        this.processedDir = processedDir;
    }


    /**
     * Название класса JDBC-драйвера.
     */
    private String jdbcDriverName;

    public String getJdbcDriverName() {
        return jdbcDriverName;
    }

    public void setJdbcDriverName(String jdbcDriverName) {
        this.jdbcDriverName = jdbcDriverName;
    }


    /**
     * Название хоста для MQ
     */
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    /**
     * Номер порта для MQ
     */
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    /**
     * Имя канала для MQ
     */
    private String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


    /**
     * Имя менеджера для MQ
     */
    private String queueManagerName;

    public String getQueueManagerName() {
        return queueManagerName;
    }

    public void setQueueManagerName(String queueManagerName) {
        this.queueManagerName = queueManagerName;
    }


    /**
     * Имя очереди для MQ
     */
    private String queueName;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }


    /**
     * Настройки hive
     */
    private HiveConfig hiveConfig;

    public HiveConfig getHiveConfig() {
        return hiveConfig;
    }


    /**
     * Класс работы с конфигом
     */
    protected XMLConfiguration xmlConfig;

    public ConfigHandler(String configFileName) throws Exception {
        if (configFileName == null)
            throw new Exception("Unknown config file");

        // Считаем настройки из конфига
        AbstractConfiguration.setDefaultListDelimiter(';');
        xmlConfig = new XMLConfiguration();
        xmlConfig.setDelimiterParsingDisabled(true);
        xmlConfig.setAttributeSplittingDisabled(true);
        xmlConfig.load(configFileName);

        // Конфигурирование log4j
        String log4j = xmlConfig.getString("log4j");
        if (log4j != null) {
            URL resouse = Loader.getResource(log4j);
            setLog4jFile(log4j);
        }

        // Чтение параметров
        setConfigType(xmlConfig.getString("adapter." + ru.binbank.fnsservice.utils.Dictionary.CONFIG_TYPE));

        if ("file".equals(configType)) {
            setBatchSize(Integer.parseInt(xmlConfig.getString("process." + ru.binbank.fnsservice.utils.Dictionary.BATCH_SIZE)));
            setInputDir(xmlConfig.getString("process." + ru.binbank.fnsservice.utils.Dictionary.INPUT_DIR));
            setProcessedDir(xmlConfig.getString("process." + ru.binbank.fnsservice.utils.Dictionary.PROCESSED_DIR));
            setOutputDir(xmlConfig.getString("process." + ru.binbank.fnsservice.utils.Dictionary.OUTPUT_DIR));
        }
        else if("MQ".equals(configType)) {
            setHost(xmlConfig.getString("queue." + ru.binbank.fnsservice.utils.Dictionary.HOST));
            setPort(xmlConfig.getInt("queue." + ru.binbank.fnsservice.utils.Dictionary.PORT));
            setChannel(xmlConfig.getString("queue." + ru.binbank.fnsservice.utils.Dictionary.CHANNEL));
            setQueueManagerName(xmlConfig.getString("queue." + ru.binbank.fnsservice.utils.Dictionary.QUEUE_MANAGER_NAME));
            setQueueName(xmlConfig.getString("queue." + ru.binbank.fnsservice.utils.Dictionary.QUEUE_NAME));
        }

        // hive
        hiveConfig = new HiveConfig();
        hiveConfig.driverName = xmlConfig.getString("hive.driver_name");
        hiveConfig.connString = xmlConfig.getString("hive.connection_string");
        hiveConfig.login = xmlConfig.getString("hive.login");
        hiveConfig.password = xmlConfig.getString("hive.password");
    }


    /**
     * Настройки hive
     */
    public class HiveConfig {
        public String connString;
        public String login;
        public String password;
        public String driverName;
    }
}

