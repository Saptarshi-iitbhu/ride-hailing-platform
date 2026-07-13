package com.ridehailing.tripservice.kafka;

import com.ridehailing.tripservice.dto.TripEvents.TripCancelledEvent;
import com.ridehailing.tripservice.dto.TripEvents.TripConfirmedEvent;
import com.ridehailing.tripservice.dto.TripEvents.TripCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTripCreated(TripCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.TRIP_CREATED, event.tripId().toString(), event);
    }

    public void publishTripConfirmed(TripConfirmedEvent event) {
        kafkaTemplate.send(KafkaTopics.TRIP_CONFIRMED, event.tripId().toString(), event);
    }

    public void publishTripCancelled(TripCancelledEvent event) {
        kafkaTemplate.send(KafkaTopics.TRIP_CANCELLED, event.tripId().toString(), event);
    }
}
