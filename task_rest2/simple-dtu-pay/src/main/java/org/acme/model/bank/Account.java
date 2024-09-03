package org.acme.model.bank;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Value;

@Value
@RegisterForReflection
public class Account {
    private String id;
    private int balance;
    private List<Transaction> transactions;
    private User user;
}
