package com.ridehailing.tripservice.dto;

import java.util.UUID;

public record DriverMatchedEvent(
        UUID eventId,
        String riderId,
        String driverId,
        double pickupLat,
        double pickupLng,
        double dropoffLat,
        double dropoffLng
) {}
