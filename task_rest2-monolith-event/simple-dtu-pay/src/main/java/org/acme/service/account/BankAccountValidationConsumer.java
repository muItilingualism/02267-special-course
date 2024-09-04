package org.acme.service.account;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationConsumer {

    @Inject
    BankAccountValidationEmitter validationEmitter;

    @Incoming("bank-account-validation-results")
    public void consumeValidationResult(JsonObject result) {
        String correlationId = result.getString("correlationId");
        boolean isValid = result.getBoolean("isValid");
        validationEmitter.receiveValidationResult(correlationId, isValid);
    }
}
