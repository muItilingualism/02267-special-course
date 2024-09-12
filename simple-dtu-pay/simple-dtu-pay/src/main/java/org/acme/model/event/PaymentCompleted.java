package org.acme.model.event;

import lombok.Getter;

@Getter
public class PaymentCompleted extends PaymentProcessed {
    public PaymentCompleted(String correlationId) {
        super(correlationId);
    }
}
