package org.acme.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class User {
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