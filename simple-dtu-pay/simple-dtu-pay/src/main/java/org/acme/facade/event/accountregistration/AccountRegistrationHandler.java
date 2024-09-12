package org.acme.facade.event.accountregistration;

import org.acme.model.event.AccountRegistrationProcessed;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.smallrye.reactive.messaging.annotations.Merge;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountRegistrationHandler {

    @Inject
    AccountRegistrationProcessor processor;

    @Merge
    @Incoming("account-registration-processed")
    public void handleAccountRegistrationProcessed(AccountRegistrationProcessed event) {
        this.processor.processAccountRegistrationProcessed(event);
    }   
}
