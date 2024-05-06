package kc.wheremybuckgoes.services;

import kc.wheremybuckgoes.constants.ApplicationConstant.TransactionType;
import kc.wheremybuckgoes.modal.Dashboard;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.modal.charts.MultiDimSeries;
import kc.wheremybuckgoes.modal.charts.Series;
import kc.wheremybuckgoes.modal.charts.SingleDimSeries;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static kc.wheremybuckgoes.utils.ApplicationHelper.getMonthsIndexMap;
import static kc.wheremybuckgoes.utils.ApplicationHelper.sortByMonthYear;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final TransactionService transactionService;

    public Dashboard getDashboardAfterDate(User user, long date){
        Map<String, Map<String, long[]>> map = new HashMap<>();
        Map<String, Map<String, Map<String, long[]>>> pieMap = new HashMap<>();
        Map<Integer, String> monthsMap =  getMonthsIndexMap();
        Calendar calendar = Calendar.getInstance();
        List<Transaction> transactions;
                if(date == 0){
                    transactions = transactionService.getAllTransactionForUser(user.getEmail()).stream().filter(transaction -> !transaction.isDeleted()).toList();
                }else{
                    transactions = transactionService.getAllTransactionForUserAfterDate(user, date).stream().filter(transaction ->!transaction.isDeleted()).toList();
                }
        transactions.forEach(transaction -> {
            if(transaction.getAccount() == null || transaction.getCategory() == null){
                return;
            }
            Date dd = new Date(transaction.getCreatedDate());
            calendar.setTime(dd);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            String uniqueId = monthsMap.get(month) + '-' + year;
            String account = transaction.getAccount();
            long expense = transaction.getType().equals(TransactionType.DEBIT) ? transaction.getAmount() : 0;
            long income = transaction.getType().equals(TransactionType.CREDIT) ? transaction.getAmount() : 0;
            this.updateMapForAccount(map, uniqueId, account, income, expense);
            this.updateMapForAccount(map, uniqueId, "ALL", income, expense);
            this.updateMapForAccountPieMap(pieMap, uniqueId,account,transaction.getCategory(),income, expense);
            this.updateMapForAccountPieMap(pieMap, uniqueId,"ALL",transaction.getCategory(),income, expense);
        });
        return this.createDashboard(map, pieMap);
    }

    private Dashboard createDashboard(Map<String, Map<String, long[]>> map, Map<String, Map<String, Map<String, long[]>>> pieMap){
        Dashboard dashboard = new Dashboard();
        map.keySet().forEach(account->{
            Map<String, long[]> fromMap = map.get(account);
            Series income = Series.builder().name("Income").color("#31C48D").data(new ArrayList<>()).build();
            Series expense = Series.builder().name("Expense").color("#F05252").data(new ArrayList<>()).build();
            List<String> labels = new ArrayList<>();
            SortedMap<String, long[]> sortedMap = sortByMonthYear(fromMap);

            for (Map.Entry<String, long[]> entry : sortedMap.entrySet()) {
                long[] values = fromMap.get(entry.getKey());
                labels.add(entry.getKey());
                income.getData().add(values[0]);
                expense.getData().add(values[1]);
            }
            MultiDimSeries mds = new MultiDimSeries();
            mds.setLabels(labels);
            mds.setSeries(Arrays.asList(income, expense));
            dashboard.getMultiDimChart().put(account, mds);
        });
        pieMap.keySet().forEach(account->{
            Map<String, Map<String, long[]>> fromMap = pieMap.get(account);
            Map<String, Map<String, List<SingleDimSeries>>> singleDimChart = dashboard.getSingleDimChart();
            Map<String, List<SingleDimSeries>> newMap = new HashMap<>();
            fromMap.keySet().forEach(uniqueId->{
                Map<String, long[]> uniqueIdMap = fromMap.get(uniqueId);
                SingleDimSeries income = SingleDimSeries.builder().series(new ArrayList<>()).labels(new ArrayList<>()).colors(new ArrayList<>()).build();
                SingleDimSeries expense = SingleDimSeries.builder().series(new ArrayList<>()).labels(new ArrayList<>()).colors(new ArrayList<>()).build();
                uniqueIdMap.keySet().forEach(category-> {
                    long[] values = uniqueIdMap.get(category);
                    income.getLabels().add(category);
                    income.getSeries().add(values[0]);
                    expense.getLabels().add(category);
                    expense.getSeries().add(values[1]);
                });
                newMap.put(uniqueId, Arrays.asList(income, expense));
            });
            singleDimChart.put(account, newMap);
        });
        return dashboard;
    }

    private void updateMapForAccount(Map<String, Map<String, long[]>> map,String uniqueId, String account, long income, long expense){
        if(map.containsKey(account)){
            this.updateMapForUniqueId(map.get(account), uniqueId, income, expense);
        }else{
            Map<String, long[]> tempMap = new HashMap<>();
            tempMap.put(uniqueId, new long[]{income, expense});
            map.put(account, tempMap);
        }
    }

    private void updateMapForUniqueId(Map<String, long[]> map,String uniqueId, long income, long expense){
        if(map.containsKey(uniqueId)){
            long[] values = map.get(uniqueId);
            income+=values[0];
            expense+=values[1];
            map.put(uniqueId, new long[]{income, expense});
        }else{
            map.put(uniqueId, new long[]{income, expense});
        }
    }

    private void updateMapForAccountPieMap(Map<String, Map<String, Map<String, long[]>>> pieMap, String uniqueId, String account, String category, long income, long expense){
        Map<String, Map<String, long[]>> tempMap = pieMap.containsKey(account) ? pieMap.get(account) : new HashMap<>();
        this.updateMapForUniqueIdPieMap(tempMap, uniqueId, category, income, expense);
        this.updateMapForUniqueIdPieMap(tempMap, "ALL", category, income, expense);
        pieMap.put(account, tempMap);
    }

    private void updateMapForUniqueIdPieMap(Map<String, Map<String, long[]>> map,String uniqueId, String category, long income, long expense){
        Map<String, long[]> tempMap = map.containsKey(uniqueId) ? map.get(uniqueId) : new HashMap<>();
        this.updateMapForCategoryPieMap(tempMap, category, income, expense);
        map.put(uniqueId, tempMap);
    }

    private void updateMapForCategoryPieMap(Map<String, long[]> map,String category, long income, long expense){
        if(map.containsKey(category)){
            long[] values = map.get(category);
            income+=values[0];
            expense+=values[1];
            map.put(category, new long[]{income, expense});
        }else{
            map.put(category, new long[] {income, expense});
        }
    }
}
