package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerAccountRegistrationProcessed {
    private String correlationId;
}
