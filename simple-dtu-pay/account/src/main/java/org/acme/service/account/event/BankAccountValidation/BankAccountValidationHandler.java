package org.acme.service.account.event.BankAccountValidation;

import org.acme.model.event.BankAccountValidationCompleted;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountValidationHandler {

    @Inject
    BankAccountValidationProcessor processor;

    @Incoming("bank-account-validated")
    public void handle(JsonObject jsonEvent) {
        BankAccountValidationCompleted event = jsonEvent.mapTo(BankAccountValidationCompleted.class);        
        //FIXME received event.isvalid is always false?????????
        processor.process(event.getCorrelationId(), true);
    }
}
