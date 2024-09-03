package org.acme.model;

public class Customer extends Account {
    public Customer(String id, String firstName, String lastName, String cpr, String bankAccountId) {
        super(id, firstName, lastName, cpr, bankAccountId);
    }
}
