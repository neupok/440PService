package ru.binbank.fnsservice.adapter;

// Фабрика должна получить на вход все параметры, необходимые для инициализации объекта,
// и вернуть инициализированный объект
public class AdapterFactory {
    private String adapterType;

    public AdapterFactory(ru.binbank.fnsservice.utils.ConfigHandler configHandler) {
        adapterType = configHandler.getConfigType();
    }

    public FnsInterface getAdapter() {

        FnsInterface fnsAdapter = null;

        if( "file".equals(adapterType) ) {
            // если на вход подали config для работы с файлами
            fnsAdapter = new FileAdapter();
        }
        else if( "MQ".equals(adapterType) ) {
            // если на вход подали config для работы с очередью
            fnsAdapter = new MQAdapter();
        }

        return (FnsInterface) fnsAdapter;
    }
}
