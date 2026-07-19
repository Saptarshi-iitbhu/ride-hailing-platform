package com.ridehailing.paymentservice.controller;

import com.ridehailing.paymentservice.dto.PaymentResponse;
import com.ridehailing.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<PaymentResponse> getPaymentForTrip(@PathVariable String tripId) {
        return paymentRepository.findByTripId(tripId)
                .map(PaymentResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}