package ru.binbank.fnsservice.utils;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.helpers.Loader;

import java.net.URL;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Класс для работы с файлом конфигурации
 *
 */
public class ConfigHandler {
    /**
     * Имя конфигурационного файла log4j
     */
    private String log4jFile;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Количество обрабатываемых за один раз запросов.
     */
    private int batchSize;

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    /**
     * Директория со входящими запросами.
     */
    private String inputDir;

    public HiveConfig getHiveConfig() {
        return hiveConfig;
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
     * Настройки hive
     */
    private HiveConfig hiveConfig;

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
        setBatchSize(Integer.parseInt(xmlConfig.getString("process." + Dictionary.BATCH_SIZE)));
        setInputDir(xmlConfig.getString("process." + Dictionary.INPUT_DIR));
        setProcessedDir(xmlConfig.getString("process." + Dictionary.PROCESSED_DIR));
        setOutputDir(xmlConfig.getString("process." + Dictionary.OUTPUT_DIR));

        // hive
        hiveConfig = new HiveConfig();
        hiveConfig.driverName = xmlConfig.getString("hive.driver_name");
        hiveConfig.connString = xmlConfig.getString("hive.connection_string");
        hiveConfig.login = xmlConfig.getString("hive.login");
        hiveConfig.password = xmlConfig.getString("hive.password");
    }

    public void setLog4jFile(String log4jFile) {
        this.log4jFile = log4jFile;
    }

    public String getLog4jFile() {
        return log4jFile;
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

