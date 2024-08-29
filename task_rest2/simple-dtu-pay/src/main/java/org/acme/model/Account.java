package org.acme.model;

public class Account {
    private String accountId;
    private User user;

    public Account() {}

    public Account(String accountId, String cprNumber, String firstName, String lastName) {
        this.accountId = accountId;
        this.user = new User(cprNumber, firstName, lastName);
    }

    public String getAccountId() {
        return this.accountId;
    }

    public User getUser() {
        return this.user;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
