package ru.binbank.fnsservice;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import ru.binbank.fnsservice.contracts.BankType;
import ru.binbank.fnsservice.contracts.ZSVRequest;
import ru.binbank.fnsservice.contracts.ZSVResponse;
import ru.binbank.fnsservice.utils.ConfigHandler;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


public class ZSVEngine {
    private Connection hiveConnection;
    //private Statement stmt;

    private static final Logger log = Logger.getLogger(ZSVEngine.class);

    // Параметры hive
    private final ConfigHandler.HiveConfig hiveConfig;

    public ZSVEngine(ConfigHandler.HiveConfig hiveConfig) {
        this.hiveConfig = hiveConfig;
    }

    private class RequestsParams {
        public String nomZapr;
    }

    public void createHiveConnection() throws SQLException, ClassNotFoundException {
        Class.forName(hiveConfig.driverName);
/*        hiveConnection = DriverManager.getConnection(
                hiveConfig.connString,     // строка соединения, например "jdbc:hive2://msk-hadoop01:10000/default"
                hiveConfig.login,
                hiveConfig.password
        );*/
        Properties properties = new Properties();
        properties.setProperty("AuthMech", "3" /* User Name and Password */);
        properties.setProperty("UID", hiveConfig.login);
        properties.setProperty("PWD", hiveConfig.password);
        properties.setProperty("UseNativeQuery", "1");

        log.info(String.format("Opening hive connection (ConnectionString=%s, UID=%s)", hiveConfig.connString, hiveConfig.login));

        hiveConnection = DriverManager.getConnection(hiveConfig.connString, properties);

        log.info("Hive connection opened");
    }

    /**
     * Поиск идентификаторов банков по их БИКам.
     * @param bics
     * @return
     */
    private Map<String, Long> selectBankIdByBIC(Collection<String> bics) throws SQLException {
        // Формирование текста запроса
        String query = "select idbank, bic from bank where bic in ("
                .concat(bics.stream().distinct().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(","))) // quote
                .concat(")");

        // Выполнение запроса
        log.info("Running hive query:");
        log.info(query);
        StopWatch stopWatch = new StopWatch(); stopWatch.start();

        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        stopWatch.stop();

        // Разбор результата
        HashMap<String, Long> result = new HashMap<String, Long>();

        int i = 0; // счетчик строк

        while (resultSet.next()) {
            result.put(resultSet.getString("bic"), resultSet.getLong("idbank"));
            i++;
        }

        log.info(String.format("Executed in %s, fetched %d rows", stopWatch, i));

        resultSet.close();
        stmt.close();

