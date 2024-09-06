package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankAccountValidationRequestedEvent {
    private String correlationId;
    private String bankAccountId;
}