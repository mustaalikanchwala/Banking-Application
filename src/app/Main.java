package app;

import service.BankService;
import service.Impl.BankServiceImpl;

import java.sql.SQLOutput;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        BankService bankService = new BankServiceImpl();
        System.out.println("Welcome To Console Bank");
        Boolean running = true;
        while(running){
//        Pre Formatted String
            System.out.println("""
        1) Open Account
        2) Deposit
        3) Withdraw
        4) Transfer
        5) Account Statement
        6) List Accounts
        7) Search Accounts by Customer Name
        8) Account Balance
        0) Exit
""");
        System.out.print("CHOOSE : ");
        String choosedNumber = sc.nextLine().trim();
        System.out.println("YOUR CHOICE : "+choosedNumber);

        switch(choosedNumber){
            case "1" -> openAccount(sc,bankService);
            case "2" -> deposite(sc,bankService);
            case "3" -> withdraw(sc,bankService);
            case "4" -> transfer(sc,bankService);
            case "5" -> accStatement(sc,bankService);
            case "6" -> listAccount(sc,bankService);
            case "7" -> searchAccount(sc,bankService);
            case "8" -> getAccountBalance(sc,bankService);
            case "0" -> running = false;
        }
        }
    }

    private static void getAccountBalance(Scanner sc, BankService bankService) {
        System.out.println("ACCOUNT NUMBER : ");
        String accountNumber = sc.nextLine().trim();
        System.out.println("ACCOUNT TYPE :");
        String accountType =sc.nextLine().trim();
        bankService.getAccountBalance(accountNumber).forEach( a -> {
            System.out.println(a.getAccountNumber()+ " | " + a.getCustomerId() + " | "+ a.getAccountType() + " | "+ a.getBalance());
        });

    }

    private static void searchAccount(Scanner sc , BankService bankService) {
        System.out.print("SEARCH ACCOUNT BY NAME : ");
        String searchedName = sc.nextLine().trim();
        bankService.searchAccountByName(searchedName).forEach( a -> {
            System.out.println(a.getAccountNumber()+ " | " + a.getCustomerId() + " | "+ a.getAccountType() + " | "+ a.getBalance());
        });
    }

    private static void listAccount(Scanner sc,BankService bankService) {
        bankService.listOfAccount().forEach( a -> {
            System.out.println(a.getAccountNumber()+ " | "+ a.getAccountType() + " | "+ a.getBalance());
        });
    }

    private static void accStatement(Scanner sc,BankService bankService) {
        System.out.println("ACCOUNT NUMBER : ");
        String accountNumber = sc.nextLine().trim();
        System.out.println("ACCOUNT TYPE :");
        String accountType =sc.nextLine().trim();
        bankService.getAccountStatement(accountNumber).forEach(t -> {
            System.out.println( t.getAccountNumber() + " | " + t.getTimestamp() + " | " + t.getAccountType() + " | " + t.getType() + " | " + t.getAmount() + " | " + t.getNote());
        });

    }

    private static void transfer(Scanner sc,BankService bankService) {
        System.out.println("SENDER ACCOUNT NUMBER : ");
        String senderAccountNumber = sc.nextLine().trim();
        System.out.println("ACCOUNT TYPE :");
        String senderAccountType =sc.nextLine().trim();
        System.out.println("RECIVER ACCOUNT NUMBER : ");
        String reciverAccountNumber = sc.nextLine().trim();
        System.out.println("ACCOUNT TYPE :");
        String reciverAccountType =sc.nextLine().trim();
        System.out.println("AMOUNT : ");
        Double transferAmount = Double.valueOf(sc.nextLine().trim());
        System.out.println("Add Note : ");
        String note = sc.nextLine().trim();
        bankService.transfer(senderAccountNumber,senderAccountType,reciverAccountNumber,reciverAccountType,transferAmount,note);
        System.out.println("TRANSFER SUCCESSFULLY");
    }

    private static void withdraw(Scanner sc,BankService bankService) {
        System.out.println("ACCOUNT NUMBER : ");
        String accountNumber = sc.nextLine().trim();
        System.out.println("ACCOUNT TYPE :");
        String type =sc.nextLine().trim();
        System.out.println("AMOUNT : ");
        Double withdrawAmount = Double.valueOf(sc.nextLine().trim());
        System.out.println("Add Note : ");
        String note = sc.nextLine().trim();
        bankService.withDraw(accountNumber,type,withdrawAmount,note);
        System.out.println("WITHDRAW");
    }

    private static void deposite(Scanner sc,BankService bankService) {
        System.out.println("ACCOUNT NUMBER : ");
        String accountNumber = sc.nextLine().trim();
        System.out.println("ACCOUNT TYPE :");
        String type =sc.nextLine().trim();
        System.out.println("AMOUNT : ");
        Double depositeAmount = Double.valueOf(sc.nextLine().trim());
        System.out.println("Add Note : ");
        String note = sc.nextLine().trim();
        bankService.deposite(accountNumber,type,depositeAmount,note);
        System.out.println("DEPOSITED");
    }

    private static void openAccount(Scanner sc,BankService bankService) {
        System.out.println("CUSTOMER NAME: ");
        String customerName = sc.nextLine().trim();
        System.out.println("CUSTOMER EMAIL :");
        String customerEmail = sc.nextLine().trim();
        System.out.println("ACCOUNT TYPE(SAVING/CURRENT) : ");
        String accountType = sc.nextLine().trim();
        System.out.println("INITIAL DEPOSIT(MIN:530) :  ");
        String amountStr = sc.nextLine().trim();
        Double amountinit = Double.valueOf(amountStr);
        String accountNumber = bankService.openAccount(customerName,customerEmail,accountType);
        if(amountinit > 0.0){
            bankService.deposite(accountNumber,accountType,amountinit,"Initail Deposite");
        }
        System.out.println("ACCOUNT OPEN : " + accountNumber);
    }
}
