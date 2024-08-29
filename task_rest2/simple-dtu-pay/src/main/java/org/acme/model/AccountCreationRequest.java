package org.acme.model;

public class AccountCreationRequest {
    private int balance;
    private User user;

    public AccountCreationRequest() {}

    public AccountCreationRequest(int balance, String cprNumber, String firstName, String lastName) {
        this.balance = balance;
        this.user = new User(cprNumber, firstName, lastName);
    }

    public int getBalance() {
        return balance;
    }

    public User getUser() {
        return user;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setUser(User user) {
        this.user = user;
    }
}