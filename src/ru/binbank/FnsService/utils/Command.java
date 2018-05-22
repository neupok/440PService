package ru.binbank.fnsservice.utils;

import org.apache.commons.cli.*;

/**
 * Обработка команд, передаваемых из командной строки.
 */
public class Command {
    // Команды
    private static final String CONFIG_OPT = "config";

    // Значения по умолчанию
    private static final String DEFAULT_CONFIG_XML = "config.xml";

    private CommandLine line;

    public Command(String[] args) {
        // Добавление возможных опций.
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt(CONFIG_OPT)
                .withDescription("Set config file for adapter")
                .hasArg()
                .withArgName("FILE")
                .create());

        // Чтение параметров
        CommandLineParser parser = new PosixParser();
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            // todo: добавить логирование как в AdapterCommand.java
            e.printStackTrace();
        }
    }

    /**
     * Считваем название конфига  -config configName
     */
    public String getConfigOpt() {
        return line.getOptionValue(CONFIG_OPT, DEFAULT_CONFIG_XML);
    }
}
