package service.Impl;

import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientBalanceException;
import exceptions.ValidationException;
import repository.AccountRepo;
import repository.CustomerRepo;
import repository.TransactionRepo;
import service.BankService;
import utils.Validation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BankServiceImpl implements BankService {

  private final AccountRepo saveAccount = new AccountRepo();
  private final TransactionRepo saveTransaction = new TransactionRepo();
  private final CustomerRepo saveCustomer = new CustomerRepo();

//  Validation
    private final Validation<String> validatename = name -> {
        if(name == null || name.isBlank()) throw new ValidationException("Name is required");
};

    private final Validation<String> validateEmail = new Validation<String>() {
        @Override
        public void validate(String email) throws ValidationException {
            if(email == null || !email.contains("@")) throw  new ValidationException("Email is not valid ");
        }
    };
    private final Validation<String> validateAccountType = accType -> {
        if(accType == null || (!accType.equalsIgnoreCase("saving") && !accType.equalsIgnoreCase("current"))) throw new ValidationException("Choose a Valid Type : 1.SAVING 2.CURRENT");
    };

    private final Validation<Double> validateMinAmountRequried = amount -> {
        if(amount == null || amount < 530 ) throw new ValidationException("Min Ammount 530 requried");
    };


    @Override
    public String openAccount(String customerName, String customerEmail, String accountType) {
        validatename.validate(customerName);
        validateEmail.validate(customerEmail);
        validateAccountType.validate(accountType);
        String customerId = UUID.randomUUID().toString();
        Customer newCustomer = new Customer(customerId,customerName,customerEmail);
        saveCustomer.save(newCustomer);
        String accountNumber = getAccountNumber();
//        It will create an object of Account but does not save the account
//        to save the Account we will create a repository folder in which create Account repo class and store detail in map.
        Account newAccount = new Account(accountNumber,customerId,accountType,0.0);
        saveAccount.save(newAccount);
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
        validateMinAmountRequried.validate(depositeAmount);
        Account account = saveAccount.findByNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found : "+accountNumber));
        account.setBalance((account.getBalance()+depositeAmount));
        Transaction transaction = new Transaction(UUID.randomUUID().toString(),Accounttype, Type.DEPOSIT,accountNumber,depositeAmount, LocalDateTime.now(),note);
        saveTransaction.save(transaction);
    }

    @Override
    public void withDraw(String accountNumber, String Accounttype, Double withdrawAmount, String note) {
        validateMinAmountRequried.validate(withdrawAmount);
        Account account = saveAccount.findByNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found : "+accountNumber));
//        The compareTo() method compares two numbers and returns a result:
//
//        Returns negative number (like -1) if the first number is smaller
//
//        Returns 0 if both numbers are equal
//
//        Returns positive number (like 1) if the first number is larger
        if(account.getBalance().compareTo(withdrawAmount) < 0){
            throw new InsufficientBalanceException("INSUFFICIENT BALANCE");
        }
        account.setBalance((account.getBalance()-withdrawAmount));
        Transaction transaction = new Transaction(UUID.randomUUID().toString(),Accounttype, Type.WITHDRAW,accountNumber,withdrawAmount, LocalDateTime.now(),note);
        saveTransaction.save(transaction);
    }

    @Override
    public void transfer(String senderAccountNumber, String senderAccountType,String reciverAccountNumber,String reciverAccountType, Double transferAmount, String note) {
        validateMinAmountRequried.validate(transferAmount);
        if(senderAccountNumber.equals(reciverAccountNumber)){
            throw new ValidationException("Cannot transfer in same Account");
        }
        Account senderAccount = saveAccount.findByNumber(senderAccountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found : "+senderAccountNumber));
        Account reciverAccount = saveAccount.findByNumber(reciverAccountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found : "+reciverAccountNumber));

        if(senderAccount.getBalance().compareTo(transferAmount) < 0){
            throw new InsufficientBalanceException("INSUFFICIENT BALANCE,CANNOT TRANSFER");
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
//        List<Account> result = new ArrayList<>();
//        for(Customer c : saveCustomer.findAll()){
//            if(c.getName().toLowerCase().contains(name)){
//                result.addAll(saveAccount.findByCustomerId(c.getId()));
//            }
//        }
//        result.sort(Comparator.comparing(Account::getAccountNumber));
//    return result;
//        now above logic using stream
        return saveCustomer.findAll()
                .stream()
                .filter(c -> c.getName().toLowerCase().contains(name))
                .flatMap(c -> saveAccount.findByCustomerId(c.getId()).stream())
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccountBalance(String accountNumber) {
        return Collections.singletonList(saveAccount.findByNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found")));
    }

    private String getAccountNumber() {
        int size = saveAccount.findAll().size()+1;  // will fetch all account and get size and add 1 to that
        return String.format("AC%06d",size);
    }
}
