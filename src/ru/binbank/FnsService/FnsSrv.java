package ru.binbank.fnsservice;

import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.contracts.ZSVResponse;
import ru.binbank.fnsservice.utils.Command;
import ru.binbank.fnsservice.utils.ConfigHandler;

import java.sql.SQLException;
import java.text.ParseException;
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
        ru.binbank.fnsservice.RequestConnector requestConnector = new ru.binbank.fnsservice
                .RequestConnector(configHandler.getBatchSize(),
                                  configHandler.getInputDir(),
                                  configHandler.getProcessedDir());
        Collection<ZSVRequest> requests = requestConnector.fetchRequests();

        // Получение ответов
        ru.binbank.fnsservice.ZSVEngine zsvEngine = new ru.binbank.fnsservice.ZSVEngine(configHandler.getHiveConfig().connString,
                configHandler.getHiveConfig().login,
                configHandler.getHiveConfig().password);
        Collection<ZSVResponse> zsvResponses = null;
        try {
            zsvResponses = zsvEngine.getResult(requests);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Запись ответов
        ru.binbank.fnsservice.ResponseConnector responseConnector = new ru.binbank.fnsservice.ResponseConnector();
        responseConnector.writeResponses(zsvResponses);

    }
}