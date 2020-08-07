package com.wine.to.up.service.kafka;

public interface KafkaMessageHandler<Message> {

    void handle(Message message);
}
