package org.acme.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankAccountIdAssembled {
    private String correlationId;
    private String bankAccountId;
}
