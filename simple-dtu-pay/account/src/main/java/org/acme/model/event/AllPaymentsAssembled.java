package org.acme.model.event;

import java.util.List;

import org.acme.model.PaymentRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AllPaymentsAssembled {
    private String correlationId;
    private List<PaymentRequest> allPayments;
}
