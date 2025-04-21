package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.*;
import kc.wheremybuckgoes.modal.card.Card;
import kc.wheremybuckgoes.modal.card.Filters;
import kc.wheremybuckgoes.repositories.VisualizeRepository;
import kc.wheremybuckgoes.utils.ApplicationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static kc.wheremybuckgoes.utils.ApplicationHelper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisualizeService {
    private final TransactionService transactionService;
    private final VisualizeRepository visualizeRepository;
    
    private List<Transaction> populateTransactions(User user, String dateType, long dynamic, long start, long end){
        return switch (dateType) {
            case "all" -> transactionService.getAllTransactionForUser(user.getEmail());
            case "after" -> transactionService.getAllTransactionForUserAfterDate(user, dynamic);
            case "before" -> transactionService.getAllTransactionForUserBeforeDate(user, dynamic);
            case "this_week" -> {
                dynamic = getThisWeekTimeInMillis(DayOfWeek.MONDAY);
                yield transactionService.getAllTransactionForUserAfterDate(user, dynamic);
            }
            case "this_month" -> {
                dynamic = getStartOfCurrentMonthMillis();
                yield transactionService.getAllTransactionForUserAfterDate(user, dynamic);
            }
            case "this_quarter" -> {
                dynamic = getStartOfCurrentQuarterMillis();
                yield transactionService.getAllTransactionForUserAfterDate(user, dynamic);
            }
            case "this_year" -> {
                dynamic = getStartOfCurrentYearMillis();
                yield transactionService.getAllTransactionForUserAfterDate(user, dynamic);
            }
            case "last_year" -> {
                dynamic = getStartOfLastYearMillis();
                yield transactionService.getAllTransactionForUserAfterDate(user, dynamic);
            }
            case "between" -> transactionService.getAllTransactionForUserBetweenDate(user, start, end);
            default -> new ArrayList<>();
        };
    }

    private List<Transaction> applyFilters(List<Transaction> transactions, Filters filters){
        Map<String, String> categoriesMap = Arrays.asList(filters.getCategories()).stream().collect(Collectors.toMap(String::toLowerCase, String::toLowerCase));
        Map<String, String> accountsMap = Arrays.asList(filters.getAccounts()).stream().collect(Collectors.toMap(String::toLowerCase, String::toLowerCase));
        Map<String, String> transactionModesMap = Arrays.asList(filters.getTransactionModes()).stream().collect(Collectors.toMap(String::toLowerCase, String::toLowerCase));
        Map<String, String> transactionTypesMap = Arrays.asList(filters.getTransactionTypes()).stream().collect(Collectors.toMap(String::toLowerCase, String::toLowerCase));
        return transactions.stream().filter(transaction -> {
            String account = transaction.getAccount().toLowerCase();
            String category = transaction.getCategory().toLowerCase();
            String transactionMode = transaction.getTransactionMode().toLowerCase();
            String transactionType = transaction.getType().name().toLowerCase();
            if(transaction.isDisableForCharts() || transaction.isDeleted()){
                return false;
            }
            if(!categoriesMap.containsKey("all") && !categoriesMap.containsKey(category)){
                return false;
            }
            if(!accountsMap.containsKey("all") && !accountsMap.containsKey(account)){
                return false;
            }
            if(!transactionModesMap.containsKey("all") && !transactionModesMap.containsKey(transactionMode)){
                return false;
            }
            if(!transactionTypesMap.containsKey("all") && !transactionTypesMap.containsKey(transactionType)){
                return false;
            }
            return true;
        }).toList();
    }

    private String getTimeFrameUniqueKey(String timeframe, long date){
        return switch(timeframe){
            case "day" -> getDateFromMillis(date, "dd MMM yyyy");
            case "month" -> getDateFromMillis(date, "MMM yy");
            case "quarter" -> getDateFromMillis(date, "quarter");
            case "year" -> getDateFromMillis(date, "yyyy");
            default -> "all";
        };
    }

    private JSONObject processChart(String timeFrame, String dimension, String function, List<Transaction> transactions){
        Map<String, Map<String, List<Long>>> map = new LinkedHashMap<>();
        if(dimension.equals("transactionType")){
            transactions.forEach(transaction->{
                String key = this.getTimeFrameUniqueKey(timeFrame, transaction.getCreatedDate());
                Map<String, List<Long>> tempMap;
                if(map.containsKey(key)){
                    tempMap = map.get(key);
                    if(tempMap.containsKey(transaction.getType().toString())){
                        List<Long> amounts = tempMap.get(transaction.getType().toString());
                        amounts.add(transaction.getAmount());
                        tempMap.put(transaction.getType().toString(), amounts);
                    }else{
                        List<Long> amounts = new ArrayList<>();
                        amounts.add(transaction.getAmount());
                        tempMap.put(transaction.getType().toString(), amounts);
                    }
                }else{
                    tempMap = new HashMap<>();
                    List<Long> amounts = new ArrayList<>();
                    amounts.add(transaction.getAmount());
                    tempMap.put(transaction.getType().toString(), amounts);
                }
                map.put(key, tempMap);
            });
        }
        return this.processFunction(map, function);
    }

    private Long processSum(List<Long> list){
        return list.stream().reduce(0L, Long::sum);
    }

    private JSONObject processFunction(Map<String, Map<String, List<Long>>> map, String function){
        JSONObject object = new JSONObject();
        map.forEach((label, datasets)->{
            JSONObject datasetJSON = new JSONObject();
            datasets.forEach((dataset, data)->{
                Long result = null;
                if(function.equals("sum")){
                    result = this.processSum(data);
                }
                datasetJSON.put(dataset, result);
            });
            object.put(label, datasetJSON);
        });
        return object;
    }

    public JSONObject getChartPreview(User user, Card card){
        String dateType = card.getLayout().getDateOptions().getDateType();
        String timeFrame = card.getLayout().getDateOptions().getTimeFrame();
        String dimension = card.getLayout().getDimension();
        String function = card.getLayout().getFunction();
        Filters filters = card.getLayout().getFilters();
        long start = card.getLayout().getDateOptions().getRange().getStart();
        long end = card.getLayout().getDateOptions().getRange().getEnd();
        long dynamic = card.getLayout().getDateOptions().getRange().getDynamic();
        List<Transaction> transactions = this.populateTransactions(user, dateType, dynamic, start, end);
        transactions = this.applyFilters(transactions, filters);
        return this.processChart(timeFrame, dimension, function, transactions);
    }

    public Visualize getVisualizeById(Long id){
        return visualizeRepository.findById(id).orElse(null);
    }

    public Visualize createOrUpdateVisualize(Visualize visualize){
        log.info("VisualizeService: createOrUpdateVisualize()");
        return visualizeRepository.save(visualize);
    }

    public Visualize getSettingByUser(User user){
        log.info("VisualizeService: getSettingByUser()");
        Visualize visualize = visualizeRepository.findByCreatedBy(user).orElse(null);
        if(visualize == null){
            visualize = Visualize.builder().createdBy(user).dashboard("[]".getBytes()).build();
            visualize = createOrUpdateVisualize(visualize);
        }
        return visualize;
    }
}
