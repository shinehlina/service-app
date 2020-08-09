package com.wine.to.up.service.configuration;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wine.to.up.api.message.KafkaServiceEventOuterClass.KafkaServiceEvent;
import org.apache.kafka.common.serialization.Deserializer;

public class EventDeserializer implements Deserializer<KafkaServiceEvent> {
    @Override
    public KafkaServiceEvent deserialize(String s, byte[] bytes) {
       try {
           return KafkaServiceEvent.parseFrom(bytes);
       } catch (InvalidProtocolBufferException e) {
           //todo shine2: logs
           return null;
       }
    }
}
