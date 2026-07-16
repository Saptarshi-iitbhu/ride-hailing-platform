package com.ridehailing.driverservice.kafka;

import com.ridehailing.driverservice.dto.TripCancelledEvent;
import com.ridehailing.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DriverEventListner {

    private DriverService driverService;

    @KafkaListener(topics = KafkaTopics.TRIP_CANCELLED, groupId = "driver-service")
    public void onTripCancelled(TripCancelledEvent tripCancelledEvent) {
        UUID driverId = UUID.fromString(tripCancelledEvent.driverId());

        log.info("Trip {} cancelled ({}) - releasing driver {} back to available pool",
                tripCancelledEvent.tripId(), tripCancelledEvent.reason(), driverId);

        driverService.releaseDriver(driverId);
    }
}
