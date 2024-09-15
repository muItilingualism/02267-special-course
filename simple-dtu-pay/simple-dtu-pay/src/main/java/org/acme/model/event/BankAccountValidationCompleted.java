package org.acme.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankAccountValidationCompleted {
    private String correlationId;
    @JsonProperty("isValid")
    private boolean isValid;
}