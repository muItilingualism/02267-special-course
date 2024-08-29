package org.acme.model;

public class Account {
    private int balance;
    private User user;

    public Account() {}

    public Account(int balance, String cprNumber, String firstName, String lastName) {
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

    public static class User {
        private String cprNumber;
        private String firstName;
        private String lastName;

        public User() {}

        public User(String cprNumber, String firstName, String lastName) {
            this.cprNumber = cprNumber;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getCprNumber() {
            return cprNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setCprNumber(String cprNumber) {
            this.cprNumber = cprNumber;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}