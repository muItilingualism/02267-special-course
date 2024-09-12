package org.acme.model.bank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Value;

@Value
@RegisterForReflection
public class Payment {
    private int amount;
    private String creditor;
    private String debtor;
    private String description;
}
