package com.makeprojects.digitaltransactionsservice.transactions.core.kafka.components;

import com.makeprojects.ewallet.shared.kafka.event.classes.TransferEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransferEventProducer {

    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;

    @Autowired
    public TransferEventProducer(KafkaTemplate<String, TransferEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void initiateTransferEvent(TransferEvent transferEvent) {
        String partitionKey = transferEvent.getAccountId().toString();   // Using accountId as key

        log.info("Publishing transfer event with key {}: {}", partitionKey, transferEvent);
        kafkaTemplate.send("transfer-event", partitionKey, transferEvent);
    }
}
