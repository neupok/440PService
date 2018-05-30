package ru.binbank.fnsservice.utils;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Класс для работы с файлом конфигурации
 *
 */
public class ConfigHandler {

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

        // Чтение параметров
        setBatchSize(Integer.parseInt(xmlConfig.getString("process." + Dictionary.BATCH_SIZE)));
        setInputDir(xmlConfig.getString("process." + Dictionary.INPUT_DIR));
        // hive
        HiveConfig hiveConfig = new HiveConfig();
        hiveConfig.connString = xmlConfig.getString("hive.connection_string");
        hiveConfig.login = xmlConfig.getString("hive.login");
        hiveConfig.password = xmlConfig.getString("hive.password");
    }

    /**
     * Настройки hive
     */
    private class HiveConfig {
        public String connString;
        public String login;
        public String password;
    }
}

