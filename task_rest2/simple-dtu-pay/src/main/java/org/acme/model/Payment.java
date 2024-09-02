package org.acme.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Payment {
    private int amount;
    private String creditor;
    private String debtor;
    private String description;

    public Payment() {
    }

    public Payment(int amount, String creditorAccountId, String debtorAccountId, String description) {
        this.amount = amount;
        this.creditor = creditorAccountId;
        this.debtor = debtorAccountId;
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditorAccountId) {
        this.creditor = creditorAccountId;
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String debtorAccountId) {
        this.debtor = debtorAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
