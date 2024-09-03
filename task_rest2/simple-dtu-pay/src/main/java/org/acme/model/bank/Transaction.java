package org.acme.model.bank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Value;

@Value
@RegisterForReflection
public class Transaction {
    private int id;
    private String debtor;
    private String creditor;
    private int amount;
    private int balance;
    private String description;
    private String time;
}
