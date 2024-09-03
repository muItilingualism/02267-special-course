package org.acme.model;

public class AccountRegistrationRequest {
    private String firstName;
    private String lastName;
    private String cpr;
    private String bankAccountId;

    public AccountRegistrationRequest() {
    }

    public AccountRegistrationRequest(String firstName, String lastName, String cpr, String bankAccountId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpr = cpr;
        this.bankAccountId = bankAccountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
