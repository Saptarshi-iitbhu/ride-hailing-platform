package com.ridehailing.riderservice;

import com.ridehailing.riderservice.kafka.RideRequestEventProducer;
import com.ridehailing.riderservice.model.Rider;
import com.ridehailing.riderservice.model.RideRequest;
import com.ridehailing.riderservice.model.RideRequestedStatus;
import com.ridehailing.riderservice.repository.RiderRepository;
import com.ridehailing.riderservice.repository.RiderRequestRepository;
import com.ridehailing.riderservice.service.RiderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiderServiceTest {

    @Mock
    private RiderRepository riderRepository;

    @Mock
    private RiderRequestRepository rideRequestRepository;

    @Mock
    private RideRequestEventProducer rideRequestEventProducer;

    private RiderService riderService;

    @BeforeEach
    void setUp() {
        riderService = new RiderService(riderRepository, rideRequestRepository, rideRequestEventProducer);
    }

    @Test
    void registerRider_savesRiderViaRepository() {
        Rider rider = Rider.builder().name("Priya Sharma").phoneNumber("+91-9876543210").build();

        when(riderRepository.save(rider)).thenReturn(rider);

        Rider saved = riderService.registerRider(rider);

        assertThat(saved.getName()).isEqualTo("Priya Sharma");
        verify(riderRepository, times(1)).save(rider);
    }

    @Test
    void getRider_throwsWhenRiderNotFound() {
        UUID riderId = UUID.randomUUID();
        when(riderRepository.findById(riderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riderService.getRider(riderId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(riderId.toString());
    }

    @Test
    void createRideRequest_savesRequestAndPublishesEvent() {
        UUID riderId = UUID.randomUUID();
        Rider rider = Rider.builder().id(riderId).name("Priya Sharma").build();

        when(riderRepository.findById(riderId)).thenReturn(Optional.of(rider));
        when(rideRequestRepository.save(any(RideRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RideRequest result = riderService.createRideRequest(
                riderId, 12.9716, 77.5946, 12.9352, 77.6146
        );

        assertThat(result.getStatus()).isEqualTo(RideRequestedStatus.PENDING);
        assertThat(result.getRider()).isEqualTo(rider);
        assertThat(result.getPickupLat()).isEqualTo(12.9716);

        verify(rideRequestEventProducer, times(1)).publishRideRequested(any());
    }

    @Test
    void createRideRequest_throwsWhenRiderNotFound_andNeverPublishesEvent() {
        UUID riderId = UUID.randomUUID();
        when(riderRepository.findById(riderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riderService.createRideRequest(riderId, 12.97, 77.59, 12.93, 77.61))
                .isInstanceOf(NoSuchElementException.class);

        verify(rideRequestEventProducer, never()).publishRideRequested(any());
        verify(rideRequestRepository, never()).save(any());
    }
}