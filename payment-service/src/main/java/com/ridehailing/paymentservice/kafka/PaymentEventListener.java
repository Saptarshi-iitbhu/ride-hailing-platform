package com.ridehailing.paymentservice.kafka;

import com.ridehailing.paymentservice.dto.TripCreatedEvent;
import com.ridehailing.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = KafkaTopics.TRIP_CREATED, groupId = "payment-service")
    public void onTripCreated(TripCreatedEvent event){
        log.info("Trip {} created - processing payment for rider {}", event.getTripId(), event.getRiderId());

        paymentService.processPayment(event.getTripId(), event.getRiderId());
    }
}
