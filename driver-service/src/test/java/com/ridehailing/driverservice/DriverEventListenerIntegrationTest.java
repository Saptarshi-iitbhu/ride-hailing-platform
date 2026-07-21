package com.ridehailing.driverservice;

import com.ridehailing.driverservice.dto.TripCancelledEvent;
import com.ridehailing.driverservice.model.Driver;
import com.ridehailing.driverservice.model.DriverStatus;
import com.ridehailing.driverservice.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class DriverEventListenerIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Test
    void whenTripCancelled_driverIsReleasedBackToAvailable(){
        UUID driverId = UUID.randomUUID();;

        Driver driver = Driver.builder()
                .uuid(driverId)
                .name("Test Driver")
                .licenseNumber("LIC-" + driverId)
                .vehicleModel("Toyota Corolla")
                .vehiclePlateNumber("PLATE-" + driverId)
                .driverStatus(DriverStatus.BUSY)
                .rating(4.8)
                .build();
        driverRepository.save(driver);

        TripCancelledEvent event = new TripCancelledEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "rider-1",
                driverId.toString(),
                "payment-failed"
        );

        kafkaTemplate.send("trip_cancelled", driverId.toString(), event);
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Driver updated = driverRepository.findById(driverId).orElseThrow();
            assertThat(updated.getDriverStatus()).isEqualTo(DriverStatus.AVAILABLE);
        });
    }
}
