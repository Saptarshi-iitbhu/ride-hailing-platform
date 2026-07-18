package com.ridehailing.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverMatchedEvent {
    UUID eventId;
    String riderId;
    String driverId;
    double pickupLat;
    double pickupLng;
    double dropoffLat;
    double dropoffLng;
}
