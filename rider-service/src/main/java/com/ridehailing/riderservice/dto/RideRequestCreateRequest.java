package com.ridehailing.riderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestCreateRequest {
    double pickupLat;
    double pickupLng;
    double dropoffLat;
    double dropoffLng;
}
