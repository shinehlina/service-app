package com.wine.to.up.service.configuration;

import com.wine.to.up.api.message.KafkaServiceEventOuterClass.KafkaServiceEvent;
import org.apache.kafka.common.serialization.Serializer;

public class EventSerializer implements Serializer<KafkaServiceEvent> {
    @Override
    public byte[] serialize(String topic, KafkaServiceEvent data) {
        return data.toByteArray();
    }
}
