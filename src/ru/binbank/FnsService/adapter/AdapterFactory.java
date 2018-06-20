package ru.binbank.fnsservice.adapter;

import ru.binbank.fnsservice.utils.ConfigHandler;

// Фабрика должна получить на вход все параметры, необходимые для инициализации объекта,
// и вернуть инициализированный объект
public class AdapterFactory {
    private String adapterType;

    public static FnsInterface getAdapter(ConfigHandler configHandler) {
        FnsInterface fnsAdapter = null;

        String adapterType = configHandler.getConfigType();

        if( "file".equals(adapterType) ) {
            // если на вход подали config для работы с файлами
            fnsAdapter = new FileAdapter(configHandler.getBatchSize(),
                                         configHandler.getInputDir(),
                                         configHandler.getProcessedDir());
        }
        else if( "MQ".equals(adapterType) ) {
            // если на вход подали config для работы с очередью
            fnsAdapter = new MQAdapter(configHandler.getBatchSize(),
                                       configHandler.getHost(),
                                       configHandler.getPort(),
                                       configHandler.getChannel(),
                                       configHandler.getQueueManagerName(),
                                       configHandler.getQueueName());
        }

        return (FnsInterface) fnsAdapter;
    }
}
