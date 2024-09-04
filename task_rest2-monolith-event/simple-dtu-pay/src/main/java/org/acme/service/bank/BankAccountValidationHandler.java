package org.acme.service.bank;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationHandler {

    @Inject
    BankService bankService;

    @Inject
    @Channel("bank-account-validation-results")
    Emitter<JsonObject> validationResultEmitter;

    @Incoming("bank-account-validation-requests")
    @Blocking
    public void handleValidationRequest(JsonObject message) {
        String bankAccountId = message.getString("bankAccountId");
        String correlationId = message.getString("correlationId");
        boolean isValid = bankService.validateAccount(bankAccountId);

        JsonObject result = new JsonObject()
                .put("bankAccountId", bankAccountId)
                .put("correlationId", correlationId)
                .put("isValid", isValid);

        validationResultEmitter.send(result);
    }
}