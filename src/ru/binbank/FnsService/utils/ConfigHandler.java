package ru.binbank.fnsservice.utils;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Класс для работы с файлом конфигурации
 *
 */
public class ConfigHandler {

    /**
     * Класс работы с конфигом
     */
    protected XMLConfiguration xmlConfig;

    public ConfigHandler(String configFileName) throws Exception {
        if (configFileName == null)
            throw new Exception("Unknown config file");

        //считаем настройки из конфига
        AbstractConfiguration.setDefaultListDelimiter(';');
        //xmlConfig = new XMLConfiguration(configFileName);
        xmlConfig = new XMLConfiguration();
        xmlConfig.setDelimiterParsingDisabled(true);
        xmlConfig.setAttributeSplittingDisabled(true);
        xmlConfig.load(configFileName);

    }
}

