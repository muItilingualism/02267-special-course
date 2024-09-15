package org.acme.model.event;

import java.util.List;

import org.acme.model.PaymentRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllPaymentsAssembled {
    private String correlationId;
    private List<PaymentRequest> allPayments;
}
