package com.ridehailing.tripservice.dto;

import java.util.UUID;

public class PaymentEvents {

    public record PaymentAuthorizedEvent(UUID eventId, UUID tripId) {}

    public record PaymentFailedEvent(UUID eventId, UUID tripId, String reason) {}
}
