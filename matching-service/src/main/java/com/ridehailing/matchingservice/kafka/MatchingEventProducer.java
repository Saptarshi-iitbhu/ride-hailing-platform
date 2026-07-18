package com.ridehailing.matchingservice.kafka;

import com.ridehailing.matchingservice.dto.DriverMatchedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchingEventProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;
    public void publishDriverMatched(DriverMatchedEvent event){
        kafkaTemplate.send(KafkaTopics.DRIVER_MATCHED, event.getDriverId(), event);
    }
}
