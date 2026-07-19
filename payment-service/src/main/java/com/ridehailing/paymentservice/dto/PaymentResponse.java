package com.ridehailing.paymentservice.dto;

import com.ridehailing.paymentservice.model.Payment;
import com.ridehailing.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    UUID id;
    String tripId;
    Double amount;
    PaymentStatus status;
    String failureReason;

    public static PaymentResponse fromEntity(Payment payment){
        return new PaymentResponse(
                payment.getId(),
                payment.getTripId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getFailureReason()
        );
    }
}
