package org.acme.model;

import java.util.List;

public class Account {
    private String id;
    private int balance;
    private List<Transaction> transactions;
    private User user;

    public Account() {}

    public Account(String accountId, int balance, List<Transaction> transactions, String cprNumber, String firstName, String lastName) {
        this.id = accountId;
        this.balance = balance;
        this.transactions = transactions;
        this.user = new User(cprNumber, firstName, lastName);
    }

    public String getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public void setId(String accountId) {
        this.id = accountId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
