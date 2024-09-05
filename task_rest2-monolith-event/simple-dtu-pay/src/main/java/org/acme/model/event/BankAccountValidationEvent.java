package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankAccountValidationEvent {
    private String correlationId;
    private BankAccountValidationEventType eventType;
    private String bankAccountId;
}