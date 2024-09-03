package org.acme.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Value;

@Value
@RegisterForReflection
public class PaymentRequest {
    private int amount;
    private String customerId;
    private String merchantId;
}
