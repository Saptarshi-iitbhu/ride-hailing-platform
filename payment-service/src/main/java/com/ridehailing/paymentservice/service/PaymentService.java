package com.ridehailing.paymentservice.service;

import com.ridehailing.paymentservice.dto.PaymentAuthorizedEvent;
import com.ridehailing.paymentservice.dto.PaymentFailedEvent;
import com.ridehailing.paymentservice.kafka.PaymentEventProducer;
import com.ridehailing.paymentservice.model.Payment;
import com.ridehailing.paymentservice.model.PaymentStatus;
import com.ridehailing.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private static final double FAILURE_RATE = 0.2;

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    public void processPayment(UUID tripId, String riderId){

        if(paymentRepository.findByTripId(tripId.toString()).isPresent()){
            log.info("Payment already processed for trip {}, skipping duplicate event", tripId);
            return;
        }

        boolean success = ThreadLocalRandom.current().nextDouble() >= FAILURE_RATE;
        double amount = 150.0 + ThreadLocalRandom.current().nextDouble(350.0);

        Payment payment = Payment.builder()
                .tripId(tripId.toString())
                .riderId(riderId)
                .amount(amount)
                .status(success ? PaymentStatus.AUTHORIZED : PaymentStatus.FAILED)
                .failureReason(success ? null : "card_declined")
                .build();

        paymentRepository.save(payment);

        if (success) {
            log.info("Payment authorized for trip {} (amount: {})", tripId, amount);
            paymentEventProducer.publishPaymentAuthorized(
                    new PaymentAuthorizedEvent(UUID.randomUUID(), tripId)
            );
        } else {
            log.info("Payment failed for trip {} - card_declined", tripId);
            paymentEventProducer.publishPaymentFailed(
                    new PaymentFailedEvent(UUID.randomUUID(), tripId, "card_declined")
            );
        }
    }
}
