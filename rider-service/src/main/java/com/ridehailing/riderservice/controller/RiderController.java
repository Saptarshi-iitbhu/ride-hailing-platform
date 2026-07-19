package com.ridehailing.riderservice.controller;

import com.ridehailing.riderservice.dto.RideRequestCreateRequest;
import com.ridehailing.riderservice.dto.RiderRegistrationRequest;
import com.ridehailing.riderservice.dto.RiderResponse;
import com.ridehailing.riderservice.model.RideRequest;
import com.ridehailing.riderservice.model.Rider;
import com.ridehailing.riderservice.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/riders")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @PostMapping
    public ResponseEntity<RiderResponse> registerRider(@Valid @RequestBody RiderRegistrationRequest request){
        Rider rider = Rider.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        Rider savedRider = riderService.registerRider(rider);
        return ResponseEntity.ok(RiderResponse.fromEntity(savedRider));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiderResponse> getRiderById(@PathVariable UUID id){
        Rider rider = riderService.getRider(id);
        return ResponseEntity.ok(RiderResponse.fromEntity(rider));
    }

    @PostMapping("/{id}/ride-requests")
    public ResponseEntity<UUID> createRiderRequest(@PathVariable UUID id, @Valid @RequestBody RideRequestCreateRequest request){
        RideRequest rideRequest = riderService.createRideRequest(
                id,
                request.getPickupLat(),
                request.getPickupLng(),
                request.getDropoffLat(),
                request.getDropoffLng()
        );

        return ResponseEntity.ok(rideRequest.getId());
    }
}
