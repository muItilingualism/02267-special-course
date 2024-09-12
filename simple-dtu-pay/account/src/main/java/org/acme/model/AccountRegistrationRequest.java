package org.acme.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountRegistrationRequest {
    private String firstName;
    private String lastName;
    private String cpr;
    private String bankAccountId;
}
