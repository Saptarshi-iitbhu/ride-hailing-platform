package com.ridehailing.matchingservice.kafka;

import com.ridehailing.matchingservice.dto.DriverMatchedEvent;
import com.ridehailing.matchingservice.dto.RideRequestedEvent;
import com.ridehailing.matchingservice.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchingEventListener {

    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;

    private final MatchingService matchingService;
    private final MatchingEventProducer matchingEventProducer;

    @KafkaListener(topics = KafkaTopics.RIDE_REQUESTED, groupId = "matching-service")
    public void onRideRequested(RideRequestedEvent event){
        log.info("Ride requested by rider {} at ({}, {})", event.getRiderId(), event.getPickupLat(), event.getPickupLng());

        Optional<String> matchedDriverId = matchingService.findNearestAvailableDriver(
                event.getPickupLng(), event.getPickupLat(), DEFAULT_SEARCH_RADIUS_KM
        );

        matchedDriverId.ifPresentOrElse(
                driverId ->{
                    log.info("Matched rider {} with driver {}", event.getRiderId(), driverId);

                    DriverMatchedEvent matchedEvent = DriverMatchedEvent.builder()
                            .eventId(UUID.randomUUID())
                            .riderId(event.getRiderId())
                            .driverId(driverId)
                            .pickupLat(event.getPickupLat())
                            .pickupLng(event.getPickupLng())
                            .dropoffLat(event.getDropoffLat())
                            .dropoffLng(event.getDropoffLng())
                            .build();

                    matchingEventProducer.publishDriverMatched(matchedEvent);
                },
                () -> log.warn("No available driver found within {}km for rider {}", DEFAULT_SEARCH_RADIUS_KM, event.getRiderId())
        );
    }
}
