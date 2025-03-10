package com.makeprojects.digitaltransactionsservice.transactions.core.service.definition;

import com.makeprojects.digitaltransactionsservice.transactions.database.model.Transaction;
import com.makeprojects.digitaltransactionsservice.transactions.dto.TransactionRequestDTO;
import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.kafka.event.classes.TransferResult;

public interface TransactionService extends CRUDService<Transaction> {

    /**
     * Initiates the requested transaction
     * @param transactionRequestDTO TransactionRequestDTO object
     * @return obj of created Transaction
     */
    Transaction initiateTransaction(TransactionRequestDTO transactionRequestDTO);

    /**
     * Completes the ongoing transaction
     * @param transferResult TransferResult object
     * @return obj of updated Transaction after completion
     */
    Transaction completeTransaction(TransferResult transferResult);
}
