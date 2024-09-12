package org.acme.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Account {
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String cpr;
    protected String bankAccountId;
}
