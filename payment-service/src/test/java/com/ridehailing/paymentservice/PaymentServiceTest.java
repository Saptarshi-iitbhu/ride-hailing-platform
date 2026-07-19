package com.ridehailing.paymentservice;

import com.ridehailing.paymentservice.kafka.PaymentEventProducer;
import com.ridehailing.paymentservice.model.Payment;
import com.ridehailing.paymentservice.model.PaymentStatus;
import com.ridehailing.paymentservice.repository.PaymentRepository;
import com.ridehailing.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, paymentEventProducer);
    }

    @Test
    void processPayment_savesExactlyOnePaymentRecord_regardlessOfOutcome() {
        UUID tripId = UUID.randomUUID();
        when(paymentRepository.findByTripId(tripId.toString())).thenReturn(Optional.empty());

        paymentService.processPayment(tripId, "rider-1");

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPayment_publishesExactlyOneEvent_neverBoth() {
        UUID tripId = UUID.randomUUID();
        when(paymentRepository.findByTripId(tripId.toString())).thenReturn(Optional.empty());

        paymentService.processPayment(tripId, "rider-1");

        int authorizedCalls = mockingDetails(paymentEventProducer).getInvocations().size();
        verify(paymentEventProducer, atMost(1)).publishPaymentAuthorized(any());
        verify(paymentEventProducer, atMost(1)).publishPaymentFailed(any());
        assertThat(authorizedCalls).isEqualTo(1); // exactly one of the two was called
    }

    @Test
    void processPayment_savedRecordMatchesWhicheverEventWasPublished() {
        UUID tripId = UUID.randomUUID();
        when(paymentRepository.findByTripId(tripId.toString())).thenReturn(Optional.empty());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        paymentService.processPayment(tripId, "rider-1");

        verify(paymentRepository).save(paymentCaptor.capture());
        Payment saved = paymentCaptor.getValue();

        if (saved.getStatus() == PaymentStatus.AUTHORIZED) {
            verify(paymentEventProducer).publishPaymentAuthorized(any());
            verify(paymentEventProducer, never()).publishPaymentFailed(any());
        } else {
            verify(paymentEventProducer).publishPaymentFailed(any());
            verify(paymentEventProducer, never()).publishPaymentAuthorized(any());
        }
    }

    @Test
    void processPayment_skipsEntirely_whenAlreadyProcessedForThisTrip() {
        UUID tripId = UUID.randomUUID();
        Payment existing = Payment.builder().tripId(tripId.toString()).status(PaymentStatus.AUTHORIZED).build();
        when(paymentRepository.findByTripId(tripId.toString())).thenReturn(Optional.of(existing));

        paymentService.processPayment(tripId, "rider-1");

        verify(paymentRepository, never()).save(any());
        verify(paymentEventProducer, never()).publishPaymentAuthorized(any());
        verify(paymentEventProducer, never()).publishPaymentFailed(any());
    }
}