        return result;
    }

    /**
     * Проверка, что ИНН существует в базе.
     * @param inns
     * @return Массив найденных ИНН
     */
    private Map<String, Long> selectExistingInn(Collection<String> inns, Long idBank) throws SQLException {
        // Формирование текста запроса
        String query = "select inn, idclient from inn where inn in ("
                .concat(inns.stream().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(","))) // quote
                .concat(") and idbank=").concat(idBank.toString());


        // Выполнение запроса
        log.info("Running hive query:");
        log.info(query);
        StopWatch stopWatch = new StopWatch(); stopWatch.start();

        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        stopWatch.stop();

        // Разбор результата
        HashMap<String, Long> result = new HashMap<>();

        int i = 0; // счетчик строк

        while (resultSet.next()) {
            result.put(resultSet.getString("inn"), resultSet.getLong("idclient"));
            i++;
        }

        resultSet.close();
        stmt.close();

        log.info(String.format("Executed in %s, fetched %d rows", stopWatch, i));

        return result;
    }


    // Возвращает исходную коллецию кодов счетов, в которой новые коды (из ЦФТ) заменены старыми кодами (из Афины)
    private Collection<String> getOldAccList (Collection<String> accCodes, Map<String, String> accOldNewCodes) {
        LinkedList<String> oldAccCodes = new LinkedList<>();

        if (!accCodes.isEmpty()) {
            for (String accCode : accCodes) {
                // Проверяем, есть ли для данного счёта из запроса соответствующий ему старый счёт Афины:
                String oldAcc = (String) accOldNewCodes.get(accCode);
                oldAccCodes.add(oldAcc);
            }

        }

        return oldAccCodes;
    }


    // Возвращает map, в котором новому коду счёта (из ЦФТ) ставится в соответствие старый код счёта (из Афины).
    // Составляется для счетов из коллекции, которая подаётся на вход.
    private Map<String, String> getAccOldNewCodes (Collection<String> accCodes) throws SQLException {

        // Соответствие между новыми счетами (ЦФТ) и старыми счетами (Афина) - только для счетов, имеющих такое соответствие:
        HashMap<String, String> accOldCodes = new HashMap<>();

        // Соответствие между новыми счетами (ЦФТ) и старыми счетами (Афина) - для всех счетов;
        // если в ЦФТ код счёта такой же, то значение равно ключу:
        HashMap<String, String> accOldNewCodes = new HashMap<>();

        // Если в запросах есть новые коды счетов (из ЦФТ), то найдём соответствующие им старые коды (из Афины)
        String queryByNewAcc = null;
        if (!accCodes.isEmpty())
            queryByNewAcc = "select codenew, codeold from account_history where codenew in ("
                    .concat(accCodes.stream().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(",")))
                    .concat(")");

        log.info("Running hive query:");
        log.info(queryByNewAcc);
        StopWatch stopWatch = new StopWatch(); stopWatch.start();

        // Выполнение запроса поиска по новым счетам
        Statement stmt_newAcc = hiveConnection.createStatement();
        ResultSet resultSet_newAcc = stmt_newAcc.executeQuery(queryByNewAcc);

        stopWatch.stop();

        int i = 0; // счетчик строк

        // Заполняем соответствие "новый код счёта - старый код счёта"
        // только для счетов, имеющих такое соответствие:
        while (resultSet_newAcc.next()) {
            accOldCodes.put(resultSet_newAcc.getString(1), resultSet_newAcc.getString(2));
            i++;
        }

        log.info(String.format("Executed in %s, fetched %d rows", stopWatch, i));

        // Проходим по всем счетам из запросов:
        for (String accCode : accCodes) {
            String oldAcc = (String) accOldCodes.get(accCode);

            // если счёта нет, значит, в ЦФТ он такой же
            if (oldAcc == null)
                accOldNewCodes.put(accCode, accCode);
                // иначе - записываем соотвествие старого и нового кодов счета
            else
                accOldNewCodes.put(accCode, oldAcc);
        }

        resultSet_newAcc.close();
        stmt_newAcc.close();

        return accOldNewCodes;

    }


    private Map<String, Map<String, Object> > selectAccounts(Collection<String> accCodes, Collection<Long> idClients, Long idBank) throws SQLException {
        HashMap<String, Map<String, Object> > result = new HashMap<>();

        // Формирование текста запроса
        String queryByAccs = null;
        if (!accCodes.isEmpty())
            queryByAccs = "select idacc, idclient, code, currency from account where code in ("
                          .concat(accCodes.stream().map(s1 -> "'" + s1 + "'").collect(Collectors.joining(","))) // quote
                          .concat(") and idbank=").concat(idBank.toString()).concat("\n");
        String queryByClient = null;
        if (!idClients.isEmpty())
            queryByClient ="select idacc, idclient, code, currency from account where idclient in ("
                 .concat(idClients.stream().map(aLong -> aLong.toString()).collect(Collectors.joining(",")))
                 .concat(") and idbank=").concat(idBank.toString());
        String[] queries = {queryByAccs, queryByClient };
        String query = Arrays.stream(queries).filter(Objects::nonNull).collect(Collectors.joining("union all\n"));

        // Если нечего запрашивать, то на выход.
        if (query.isEmpty())
            return result;

        // Выполнение запроса
        StopWatch stopWatch = new StopWatch(); stopWatch.start();
        log.info("Running hive query:");
        log.info(query);
        stopWatch.reset(); stopWatch.start();

        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        stopWatch.stop();

        // Разбор результата
        int i = 0; // счетчик строк

        while (resultSet.next()) {
            HashMap<String, Object> rowMap = new HashMap<>();
            // Заполняем значениями атрибутов
            // Hive при выполнении union добавляет к названию столбца префикс,
            // поэтому доступ к полям по имени здесь не подходит - используем индексы.
            rowMap.put("idacc", resultSet.getLong(1));
            rowMap.put("idclient", resultSet.getLong(2));
            rowMap.put("currency", resultSet.getString(4));

            result.put(resultSet.getString(3), rowMap);
        }

        resultSet.close();
        stmt.close();

        log.info(String.format("Executed in %s, fetched %d rows", stopWatch, i));

        return result;
    }

    private  Map<Long, Map<Date, BigDecimal> > selectRest(Collection<Long> idAccs, Date minDate, Date maxDate, Long idBank) throws SQLException {
        // Форматируем даты
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringMindate = format.format(minDate);
        String stringMaxdate = format.format(maxDate);

        // Формирование текста запроса
        String query = "select amount, idaccount, dt from rest where idaccount in ("
                .concat(idAccs.stream().map(n -> n.toString()).collect(Collectors.joining(","))) // quote
                .concat(") and idbank=").concat(idBank.toString())
                .concat(" and dt between cast('").concat(stringMindate).concat("' as date)")
                .concat(" and cast('").concat(stringMaxdate).concat("' as date)");

        // Выполнение запроса
        log.info("Running hive query:");
        log.info(query);
        StopWatch stopWatch = new StopWatch(); stopWatch.start();

        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        stopWatch.stop();

        // Разбор результата
        HashMap<Long, Map<Date, BigDecimal> > result = new HashMap<>();

        int i = 0; // счетчик строк

        while (resultSet.next()) {
            Long idAcc = resultSet.getLong("idaccount");
            Map<Date, BigDecimal> rowMap = result.get(idAcc);
            // Если данных по данному счету еще не было, то создаем Map
            if (rowMap == null) {
                rowMap = new HashMap<>();
                result.put(idAcc, rowMap);
            }
            // Заполняем значениями атрибутов
            rowMap.put(resultSet.getDate("dt"), resultSet.getBigDecimal("amount"));
            i++;
        }

        resultSet.close();
        stmt.close();

        log.info(String.format("Executed in %s, fetched %d rows", stopWatch, i));

        return result;
    }



    public void closeHiveConnection() throws SQLException {
        if (hiveConnection != null) {
            log.info("Closing hive connection");
            hiveConnection.close();
        }
    }


    public Statement getStatement() throws SQLException {
        return hiveConnection.createStatement();
    }


    public void closeStatement(Statement statement) throws SQLException {
        statement.close();
    }


    // Проверка строки, которая должна конвертироваться в BigInteger.
    private BigInteger getBigIntFromString (String inString) {

        if (inString == null || inString.equals("")) {
            return null;
        }
        else {
            BigInteger outBigInt = new BigInteger(inString);
            return outBigInt;
        }

    }

    // Проверка строки, которая должна конвертироваться в BigDecimal.
    private BigDecimal getBigDecFromString (String inString) {

        if (inString == null || inString.equals("")) {
            return null;
        }
        else {
            BigDecimal outBigDec = new BigDecimal(inString);
            return outBigDec;
        }

    }

    /**
     * Формирование текста запроса по операциям
     * @param requests
     * @throws SQLException
     */
    private Map<Long, List<ZSVResponse.SvBank.Svedenia.Operacii> > selectOperacii(Collection<Long> idAccs, Date minDate, Date maxDate, Long idBank) throws SQLException, DatatypeConfigurationException {
        ArrayList<ZSVResponse.SvBank.Svedenia.Operacii> allOperacii = new ArrayList<ZSVResponse.SvBank.Svedenia.Operacii>();

        // Форматируем даты
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringMindate = format.format(minDate);
        String stringMaxdate = format.format(maxDate);

        // Формирование текста запроса
        String query = "select idaccount, description, dtoperdate, viddoc, dtdocdate, docnum, corraccnum, paybankname,"
                .concat("      paybankbik, clientlabel, clientinn, clientkpp, clientaccnum, amountdeb, amountcre")
                .concat(" from zsv_lines_parquet ")
                .concat("where idaccount in (")
                .concat(idAccs.stream().map(x -> "'" + x + "'").collect(Collectors.joining(",")))
                .concat(") and dtdocdate between cast ('").concat(stringMindate).concat("' as date) ")
                .concat("  and cast ('").concat(stringMaxdate).concat("' as date) ");

        // Выполнение запроса
        log.info("Running hive query:");
        log.info(query);
        StopWatch stopWatch = new StopWatch(); stopWatch.start();

        Statement stmt = hiveConnection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        stopWatch.stop();

        // Объект для итогового результата:
        HashMap<Long, List<ZSVResponse.SvBank.Svedenia.Operacii> > result = new HashMap<>();

        int i = 0; // счетчик строк

        LinkedList<ZSVResponse.SvBank.Svedenia.Operacii> opers;

        // Разбор результата
        while (resultSet.next()) {
            // Записываем id счёта из текущей строки результата hive-запроса:
            Long idAcc = resultSet.getLong("idaccount");

            // Проверяем, есть ли для данного счёта запись в результирующем map'е.
            // Если нет, создаём запись для данного счёта.
            opers = (LinkedList<ZSVResponse.SvBank.Svedenia.Operacii>) result.get(idAcc);

            if(opers == null) {
                opers = new LinkedList<ZSVResponse.SvBank.Svedenia.Operacii>();
                result.put(idAcc, opers);
            }

            // Формируем объект класса ZSVResponse.SvBank.Svedenia.Operacii для данной строки результата hive-запроса:
            ZSVResponse.SvBank.Svedenia.Operacii operacii = new ZSVResponse.SvBank.Svedenia.Operacii();

            // operacii.dataOper
            GregorianCalendar dataOper_Greg = new GregorianCalendar();
            dataOper_Greg.setTime(resultSet.getDate("dtoperdate"));
            XMLGregorianCalendar dataOper_XMLGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataOper_Greg);
            operacii.setDataOper(dataOper_XMLGreg);

            // operacii.NaznPl
            operacii.setNaznPl(resultSet.getString("description"));

            //
            // Объект recvDoc
            ZSVResponse.SvBank.Svedenia.Operacii.RekvDoc recvDoc = new ZSVResponse.SvBank.Svedenia.Operacii.RekvDoc();

            // recvDoc.BidDoc
            BigInteger viddocBigInteger = getBigIntFromString(resultSet.getString("viddoc"));
            //BigInteger viddocBigInteger = BigInteger("");
            recvDoc.setBidDoc(viddocBigInteger);

            // recvDoc.NomDoc
            recvDoc.setNomDoc(resultSet.getString("docnum"));

            // recvDoc.DataDoc
            GregorianCalendar dataDoc_Greg = new GregorianCalendar();
            dataDoc_Greg.setTime(resultSet.getDate("dtdocdate"));
            XMLGregorianCalendar dataDoc_XMLGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataDoc_Greg);
            recvDoc.setDataDoc(dataDoc_XMLGreg);

            //
            // Объект recvBank
            ZSVResponse.SvBank.Svedenia.Operacii.RekvBank recvBank = new ZSVResponse.SvBank.Svedenia.Operacii.RekvBank();
            recvBank.setNomKorSch(resultSet.getString("corraccnum"));
            recvBank.setNaimBP(resultSet.getString("paybankname"));
            recvBank.setBIKBP(resultSet.getString("paybankbik"));

            //
            // Объект recvPlat
            ZSVResponse.SvBank.Svedenia.Operacii.RekvPlat recvPlat = new ZSVResponse.SvBank.Svedenia.Operacii.RekvPlat();
            recvPlat.setNaimPP(resultSet.getString("clientlabel"));
            recvPlat.setINNPP(resultSet.getString("clientinn"));
            recvPlat.setKPPPP(resultSet.getString("clientkpp"));
            recvPlat.setNomSchPP(resultSet.getString("clientaccnum"));

            //
            // Объект summaOper
            ZSVResponse.SvBank.Svedenia.Operacii.SummaOper summaOper = new ZSVResponse.SvBank.Svedenia.Operacii.SummaOper();

            // summaOper.amountdeb
            BigDecimal amountdebBigDecimal = getBigDecFromString(resultSet.getString("amountdeb"));
            summaOper.setDebet(amountdebBigDecimal);

            // summaOper.amountcre
            BigDecimal amountcreBigDecimal = getBigDecFromString(resultSet.getString("amountcre"));
            summaOper.setCredit(amountcreBigDecimal);

            operacii.setRekvDoc(recvDoc);
            operacii.setRekvBank(recvBank);
            operacii.setRekvPlat(recvPlat);
            operacii.setSummaOper(summaOper);

            // Записываем объект класса ZSVResponse.SvBank.Svedenia.Operacii в список opers:
            opers.add(operacii);

            i++;
        }

        resultSet.close();
        stmt.close();

        log.info(String.format("Executed in %s, fetched %d rows", stopWatch, i));

        return result;
    }

    /**
     * Получение ответов на запросы ФНС.
     * @param requests
     * @throws SQLException
     */
    public Collection<ZSVResponse> getResult(Collection<ZSVRequest> requests,
                                             Map<ZSVResponse, ZSVRequest> zsvResponseZSVRequestMap)
            throws SQLException, ParseException, ClassNotFoundException, DatatypeConfigurationException {
        try {
            // Открытие соединения
            createHiveConnection();

            // Соответствие запросов и ответов
            HashMap<ZSVRequest, List<ZSVResponse>> respMap = new HashMap<>();
            // Для каждого запроса создать пустой список ответов
            for (ZSVRequest r : requests)
                respMap.put(r, new LinkedList<>());

            // Запросы, по которым еще не сформирован ответ
            Iterable<ZSVRequest> requestsToProcess;

            // Определение идентификаторов банков
            LinkedList<String> bics = new LinkedList<>();
            for (ZSVRequest r : requests) {
                bics.add(r.getZapnoVipis().getsvBank().getBIK());
            }
            Map<String, Long> banks = selectBankIdByBIC(bics);
            // Проверка, что банки найдены
            for (ZSVRequest r : requests) {
                if (!banks.containsKey(r.getZapnoVipis().getsvBank().getBIK())) {
                    // Формирование ответа об отсутствии банка
                    ZSVResponse response = new ZSVResponse();
                    // TODO: 01.06.2018 формирование ответа с ошибкой
                    respMap.get(r).add(response);
                }
            }
            // Запросы, по которым уже есть ответы, не обрабатываем
            requestsToProcess = requests.stream().filter(zsvRequest -> respMap.get(zsvRequest).isEmpty()).collect(Collectors.toList());

            // TODO: 30.05.2018 сделать правильную начитку банка
            Long idBank = (Long) banks.values().toArray()[0];

            // Поиск клиентов
            LinkedList<String> inns = new LinkedList<>();
            // Обрабатываются только запросы, по которым еще нет ответа
            for (ZSVRequest r : requestsToProcess) {
                inns.add(getRequestInn(r));
            }
            Map<String, Long> existingInns = selectExistingInn(inns, idBank);
            // Разбор найденных клиентов
            /* Скорее всего не нужно. так как ответы обрабатываются централизованно
            for (ZSVRequest r : requestsToProcess) {
                if (!existingInns.containsKey(r.getZapnoVipis().getSvPl().getPlUl().getINNUL())) {
                    // Формирование ответа об отсутствии клиента
                    ZSVResponse response = new ZSVResponse();
                    // TODO: 01.06.2018 формирование ответа с ошибкой
                    respMap.get(r).add(response);
                }
            }*/
            // Запросы, по которым уже есть ответы, не обрабатываем
            requestsToProcess = requests.stream().filter(zsvRequest -> respMap.get(zsvRequest).isEmpty()).collect(Collectors.toList());

            // Клиентов, по которым пришел запрос по всем счетам, выделяем в отдельный список
            LinkedList<Long> allAccsClients = new LinkedList<>();
            for (ZSVRequest r : requests) {
                if (r.getZapnoVipis().getpoVsem() != null) {
                    Long clientId = existingInns.get(getRequestInn(r));
                    if (clientId != null)
                        allAccsClients.add(clientId);
                }
            }

            // Поиск счетов
            LinkedList<String> accCodes = new LinkedList<>();
            for (ZSVRequest r : requestsToProcess) {
                for (ZSVRequest.ZapnoVipis.poUkazannim poUkazannim : r.getZapnoVipis().getpoUkazannim()) {
                    accCodes.add(poUkazannim.getNomSch());
                }
            }

            // Формируем соответствие между новым кодом счёта (из ЦФТ) и старым кодом счёта (из Афины):
            Map<String, String> accOldNewCodes = getAccOldNewCodes(accCodes);

            // Составляем список, состоящий только из старых кодов счетов (из Афины):
            Collection<String> oldAccList = getOldAccList(accCodes, accOldNewCodes);

            Map<String, Map<String, Object>> accounts = selectAccounts(oldAccList, allAccsClients, idBank);
            // Разбор найденных счетов
            for (ZSVRequest r : requestsToProcess) {
                // Если запрос "по всем" счетам, то поиск соответствия делать не надо
                if (r.getZapnoVipis().getpoVsem() != null) {
                    // Если по запрошенному счету счет не найден, то нужно делать отдельный ответ с кодом 42 (счет не найден)
                    for (ZSVRequest.ZapnoVipis.poUkazannim poUkazannim : r.getZapnoVipis().getpoUkazannim()) {
                        if (!accounts.containsKey(poUkazannim.getNomSch())) {
                            // Запрошенный счет не найден. Формируем ответ
                            ZSVResponse response = new ZSVResponse();
                            // TODO: 01.06.2018 Добавить формирование ответа
                            respMap.get(r).add(response);
                        }
                    }
                }
            }

            // Сбор всех запрашиваемых счетов в список
            LinkedList<Long> idAccs = new LinkedList<>();
            for (Map<String, Object> val : accounts.values()) {
                idAccs.add((Long) val.get("idacc"));
            }

            // Запрос остатков
            // Определение минимальной и максимальной дат
            LinkedList<Date> datesFrom = new LinkedList<>();
            LinkedList<Date> datesTo = new LinkedList<>();
            // TODO: 05.06.2018 Анализировать дату не по всем счетам
            for (ZSVRequest r : requestsToProcess) {
                ZSVRequest.ZapnoVipis.ZaPeriod zaPeriod = r.getZapnoVipis().getzaPeriod();
                datesFrom.add(zaPeriod.getDateBeg().toGregorianCalendar().getTime());
                datesTo.add(zaPeriod.getDateEnd().toGregorianCalendar().getTime());
            }
            Date minDate = datesFrom.stream().min(Date::compareTo).get();
            Date maxDate = datesTo.stream().max(Date::compareTo).get();

            Map<Long, Map<Date, BigDecimal>> rest = selectRest(idAccs, minDate, maxDate, idBank);

            // Запрос операций
            Map<Long, List<ZSVResponse.SvBank.Svedenia.Operacii>> operacii =
                    selectOperacii(idAccs, minDate, maxDate, idBank);

            // Формирование ответов
            return makeResponses(requests, banks, existingInns, accounts, operacii, rest, accOldNewCodes, zsvResponseZSVRequestMap);
        }
        finally {
            closeHiveConnection();
        }
    }

    /**
     * Определение ИНН запроса
     * @param r
     * @return
     */
    private String getRequestInn(ZSVRequest r) {
        String inn = null;
        if (r.getZapnoVipis().getSvPl().getPlUl() != null)
            inn = r.getZapnoVipis().getSvPl().getPlUl().getINNUL();
        else if (r.getZapnoVipis().getSvPl().getPlIp() != null)
            inn = r.getZapnoVipis().getSvPl().getPlIp().getINNIP();
        else if (r.getZapnoVipis().getSvPl().getPFL() != null)
            inn = r.getZapnoVipis().getSvPl().getPFL().getInnFl();
        return inn;
    }

    /**
     * Создание ответов на основе собранной информации из базы.
     * @param requests
     * @param banks
     * @param inns
     * @param accounts
     * @param operacii
     * @param rests
     * @return
     */
    private Collection<ZSVResponse> makeResponses(Iterable<ZSVRequest> requests, Map<String, Long> banks, Map<String, Long> inns,
                               Map<String, Map<String, Object> > accounts,
                               Map<Long, List<ZSVResponse.SvBank.Svedenia.Operacii> > operacii,
                               Map<Long, Map<Date, BigDecimal> > rests,
                               Map<String, String> accOldNewCodes, Map<ZSVResponse, ZSVRequest> zsvResponseZSVRequestMap)
    {
        ArrayList<ZSVResponse> responses = new ArrayList<>();

        for (ZSVRequest r: requests) {
            // Определим id банка
            if (banks.get(r.getZapnoVipis().getsvBank().getBIK()) == null) {
                // Банк, указанный в запросе, в базе отсутствует. Формируем соответствующий ответ.
                ZSVResponse response = new ZSVResponse();
                // TODO: 04.06.2018 Установить номер запроса
                response.setZapros("");
                response.setChast(BigInteger.valueOf(1));
                response.setIs(BigInteger.valueOf(1));

                ZSVResponse.Result result = new ZSVResponse.Result();
                result.setKodResProverki("41");
                response.getResult().add(result);

                responses.add(response);
                zsvResponseZSVRequestMap.put(response, r);
                continue; // к следующему запросу
            }

            // Определим клиента
            Long clientId = inns.get(getRequestInn(r));
            if (clientId == null) {
                // Клиент, указанный в запросе, в базе отсутствует. Формируем соответствующий ответ.
                ZSVResponse response = new ZSVResponse();
                // TODO: 04.06.2018 Установить номер запроса
                response.setChast(BigInteger.valueOf(1));
                response.setIs(BigInteger.valueOf(1));

                ZSVResponse.SvBank svBank = new ZSVResponse.SvBank();
                // Копирование атрибутов банка из запроса
                copyBankAttr(svBank, r.getZapnoVipis().getsvBank());

                ZSVResponse.SvBank.Result result = new ZSVResponse.SvBank.Result();
                result.setKodResProverki("44");
                svBank.getResult().add(result);

                response.setSvBank(svBank);
                responses.add(response);
                zsvResponseZSVRequestMap.put(response, r);
                continue; // к следующему запросу
            }

            // Отбираем счета, относящиеся к запросу.
            HashMap<String, Map<String, Object> > accs = new HashMap<>();
            // Поиск по указанным
            for (ZSVRequest.ZapnoVipis.poUkazannim poUkazannim: r.getZapnoVipis().getpoUkazannim()) {
                // Поиск указанных счетов по номеру
                // Подменяем на старый счёт Афины
                String nomSch = poUkazannim.getNomSch();
                nomSch = (String) accOldNewCodes.get(nomSch);
                if (accounts.containsKey(nomSch)) {
                    // По счету должны быть остатки. Если их нет, то счета не было на запрашиваемый интервал.
                    Map<String, Object> accAttr = accounts.get(nomSch);
                    if (rests.containsKey((Long) accAttr.get("idacc")))
                        accs.put(nomSch, accAttr);
                }
            }

            // Если в запросе "по всем", то поиск счетов по клиенту
            if (r.getZapnoVipis().getpoVsem() != null) {
                for (Map.Entry<String, Map<String, Object> > val: accounts.entrySet()) {
                    if (val.getValue().getOrDefault("idclient", 0).equals(clientId)) {
                        // По счету должны быть остатки. Если их нет, то счета не было на запрашиваемый интервал.
                        Map<String, Object> accAttr = val.getValue();
                        if (rests.containsKey((Long) accAttr.get("idacc")))
                            accs.put(val.getKey(), accAttr);
                    }
                }
            }

            // Сведения
            LinkedList<ZSVResponse.SvBank.Svedenia> svedList = new LinkedList<>();

            // По каждому найденному счету сформировать ответ
            for (String accCode : accs.keySet()) {
                Map<String, Object> accAttr = accs.get(accCode);
                Long accId = (Long) accAttr.get("idacc");

                ZSVResponse.SvBank.Svedenia svedenia = new ZSVResponse.SvBank.Svedenia();
                // TODO: 01.06.2018 Заполнение атрибутов
                svedenia.setNomSch(accCode);
                svedenia.setKodVal(getBigDecFromString((String) accAttr.get("currency")));

                // Остатки
                ZSVRequest.ZapnoVipis.ZaPeriod zaPeriod = r.getZapnoVipis().getzaPeriod();
                Rests accRests = getAccountRest(
                        accId,
                        zaPeriod.getDateBeg().toGregorianCalendar().getTime(),
                        zaPeriod.getDateEnd().toGregorianCalendar().getTime(),
                        rests.get(accId));
                // Установка даты начала и конца периода
                GregorianCalendar gc = new GregorianCalendar(); gc.setTime(accRests.dateNach);
                try {
                    svedenia.setDataNach(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                gc = new GregorianCalendar(); gc.setTime(accRests.dateKon);
                try {
                    svedenia.setDataKon(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                // Установка остатков
                svedenia.setOstatNach(accRests.ostatokNach);
                svedenia.setOstatKon(accRests.ostatokKon);

                // Поиск и добавление операций
                List<ZSVResponse.SvBank.Svedenia.Operacii> opers = operacii.get(accId);
                if (opers != null)
                    svedenia.getOperacii().addAll(opers);
                // Нумерация сведений
                int i = 0;
                for (ZSVResponse.SvBank.Svedenia.Operacii oper: svedenia.getOperacii())
                    oper.setIdBlock(++i);

                // Расчет оборотов по операциям
                svedenia.setSummaDeb(svedenia.getOperacii().stream().map(operacii1 -> operacii1.getSummaOper().getDebet())
                                     .reduce(BigDecimal.ZERO, BigDecimal::add));
                svedenia.setSummaKred(svedenia.getOperacii().stream().map(operacii1 -> operacii1.getSummaOper().getCredit())
                                      .reduce(BigDecimal.ZERO, BigDecimal::add));

                // Добавление сведения в общий список
                svedList.add(svedenia);
            }

            // Проверить, что по всем указанным в запросе счетам есть сведения. Если нет, то добавить сведения с ошибкой.
            for (ZSVRequest.ZapnoVipis.poUkazannim poUkazannim: r.getZapnoVipis().getpoUkazannim()) {
                // заменяем poUkazannim.getNomSch() на старый счёт
                if (!accs.containsKey(accOldNewCodes.get(poUkazannim.getNomSch()))) {
                    ZSVResponse.SvBank.Svedenia svedenia = new ZSVResponse.SvBank.Svedenia();
                    svedenia.setNomSch(poUkazannim.getNomSch());

                    ZSVResponse.SvBank.Svedenia.Result result = new ZSVResponse.SvBank.Svedenia.Result();
                    result.setKodResProverki("42");
                    svedenia.getResult().add(result);

                    // Добавление сведения в общий список
                    svedList.add(svedenia);
                }
            }

            // Для каждого сведения создается отдельный ответ
            int i = 1; // счетчик сведений
            for (ZSVResponse.SvBank.Svedenia svedenia: svedList) {
                ZSVResponse response = new ZSVResponse();
                // TODO: 04.06.2018 Установить номер запроса
                ZSVResponse.SvBank svBank = new ZSVResponse.SvBank();
                response.setSvBank(svBank);
                // Копирование атрибутов банка из запроса
                copyBankAttr(svBank, r.getZapnoVipis().getsvBank());
                // Нумерация сведения
                svedenia.setPorNom(String.format("%06d", i));
                // Нумерация ответа
                response.setChast(BigInteger.valueOf(i));
                response.setIs(BigInteger.valueOf(svedList.size()));

                svBank.setSvedenia(svedenia);
                ++i;

                responses.add(response);
                zsvResponseZSVRequestMap.put(response, r);
            }
        }

        return responses;
    }

    /**
     * Выбор входящего и исходящего остатков по счету на даты, указанные в запросе, с учетом дат существования счета.
     * @param accId
     * @param desiredDateFrom
     * @param desiredDateTo
     * @param rests
     * @return
     */
    private Rests getAccountRest(Long accId, Date desiredDateFrom, Date desiredDateTo, Map<Date, BigDecimal> rests) {
        Rests result = new Rests();
        result.dateNach = desiredDateFrom;
        result.dateKon = desiredDateTo;

        if (rests != null) {
            result.ostatokNach = rests.containsKey(result.dateNach) ? rests.get(result.dateNach) : new BigDecimal(0);
            result.ostatokKon = rests.containsKey(result.dateKon) ? rests.get(result.dateKon) : new BigDecimal(0);
        }
        else {
            // for breakpoint
            int x = 0;
        }

        return result;
    }

    private class Rests {
        Date dateNach;
        Date dateKon;
        BigDecimal ostatokNach;
        BigDecimal ostatokKon;
    }

    /**
     * Копирование атрибутов банка из запроса в ответ.
     * @param svBank
     * @param bankFrom
     */
    public void copyBankAttr(ZSVResponse.SvBank svBank, BankType bankFrom) {
        svBank.setRegNom(bankFrom.getRegNom());
        svBank.setNaimBank(bankFrom.getNaim());
        svBank.setKPPBank(bankFrom.getKPPBank());
        svBank.setINNBank(bankFrom.getINNBank());
        svBank.setNomFil(bankFrom.getNomFil());
        svBank.setBIK(bankFrom.getBIK());
    }
}