package ru.ru.binbank.FnsService.adapter;


import ru.ru.binbank.FnsService.adapter.FileAdapter;
import ru.ru.binbank.FnsService.adapter.FnsAdapter;
import ru.ru.binbank.FnsService.adapter.MQAdapter;

// Фабрика должна получить на вход все параметры, необходимые для инициализации объекта,
// а вернуть инициализированный объект
public class AdapterFactory {
    private String adapterType;

    public AdapterFactory(ru.binbank.fnsservice.utils.ConfigHandler configHandler) {
        adapterType = configHandler.getConfigType();
    }

    public FnsAdapter getAdapter() {

        FnsAdapter fnsAdapter = null;

        if( "file".equals(adapterType) ) {
            // если на вход подали config для работы с файлами
            fnsAdapter = new FileAdapter();
        }
        else if( "MQ".equals(adapterType) ) {
            // если на вход подали config для работы с очередью
            fnsAdapter = new MQAdapter();
        }

        return fnsAdapter;
    }
}
