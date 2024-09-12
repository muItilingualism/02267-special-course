package org.acme.model;

public class Merchant extends Account {
    public Merchant(String id, String firstName, String lastName, String cpr, String bankAccountId) {
        super(id, firstName, lastName, cpr, bankAccountId);
    }
}
