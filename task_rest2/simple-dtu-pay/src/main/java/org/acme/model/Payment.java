package org.acme.model;

public class Payment {
    private int amount;
    private String creditorAccountId;
    private String debtorAccountId;
    private String description;

    public Payment() {
    }

    public Payment(int amount, String creditorAccountId, String debtorAccountId, String description) {
        this.amount = amount;
        this.creditorAccountId = creditorAccountId;
        this.debtorAccountId = debtorAccountId;
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCreditorAccountId() {
        return creditorAccountId;
    }

    public void setCreditorAccountId(String creditorAccountId) {
        this.creditorAccountId = creditorAccountId;
    }

    public String getDebtorAccountId() {
        return debtorAccountId;
    }

    public void setDebtorAccountId(String debtorAccountId) {
        this.debtorAccountId = debtorAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
