package org.acme.service.account.event;

import org.acme.model.event.BankAccountIdAssembled;
import org.acme.model.event.BankAccountIdRequested;
import org.acme.service.account.AccountService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BankAccountIdHandler {
    
    @Inject
    AccountService accountService;

    @Incoming("bank-account-id-requested")
    @Broadcast
    @Outgoing("bank-account-id-assembled")
    public BankAccountIdAssembled handleBankAccountIdRequested(JsonObject jsonEvent) {
        BankAccountIdRequested event = jsonEvent.mapTo(BankAccountIdRequested.class);

        String bankAccountId = accountService.getAccountBankId(event.getAccountId()).orElse(null);
        return new BankAccountIdAssembled(event.getCorrelationId(), bankAccountId);
    }
}
