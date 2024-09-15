package org.acme.facade.event.accountregistration;

import org.acme.model.event.CustomerAccountRegistrationCompleted;
import org.acme.model.event.CustomerAccountRegistrationFailed;
import org.acme.model.event.MerchantAccountRegistrationCompleted;
import org.acme.model.event.MerchantAccountRegistrationFailed;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountRegistrationHandler {

    @Inject
    AccountRegistrationProcessor processor;

    @Incoming("customer-account-registration-completed")
    public void handleCustomerAccountRegistrationCompleted(JsonObject jsonEvent) {
        CustomerAccountRegistrationCompleted event = jsonEvent.mapTo(CustomerAccountRegistrationCompleted.class);
        this.processor.processCustomerAccountRegistrationCompleted(event);
    }  
    
    @Incoming("customer-account-registration-failed")
    public void handleCustomerAccountRegistrationFailed(JsonObject jsonEvent) {
        CustomerAccountRegistrationFailed event = jsonEvent.mapTo(CustomerAccountRegistrationFailed.class);
        this.processor.processCustomerAccountRegistrationFailed(event);
    }  

    @Incoming("merchant-account-registration-completed")
    public void handleMerchantAccountRegistrationCompleted(JsonObject jsonEvent) {
        MerchantAccountRegistrationCompleted event = jsonEvent.mapTo(MerchantAccountRegistrationCompleted.class);
        this.processor.processMerchantAccountRegistrationCompleted(event);
    }  
    
    @Incoming("merchant-account-registration-failed")
    public void handleMerchantAccountRegistrationFailed(JsonObject jsonEvent) {
        MerchantAccountRegistrationFailed event = jsonEvent.mapTo(MerchantAccountRegistrationFailed.class);
        this.processor.processMerchantAccountRegistrationFailed(event);
    }  
}
