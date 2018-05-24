package ru.binbank.fnsservice;

import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.contracts.ZSVResponse;
import ru.binbank.fnsservice.utils.Command;
import ru.binbank.fnsservice.utils.ConfigHandler;

import java.sql.SQLException;
import java.util.Collection;

public class FnsSrv {

    /**
     * Точка входа в программу.
     */
    public static void main(String[] args)
    {
        // Разбор параметров командной строки
        Command command = new Command(args);
        String config = command.getConfigOpt();

        ConfigHandler configHandler = null;
        try {
            configHandler = new ConfigHandler(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Чтение входящих сообщений
        RequestConnector requestConnector = new RequestConnector(configHandler.getBatchSize());
        Collection<ZSVRequest> requests = requestConnector.fetchRequests();

        // Получение ответов
        ZSVEngine zsvEngine = new ZSVEngine();
        Collection<ZSVResponse> zsvResponses = null;
        try {
            zsvResponses = zsvEngine.getResult(requests);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Запись ответов
        ResponseConnector responseConnector = new ResponseConnector();
        responseConnector.writeResponses(zsvResponses);

    }
}