package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountRegistrationProcessed {
    private String correlationId;
}
