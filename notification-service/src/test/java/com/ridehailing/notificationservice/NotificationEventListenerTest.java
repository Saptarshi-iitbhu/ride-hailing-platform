package com.ridehailing.notificationservice;

import com.ridehailing.notificationservice.dto.TripCancelledEvent;
import com.ridehailing.notificationservice.dto.TripConfirmedEvent;
import com.ridehailing.notificationservice.kafka.NotificationEventListener;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

class NotificationEventListenerTest {

    private final NotificationEventListener listener = new NotificationEventListener();

    @Test
    void onTripConfirmed_doesNotThrow() {
        TripConfirmedEvent event = TripConfirmedEvent.builder()
                .eventId(UUID.randomUUID())
                .tripId(UUID.randomUUID())
                .build();

        assertThatCode(() -> listener.onTripConfirmed(event)).doesNotThrowAnyException();
    }

    @Test
    void onTripCancelled_doesNotThrow() {
        TripCancelledEvent event = TripCancelledEvent.builder()
                .eventId(UUID.randomUUID())
                .tripId(UUID.randomUUID())
                .riderId("rider-1")
                .driverId("driver-1")
                .reason("payment_failed")
                .build();

        assertThatCode(() -> listener.onTripCancelled(event)).doesNotThrowAnyException();
    }
}