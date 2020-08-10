package com.wine.to.up.service.service;

import com.wine.to.up.api.ServiceApiProperties;
import com.wine.to.up.api.message.KafkaServiceEventOuterClass.KafkaServiceEvent;
import com.wine.to.up.service.annotations.InjectEventLogger;
import com.wine.to.up.service.components.AppMetrics;
import com.wine.to.up.service.logging.EventLogger;
import com.wine.to.up.service.logging.NotableEvents;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class KafkaSendMessageService {

    private final KafkaProducer<String, KafkaServiceEvent> producer; // todo sukhoa investigate thread safety
    private final ServiceApiProperties apiProperties;

    private final AppMetrics appMetrics;

    @InjectEventLogger
    @SuppressWarnings("unused")
    private EventLogger eventLogger;

    @Autowired
    public KafkaSendMessageService(KafkaProducer<String, KafkaServiceEvent> producer,
                                   AppMetrics appMetrics,
                                   ServiceApiProperties apiProperties) {
        this.appMetrics = appMetrics;
        this.producer = producer;
        this.apiProperties = apiProperties;
    }

    public void sendMessage(KafkaServiceEvent event) {
        String topicName = apiProperties.getTopicName();
        ProducerRecord<String, KafkaServiceEvent> record = new ProducerRecord<>(topicName, event);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                eventLogger.warn(NotableEvents.W_KAFKA_SEND_MESSAGE_FAILED, topicName);
                return;
            }
            log.debug("Message sent to Kafka topic: {}, event: {}", topicName, event);
            appMetrics.countKafkaMessageSent(topicName);
        });
    }
}
