package com.makeprojects.digitaltransactionsservice.transactions.core.service.implementation;

import com.makeprojects.digitaltransactionsservice.transactions.core.enums.TransactionStatus;
import com.makeprojects.digitaltransactionsservice.transactions.core.kafka.components.TransferEventProducer;
import com.makeprojects.digitaltransactionsservice.transactions.core.service.definition.TransactionService;
import com.makeprojects.digitaltransactionsservice.transactions.database.model.Transaction;
import com.makeprojects.digitaltransactionsservice.transactions.database.repository.TransactionRepository;
import com.makeprojects.digitaltransactionsservice.transactions.dto.TransactionRequestDTO;
import com.makeprojects.ewallet.shared.event.classes.TransferEvent;
import com.makeprojects.ewallet.shared.event.classes.TransferResult;
import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;
    private TransferEventProducer transferEventProducer;

    private static final String EMPTY_STRING = "";

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransferEventProducer transferEventProducer) {
        this.transactionRepository = transactionRepository;
        this.transferEventProducer = transferEventProducer;
    }

    /**
     * Gets Transaction with specified UUID
     * @param id UUID of transaction
     * @return Transaction object
     */
    @Override
    public Transaction get(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                errorMsg = String.format("UUID of transaction cannot be null.");
                log.error(errorMsg);
                throw new NullPointerException(errorMsg);
            }

            Optional<Transaction> optionalTransaction = this.transactionRepository.findById(id);
            if (optionalTransaction.isEmpty()) {
                errorMsg = String.format("Transaction with UUID %s is not found.", id);
                log.error(errorMsg);
                throw new NotFoundException(Transaction.class, "UUID", id);
            }

            log.info(String.format("Successfully retrieved Transaction with UUID '%s'.", id));
            return optionalTransaction.get();
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while retrieving Transaction with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Gets list of all Transactions
     * @return list of all transactions
     */
    @Override
    public List<Transaction> getAll() {
        try {
            List<Transaction> transactionList = this.transactionRepository.findAll();
            if (transactionList.isEmpty()) {
                log.error("No Transaction found.");
            }

            log.info("Successfully retrieved Transactions list.");
            return transactionList;
        } catch (Exception e) {
            log.error("Exception occurred while retrieving list of Transactions.");
            throw e;
        }
    }

    /**
     * Creates a new Transaction
     * @param entity Transaction object to create
     * @return created Transaction
     */
    @Override
    public Transaction create(Transaction entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Transaction entity cannot be null.");
                throw new NullPointerException("Transaction entity cannot be null.");
            }

            if ((entity.getTransactionId() != null) && (get(entity.getTransactionId()) != null)) {
                errorMsg = String.format("Transaction with UUID %s already exists.", entity.getTransactionId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            Transaction createdTransaction = this.transactionRepository.save(entity);
            if (createdTransaction == null) {
                log.error("Failed to create a Transaction.");
                throw new RuntimeException("Failed to create a Transaction.");
            }

            log.info(String.format("Successfully created a Transaction with UUID '%s'.", createdTransaction.getTransactionId()));
            return createdTransaction;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating a Transaction. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Creates a new Transaction
     * @param entity Transaction object to update
     * @return updated Transaction
     */
    @Override
    public Transaction update(Transaction entity) {
        String errorMsg = EMPTY_STRING;
        try {
            if (entity == null) {
                log.error("Transaction entity which needs to be updated cannot be null.");
                throw new NullPointerException("Transaction entity cannot be null.");
            }

            if (get(entity.getTransactionId()) == null) {
                errorMsg = String.format("Transaction with UUID %s doesn't exist in database.", entity.getTransactionId());
                log.error(errorMsg);
                throw new NotFoundException(Transaction.class, "UUID", entity.getTransactionId());
            }

            Transaction updatedTransaction = this.transactionRepository.save(entity);
            if (updatedTransaction == null) {
                errorMsg = String.format("Failed to updated a Transaction with UUID '%s'.", entity.getTransactionId());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info(String.format("Successfully updated a Transaction with UUID '%s'.", updatedTransaction.getTransactionId()));
            return updatedTransaction;
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while creating a Transaction. Exception: %s", e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Deletes Transaction with specified UUID
     * @param id UUID of transaction
     */
    @Override
    public void delete(UUID id) {
        String errorMsg = EMPTY_STRING;
        try {
            if (id == null) {
                log.error("Transaction entity UUID which needs to be deleted cannot be null.");
                throw new NullPointerException("Transaction entity UUID which needs to be deleted cannot be null.");
            }

            if (get(id) == null) {
                errorMsg = String.format("Transaction with UUID %s doesn't exist in database.", id);
                log.error(errorMsg);
                throw new NotFoundException(Transaction.class, "UUID", id);
            }

            this.transactionRepository.deleteById(id);
            log.info(String.format("Successfully deleted a Transaction with UUID '%s'.", id));
        } catch (Exception e) {
            errorMsg = String.format("Exception occurred while deleting a Transaction with UUID '%s'. Exception: %s", id, e);
            log.error(errorMsg);
            throw e;
        }
    }

    /**
     * Initiates the requested transaction
     * @param transactionRequestDTO TransactionRequestDTO object
     * @return obj of created Transaction
     */
    @Override
    public Transaction initiateTransaction(TransactionRequestDTO transactionRequestDTO) {
        try {
            Transaction transaction = Transaction.builder()
                    .accountId(transactionRequestDTO.getSenderAccountId())
                    .targetAccountId(transactionRequestDTO.getReceiverAccountId())
                    .transactionType(transactionRequestDTO.getTransactionType())
                    .transactionStatus(TransactionStatus.PENDING)
                    .amount(transactionRequestDTO.getAmount())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            Transaction createdTransaction = this.create(transaction);

            TransferEvent transferEvent = TransferEvent.builder()
                    .transactionId(createdTransaction.getTransactionId())
                    .accountId(createdTransaction.getAccountId())
                    .targetAccountId(createdTransaction.getTargetAccountId())
                    .amount(createdTransaction.getAmount())
                    .transactionType(createdTransaction.getTransactionType())
                    .timestamp(Instant.now())
                    .build();

            transferEventProducer.initiateTransferEvent(transferEvent);

            return createdTransaction;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Completes the ongoing transaction
     * @param transferResult TransferResult object
     * @return obj of updated Transaction after completion
     */
    @Override
    public Transaction completeTransaction(TransferResult transferResult) {
        try {
            Transaction transaction = this.get(transferResult.getTransactionId());

            if(transferResult.isSuccess()) {
                transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            } else {
                transaction.setTransactionStatus(TransactionStatus.FAILED);
            }

            transaction.setUpdatedAt(Instant.now());

            return this.update(transaction);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
