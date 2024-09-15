package org.acme.model.event;

import org.acme.model.PaymentRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRequested {
    private String correlationId;
    private PaymentRequest paymentRequest;
}
