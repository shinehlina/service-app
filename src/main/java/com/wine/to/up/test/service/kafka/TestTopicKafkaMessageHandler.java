package com.wine.to.up.test.service.kafka;

import com.wine.to.up.test.api.ServiceApiProperties;
import com.wine.to.up.test.api.message.KafkaServiceMessageOuterClass.KafkaServiceMessage;
import com.wine.to.up.test.service.domain.entity.Message;
import com.wine.to.up.test.service.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class TestTopicKafkaMessageHandler implements KafkaMessageHandler<String> {
    private final MessageRepository messageRepository;

    private final AtomicInteger counter = new AtomicInteger(0);

    private final ServiceApiProperties serviceApiProperties;

    @Autowired
    public TestTopicKafkaMessageHandler(MessageRepository messageRepository, ServiceApiProperties serviceApiProperties) {
        this.messageRepository = messageRepository;
        this.serviceApiProperties = serviceApiProperties;
        log.info("Handler for topic {} is creating...", serviceApiProperties.getTopicName());
    }

    @Override
    public void handle(String message) {
        counter.incrementAndGet();
        log.info("Message received from test topic: test, number of messages: {}", counter.get());
        messageRepository.save(new Message(message));
    }

    public void handle(KafkaServiceMessage kafkaServiceMessage) {
        //todo
    }
}
