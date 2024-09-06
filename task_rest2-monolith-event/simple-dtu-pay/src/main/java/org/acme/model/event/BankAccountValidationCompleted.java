package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankAccountValidationCompleted {
    private String correlationId;
    private boolean isValid;
}