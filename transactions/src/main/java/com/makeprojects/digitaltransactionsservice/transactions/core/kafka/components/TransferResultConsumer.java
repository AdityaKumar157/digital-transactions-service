package com.makeprojects.digitaltransactionsservice.transactions.core.kafka.components;

import com.makeprojects.digitaltransactionsservice.transactions.core.service.definition.TransactionService;
import com.makeprojects.ewallet.shared.kafka.event.classes.TransferResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransferResultConsumer {

    private final TransactionService transactionService;

    @Autowired
    public TransferResultConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
            topics = "transfer-result",
            groupId = "transactions-group",
            concurrency = "3"  // Enables parallel processing for different partitions
    )
    public void processTransferResult(@Payload TransferResult transferResult, @Header(KafkaHeaders.RECEIVED_KEY) String partitionKey) {
        log.info("Processing transfer result from partition key {}: {}", partitionKey, transferResult);
        transactionService.completeTransaction(transferResult);
    }
}
