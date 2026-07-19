package com.ridehailing.paymentservice.kafka;

import com.ridehailing.paymentservice.dto.PaymentAuthorizedEvent;
import com.ridehailing.paymentservice.dto.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishPaymentAuthorized(PaymentAuthorizedEvent event){
        kafkaTemplate.send(KafkaTopics.PAYMENT_AUTHORIZED, event.getTripId().toString(), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event){
        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, event.getTripId().toString(), event);
    }
}
