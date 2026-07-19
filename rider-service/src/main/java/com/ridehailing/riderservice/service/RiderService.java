package com.ridehailing.riderservice.service;

import com.ridehailing.riderservice.dto.RideRequestedEvent;
import com.ridehailing.riderservice.dto.RiderRegistrationRequest;
import com.ridehailing.riderservice.kafka.RideRequestEventProducer;
import com.ridehailing.riderservice.model.RideRequest;
import com.ridehailing.riderservice.model.RideRequestedStatus;
import com.ridehailing.riderservice.model.Rider;
import com.ridehailing.riderservice.repository.RiderRepository;
import com.ridehailing.riderservice.repository.RiderRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final RiderRequestRepository riderRequestRepository;
    private final RideRequestEventProducer rideRequestEventProducer;

    public Rider registerRider(Rider rider){
        return riderRepository.save(rider);
    }

    public Rider getRider(UUID riderId){
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new NoSuchElementException("Rider not found: " + riderId));
    }

    public RideRequest createRideRequest(UUID riderId, double pickupLat, double pickupLng, double dropoffLat, double dropoffLng){
        Rider rider = getRider(riderId);

        RideRequest rideRequest = RideRequest.builder()
                .rider(rider)
                .pickupLat(pickupLat)
                .pickupLng(pickupLng)
                .dropoffLat(dropoffLat)
                .dropoffLng(dropoffLng)
                .status(RideRequestedStatus.PENDING)
                .build();

        RideRequest savedRideRequest = riderRequestRepository.save(rideRequest);

        RideRequestedEvent rideRequestedEvent = RideRequestedEvent.builder()
                .eventId(UUID.randomUUID())
                .riderId(riderId.toString())
                .pickupLat(pickupLat)
                .pickupLng(pickupLng)
                .dropoffLat(dropoffLat)
                .dropoffLng(dropoffLng)
                .build();

        rideRequestEventProducer.publishRideRequested(rideRequestedEvent);

        return savedRideRequest;
    }
}
