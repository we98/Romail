package com.romail.romail.entity;


import java.util.ArrayList;

public class Account {

    private String account;
    private String password;
    private String emailType;

    private Account(String account, String password, String emailType){
        this.account = account;
        this.password = password;
        this.emailType = emailType;
    }
    private static Account currentAccount;
    public static void buildAccount(String account, String password, String emailType){
        if(currentAccount == null){
            currentAccount = new Account(account, password, emailType);
        }
    }
    public static Account getCurrentAccount(){
        return currentAccount;
    }

    public static void clearCurrentAccount(){
        currentAccount = null;
    }

    public String getCurrentAccountEmail() {
        return account;
    }

    public String getCurrentAccountPassword() {
        return password;
    }

    public String getCurrentAccountEmailType() {
        return emailType;
    }

    private ArrayList<Email> emails = null;
    public ArrayList<Email> getEmails(){
        return emails;
    }
    public void setEmails(ArrayList<Email> emails){
        this.emails = emails;
    }

    public void deleteOneEmail(int index){
        if(index >= 0 && index < emails.size()){
            emails.remove(index);
        }
    }

}
