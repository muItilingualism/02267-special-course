package org.acme.model.event;

import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MerchantAccountRegistrationCompleted extends AccountRegistrationProcessed {
    private String id;

    public MerchantAccountRegistrationCompleted(String correlationId, String id) {
        super(correlationId);
        this.id = id;
    }

    public void completeFuture(CompletableFuture<String> future) {
        future.complete(this.id);
    }
}