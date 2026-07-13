package com.ridehailing.tripservice.kafka;

import com.ridehailing.tripservice.dto.DriverMatchedEvent;
import com.ridehailing.tripservice.dto.PaymentEvents.PaymentAuthorizedEvent;
import com.ridehailing.tripservice.dto.PaymentEvents.PaymentFailedEvent;
import com.ridehailing.tripservice.service.TripSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripEventListener {

    private final TripSagaService tripSagaService;

    @KafkaListener(topics = KafkaTopics.DRIVER_MATCHED, groupId = "trip-service")
    public void onDriverMatched(DriverMatchedEvent event) {
        tripSagaService.handleDriverMatched(event);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_AUTHORIZED, groupId = "trip-service")
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        tripSagaService.handlePaymentAuthorized(event);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "trip-service")
    public void onPaymentFailed(PaymentFailedEvent event) {
        tripSagaService.handlePaymentFailed(event);
    }
}
