package com.wine.to.up.service.configuration;

import com.wine.to.up.api.message.KafkaServiceEventOuterClass.KafkaServiceEvent;
import com.wine.to.up.service.kafka.BaseKafkaHandler;
import com.wine.to.up.service.kafka.KafkaMessageHandler;
import com.wine.to.up.service.kafka.TestTopicKafkaMessageHandler;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {
    /**
     * Library serialization path
     */
    private static final String STRING_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String PROTOBUF_SERIALIZER = "";

    /**
     * Library deserialization path
     */
    private static final String DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";

    /**
     * List of kafka servers
     */
    @Value("${spring.kafka.bootstrap-server}")
    private String brokers;

    /**
     * Application consumer group id
     */
    @Value("${spring.kafka.consumer.group-id}")
    private String applicationConsumerGroupId;

    @Bean
    public KafkaProducer<String, KafkaServiceEvent> kafkaProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, STRING_SERIALIZER);
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PROTOBUF_SERIALIZER);

        return new KafkaProducer<>(properties);
    }

    /**
     * Creating general consumer properties. Common for all the consumers
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Properties consumerProperties() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, applicationConsumerGroupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // todo sukhoa figure out the meaning
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);
        return properties;
    }


    /**
     * Creates consumer based on general properties.
     * <p>
     * Uses custom deserializer as the messages within single topic should be the same type. And
     * the messages in different topics can have different types and require different deserializers
     * <p>
     * Binds the consumer of the topic with the object which is responsible for handling messages from
     * this topic
     * <p>
     * From now on all the messages consumed from given topic will be delegate
     * to {@link KafkaMessageHandler#handle(Object)} of the given handler
     *
     * @param consumerProperties is the general consumer properties. {@link #consumerProperties()}
     * @param handler            which is responsible for handling messages from this topic
     */
    @Bean
    BaseKafkaHandler<String> anotherTopicHandler(Properties consumerProperties,
                                                 TestTopicKafkaMessageHandler handler) {
        // set appropriate deserializer
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DESERIALIZER);

        // bind consumer with topic name and with appropriate handler
        return new BaseKafkaHandler<>("test", new KafkaConsumer<>(consumerProperties), handler); // todo sukhoa topic property
    }
}
