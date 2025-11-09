package service;

import domain.Account;
import domain.Transaction;

import java.util.List;

public interface BankService {
    String openAccount(String customerName,String customerEmail,String accountType);
    List<Account> listOfAccount();

    void deposite(String accountNumber,String Accounttype, Double depositeAmount, String note);
    void withDraw(String accountNumber, String type, Double withdrawAmount, String note);
    void transfer(String senderAccountNumber,String senderAccountType, String reciverAccountNumber,String reciverAccountType, Double transferAmount, String note);

    List<Transaction> getAccountStatement(String accountNumber);

    List<Account> searchAccountByName(String searchedName);

    List<Account> getAccountBalance(String accountNumber);
}
