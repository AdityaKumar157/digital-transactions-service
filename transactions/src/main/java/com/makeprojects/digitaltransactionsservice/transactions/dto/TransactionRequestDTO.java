package com.makeprojects.digitaltransactionsservice.transactions.dto;

import com.makeprojects.ewallet.shared.core.enums.transaction.TransactionEnums.TransactionType;
import lombok.*;

import java.util.UUID;

@Builder
@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private double amount;
    private TransactionType transactionType;
}
