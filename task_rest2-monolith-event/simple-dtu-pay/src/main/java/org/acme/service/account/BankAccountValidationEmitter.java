package org.acme.service.account;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class BankAccountValidationEmitter {

    @Inject
    @Channel("bank-account-validation-out")
    Emitter<String> bankAccountValidationEmitter;

    public Uni<Boolean> requestValidation(String bankAccountId) {
        return Uni.createFrom().emitter(emitter -> {
            bankAccountValidationEmitter.send(bankAccountId);
            emitter.complete(true);
        });
    }
}
