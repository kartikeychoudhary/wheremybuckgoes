package kc.wheremybuckgoes.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.constants.ApplicationConstant.TransactionType;
import kc.wheremybuckgoes.exceptions.CustomGenericRuntimeException;
import kc.wheremybuckgoes.modal.Transaction;
import kc.wheremybuckgoes.modal.TransactionFromSheet;
import kc.wheremybuckgoes.modal.User;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SheetParsingHelper {
    private SheetParsingHelper(){}

    public static List<Transaction> parseFile(InputStreamReader file, User user) throws FileNotFoundException {
        List<Transaction> transactions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(file)) {
            // Get the header
            String[] header = reader.readNext();
//            for (int i = 0; i < header.length; i++) {
//                if(!header[i].equalsIgnoreCase(ApplicationConstant.HEADERS[i])){
//                    throw new FileNotFoundException("Headers mismatched");
//                }
//            }
            reader.readNext(); // skip example line
            reader.readNext(); // skip empty line

            // Read data lines
            String[] line;

            while ((line = reader.readNext()) != null) {
                String date = null;
                String description = null;
                String mode = null;
                String category = null;
                String account = null;
                double amount = 0;
                TransactionType type = TransactionType.fromString("");
                for (int i = 0; i < line.length; i++) {
                    String value = line[i];
                    String columnName = ApplicationConstant.HEADERS[i];
                    switch (columnName.toLowerCase()){
                        case "date": date = value; break;
                        case "description": description = value; break;
                        case "type": type = TransactionType.fromString(value); break;
                        case "category": category = value; break;
                        case "mode": mode = value; break;
                        case "account": account = value; break;
                        case "amount": amount = Double.parseDouble(value);break;
                        default: break;
                    }
                }
                TransactionFromSheet t = TransactionFromSheet.builder()
                        .type(type)
                        .description(description)
                        .mode(mode)
                        .amount((long)amount)
                        .account(account)
                        .build();
                long d = t.convertDateToMillis(date);
                t.setDate(d);
                t.setSpendAtAndCategory(category);
                transactions.add(t.convertToTransaction(user));

            }
        } catch (IOException | CsvValidationException | ParseException e) {
            throw new CustomGenericRuntimeException(e.getMessage());
        }
        return transactions;
    }

}
