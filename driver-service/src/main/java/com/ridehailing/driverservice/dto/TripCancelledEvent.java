package com.ridehailing.driverservice.dto;

import java.util.UUID;

public record TripCancelledEvent(UUID eventId, UUID tripId, String riderId, String driverId, String reason) {}