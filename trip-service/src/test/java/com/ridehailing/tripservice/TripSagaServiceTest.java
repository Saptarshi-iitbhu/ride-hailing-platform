package com.ridehailing.tripservice;

import com.ridehailing.tripservice.dto.DriverMatchedEvent;
import com.ridehailing.tripservice.dto.PaymentEvents.PaymentAuthorizedEvent;
import com.ridehailing.tripservice.dto.PaymentEvents.PaymentFailedEvent;
import com.ridehailing.tripservice.kafka.TripEventProducer;
import com.ridehailing.tripservice.model.Trip;
import com.ridehailing.tripservice.model.TripStatus;
import com.ridehailing.tripservice.repository.TripRepository;
import com.ridehailing.tripservice.service.TripSagaService;
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
class TripSagaServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripEventProducer tripEventProducer;

    private TripSagaService tripSagaService;

    @BeforeEach
    void setUp() {
        tripSagaService = new TripSagaService(tripRepository, tripEventProducer);
    }

    @Test
    void handleDriverMatched_createsTripAndPublishesTripCreated() {
        DriverMatchedEvent event = new DriverMatchedEvent(
                UUID.randomUUID(), "rider-1", "driver-1", 12.9, 77.6, 12.95, 77.65
        );

        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> {
            Trip t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        tripSagaService.handleDriverMatched(event);

        ArgumentCaptor<Trip> tripCaptor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository, times(2)).save(tripCaptor.capture());
        verify(tripEventProducer, times(1)).publishTripCreated(any());

        Trip lastSaved = tripCaptor.getValue();
        assertThat(lastSaved.getStatus()).isEqualTo(TripStatus.AWAITING_PAYMENT);
    }

    @Test
    void handlePaymentAuthorized_confirmsTripAndPublishesTripConfirmed() {
        UUID tripId = UUID.randomUUID();
        Trip trip = Trip.builder().id(tripId).riderId("rider-1").driverId("driver-1")
                .status(TripStatus.AWAITING_PAYMENT).build();

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        tripSagaService.handlePaymentAuthorized(new PaymentAuthorizedEvent(UUID.randomUUID(), tripId));

        assertThat(trip.getStatus()).isEqualTo(TripStatus.CONFIRMED);
        verify(tripEventProducer).publishTripConfirmed(any());
    }

    @Test
    void handlePaymentFailed_cancelsTripAndPublishesCompensatingEvent() {
        UUID tripId = UUID.randomUUID();
        Trip trip = Trip.builder().id(tripId).riderId("rider-1").driverId("driver-1")
                .status(TripStatus.AWAITING_PAYMENT).build();

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        tripSagaService.handlePaymentFailed(new PaymentFailedEvent(UUID.randomUUID(), tripId, "card_declined"));

        assertThat(trip.getStatus()).isEqualTo(TripStatus.CANCELLED);
        verify(tripEventProducer).publishTripCancelled(any());
    }
}
