package com.ridehailing.riderservice.kafka;

import com.ridehailing.riderservice.dto.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideRequestEventProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishRideRequested(RideRequestedEvent event){
        kafkaTemplate.send(KafkaTopics.RIDE_REQUESTED, event.getRiderId(), event);
    }
}
