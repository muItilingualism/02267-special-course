package org.acme.model.event;

import org.acme.model.AccountRegistrationRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerAccountRegistrationRequested {
    private String correlationId;
    private AccountRegistrationRequest request;
}
