package org.acme.model.bank;

import lombok.Getter;

@Getter
public enum BankError {
    NO_DEBTOR("Debtor account does not exist"),
    NO_CREDITOR("Creditor account does not exist");

    private final String errorMessage;

    BankError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
