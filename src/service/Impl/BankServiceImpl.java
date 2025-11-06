package service.Impl;

import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import repository.AccountRepo;
import repository.TransactionRepo;
import service.BankService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BankServiceImpl implements BankService {

  private final AccountRepo saveAccount = new AccountRepo();
  private final TransactionRepo saveTransaction = new TransactionRepo();

    @Override
    public String openAccount(String customerName, String customerEmail, String accountType) {
        String customerId = UUID.randomUUID().toString();
//        String accountNumber = UUID.randomUUID().toString();
        String accountNumber = getAccountNumber();
//        It will create an object of Account but does not save the account
//        to save the Account we will create a repository folder in which create Account repo class and store detail in map.
        Account newAccount = new Account(accountNumber,customerId,accountType,0.0);
        saveAccount.save(newAccount);
        Customer newCustomer = new Customer(customerId,customerName,customerEmail);
        return accountNumber;
    }

    @Override
    public List<Account> listOfAccount() {
        return saveAccount.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public void deposite(String accountNumber,String Accounttype, Double depositeAmount, String note) {
        Account account = saveAccount.findByNumber(accountNumber).orElseThrow(() -> new RuntimeException("Account not found : "+accountNumber));
        account.setBalance((account.getBalance()+depositeAmount));
        Transaction transaction = new Transaction(UUID.randomUUID().toString(),Accounttype, Type.DEPOSIT,accountNumber,depositeAmount, LocalDateTime.now(),note);
        saveTransaction.save(transaction);
    }

    @Override
    public void withDraw(String accountNumber, String Accounttype, Double withdrawAmount, String note) {
        Account account = saveAccount.findByNumber(accountNumber).orElseThrow(() -> new RuntimeException("Account not found : "+accountNumber));
//        The compareTo() method compares two numbers and returns a result:
//
//        Returns negative number (like -1) if the first number is smaller
//
//        Returns 0 if both numbers are equal
//
//        Returns positive number (like 1) if the first number is larger
        if(account.getBalance().compareTo(withdrawAmount) < 0){
            throw new RuntimeException("INSUFFICIENT BALANCE");
        }
        account.setBalance((account.getBalance()-withdrawAmount));
        Transaction transaction = new Transaction(UUID.randomUUID().toString(),Accounttype, Type.WITHDRAW,accountNumber,withdrawAmount, LocalDateTime.now(),note);
        saveTransaction.save(transaction);
    }

    @Override
    public void transfer(String senderAccountNumber, String senderAccountType,String reciverAccountNumber,String reciverAccountType, Double transferAmount, String note) {
        if(senderAccountNumber.equals(reciverAccountNumber)){
            throw new RuntimeException("Cannot transfer in same Account");
        }
        Account senderAccount = saveAccount.findByNumber(senderAccountNumber).orElseThrow(() -> new RuntimeException("Account not found : "+senderAccountNumber));
        Account reciverAccount = saveAccount.findByNumber(reciverAccountNumber).orElseThrow(() -> new RuntimeException("Account not found : "+reciverAccountNumber));

        if(senderAccount.getBalance().compareTo(transferAmount) < 0){
            throw new RuntimeException("INSUFFICIENT BALANCE,CANNOT TRANSFER");
        }
        senderAccount.setBalance((senderAccount.getBalance()-transferAmount));
        Transaction senderTransaction = new Transaction(UUID.randomUUID().toString(),senderAccountType, Type.TRANSFER_OUT,senderAccountNumber,transferAmount, LocalDateTime.now(),note);
        saveTransaction.save(senderTransaction);
        reciverAccount.setBalance((senderAccount.getBalance()+transferAmount));
        Transaction reciverTransaction = new Transaction(UUID.randomUUID().toString(),reciverAccountType, Type.TRANSFER_IN,reciverAccountNumber,transferAmount, LocalDateTime.now(),note);
        saveTransaction.save(reciverTransaction);
    }

    @Override
    public List<Transaction> getAccountStatement(String accountNumber) {
        return  saveTransaction.findByAccountNumber(accountNumber).stream().sorted(Comparator.comparing(Transaction::getTimestamp)).collect(Collectors.toList());
    }

    @Override
    public List<Account> searchAccountByName(String searchedName) {
        String name = (searchedName == null ) ? "" : searchedName.toLowerCase();
        return new ArrayList<>(saveAccount.findAccountByNames(name));
    }

    private String getAccountNumber() {
        int size = saveAccount.findAll().size()+1;  // will fetch all account and get size and add 1 to that
        return String.format("AC%06d",size);
    }
}
