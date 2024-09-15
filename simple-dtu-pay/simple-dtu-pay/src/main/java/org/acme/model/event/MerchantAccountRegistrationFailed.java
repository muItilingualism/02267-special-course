package org.acme.model.event;

import java.util.concurrent.CompletableFuture;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MerchantAccountRegistrationFailed extends AccountRegistrationProcessed {
    private Throwable cause;

    public MerchantAccountRegistrationFailed(String correlationId, Throwable cause) {
        super(correlationId);
        this.cause = cause;
    }

    public void completeFuture(CompletableFuture<String> future) {
        future.completeExceptionally(this.cause);
    }
}
