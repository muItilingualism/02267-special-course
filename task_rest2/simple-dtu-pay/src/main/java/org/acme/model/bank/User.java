package org.acme.model.bank;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Value;

@Value
@RegisterForReflection
public class User {
    private String cprNumber;
    private String firstName;
    private String lastName;
}