package com.wine.to.up.test.service.controller;

import com.wine.to.up.test.api.dto.ServiceMessage;
import com.wine.to.up.test.service.service.KafkaSendMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
@Slf4j
public class KafkaController {

    private KafkaSendMessageService kafkaSendMessageService;

    @Autowired
    public KafkaController(KafkaSendMessageService kafkaSendMessageService) {
        this.kafkaSendMessageService = kafkaSendMessageService;
    }

    @PostMapping(value = "/send/{topicName}")
    public void sendMessage(@PathVariable String topicName, @RequestParam String message) {
        sendMessageWithHeaders(topicName, new ServiceMessage(Collections.emptyMap(), message));
    }

    @PostMapping(value = "/send/{topicName}/headers")
    public void sendMessageWithHeaders(@PathVariable String topicName, @RequestBody @Valid ServiceMessage message) {
        AtomicInteger counter = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Headers headers = new RecordHeaders();
        message.getHeaders().forEach(headers::add);

        int sent = Stream.iterate(1, v -> v + 1)
                .limit(10)
                .map(n -> executorService.submit(() -> {
                    int numOfMessages = 10;
                    for (int j = 0; j < numOfMessages; j++) {
                        kafkaSendMessageService.sendMessage(topicName, headers, message.getMessage());
                        counter.incrementAndGet();
                    }
                    return numOfMessages;
                }))
                .map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("Error while sending in Kafka ", e);
                        return 0;
                    }
                })
                .mapToInt(Integer::intValue)
                .sum();

        log.info("Sent: " + sent);
    }
}
