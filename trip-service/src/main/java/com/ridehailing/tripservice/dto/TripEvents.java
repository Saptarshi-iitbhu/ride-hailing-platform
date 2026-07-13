package com.ridehailing.tripservice.dto;

import java.util.UUID;

public class TripEvents {

    public record TripCreatedEvent(UUID eventId, UUID tripId, String riderId, String driverId) {}

    public record TripConfirmedEvent(UUID eventId, UUID tripId) {}

    public record TripCancelledEvent(UUID eventId, UUID tripId, String riderId, String driverId, String reason) {}
}
