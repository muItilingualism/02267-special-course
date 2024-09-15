package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentProcessed {
    private String correlationId;
}
