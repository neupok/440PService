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
        setBatchSize(Integer.parseInt(xmlConfig.getString(Dictionary.BATCH_SIZE)));

    }
}

