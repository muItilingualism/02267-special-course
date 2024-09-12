package org.acme.model.event;

import java.util.concurrent.CompletableFuture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AccountRegistrationProcessed {
    private String correlationId;

    public abstract void completeFuture(CompletableFuture<String> future);
}
