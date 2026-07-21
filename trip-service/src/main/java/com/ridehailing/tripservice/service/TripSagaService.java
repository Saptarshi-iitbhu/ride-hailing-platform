package com.ridehailing.tripservice.service;

import com.ridehailing.tripservice.dto.DriverMatchedEvent;
import com.ridehailing.tripservice.dto.PaymentEvents.PaymentAuthorizedEvent;
import com.ridehailing.tripservice.dto.PaymentEvents.PaymentFailedEvent;
import com.ridehailing.tripservice.dto.TripEvents.TripCancelledEvent;
import com.ridehailing.tripservice.dto.TripEvents.TripConfirmedEvent;
import com.ridehailing.tripservice.dto.TripEvents.TripCreatedEvent;
import com.ridehailing.tripservice.kafka.TripEventProducer;
import com.ridehailing.tripservice.model.Trip;
import com.ridehailing.tripservice.model.TripStatus;
import com.ridehailing.tripservice.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripSagaService {

    private final TripRepository tripRepository;
    private final TripEventProducer tripEventProducer;

    public void handleDriverMatched(DriverMatchedEvent event) {
        Trip trip = Trip.builder()
                .riderId(event.riderId())
                .driverId(event.driverId())
                .pickupLat(event.pickupLat())
                .pickupLng(event.pickupLng())
                .dropoffLat(event.dropoffLat())
                .dropoffLng(event.dropoffLng())
                .status(TripStatus.CREATED)
                .build();

        Trip saved = tripRepository.save(trip);
        log.info("Trip {} created for rider {} / driver {}", saved.getId(), saved.getRiderId(), saved.getDriverId());

        tripEventProducer.publishTripCreated(
                new TripCreatedEvent(UUID.randomUUID(), saved.getId(), saved.getRiderId(), saved.getDriverId())
        );

        saved.setStatus(TripStatus.AWAITING_PAYMENT);
        tripRepository.save(saved);
    }

    public void handlePaymentAuthorized(PaymentAuthorizedEvent event) {
        tripRepository.findById(event.tripId()).ifPresentOrElse(trip -> {
            trip.setStatus(TripStatus.CONFIRMED);
            tripRepository.save(trip);

            tripEventProducer.publishTripConfirmed(new TripConfirmedEvent(UUID.randomUUID(), trip.getId()));
            log.info("Trip {} confirmed after payment authorization", trip.getId());
        }, () -> log.warn("PaymentAuthorized received for unknown trip {}", event.tripId()));
    }

    public void handlePaymentFailed(PaymentFailedEvent event) {
        tripRepository.findById(event.tripId()).ifPresentOrElse(trip -> {
            trip.setStatus(TripStatus.CANCELLED);
            tripRepository.save(trip);

            tripEventProducer.publishTripCancelled(
                    new TripCancelledEvent(UUID.randomUUID(), trip.getId(), trip.getRiderId(), trip.getDriverId(), event.reason())
            );
            log.info("Trip {} cancelled - compensating for failed payment: {}", trip.getId(), event.reason());
        }, () -> log.warn("PaymentFailed received for unknown trip {}", event.tripId()));
    }
}
