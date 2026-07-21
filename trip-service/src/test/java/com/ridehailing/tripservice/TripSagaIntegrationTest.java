package com.ridehailing.tripservice;

import com.ridehailing.tripservice.dto.PaymentEvents;
import com.ridehailing.tripservice.model.Trip;
import com.ridehailing.tripservice.model.TripStatus;
import com.ridehailing.tripservice.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

public class TripSagaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void whenPaymentFails_tripIsCancelled_compensatingActionFires() {
        UUID tripId = UUID.randomUUID();
        Trip trip = Trip.builder()
                .id(tripId)
                .riderId("rider-1")
                .driverId("driver-1")
                .status(TripStatus.AWAITING_PAYMENT)
                .build();
        tripRepository.save(trip);

        PaymentEvents.PaymentFailedEvent event =
                new PaymentEvents.PaymentFailedEvent(UUID.randomUUID(), tripId, "card_declined");
        kafkaTemplate.send("payment-failed", tripId.toString(), event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Trip updated = tripRepository.findById(tripId).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(TripStatus.CANCELLED);
        });
    }
}