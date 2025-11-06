package repository;

import domain.Transaction;

import java.util.*;

public class TransactionRepo {
    private final Map<String, List<Transaction>> txByAccountNumber = new HashMap<>();

    public  List<Transaction> findByAccountNumber(String accountNumber) {
        return new ArrayList<>(txByAccountNumber.getOrDefault(accountNumber, Collections.emptyList()));
    }


    public void save(Transaction transaction) {
//        This line groups transactions by account number, creating a list of transactions for each account and automatically handling the list creation
        txByAccountNumber.computeIfAbsent(transaction.getAccountNumber(),k -> new ArrayList<>()).add(transaction);
    }
}
