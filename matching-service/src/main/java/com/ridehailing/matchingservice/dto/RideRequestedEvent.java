package com.ridehailing.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {
    UUID eventId;
    String riderId;
    double pickupLat;
    double pickupLng;
    double dropoffLat;
    double dropoffLng;
}