package com.ridehailing.driverservice;

import com.ridehailing.driverservice.model.Driver;
import com.ridehailing.driverservice.model.DriverStatus;
import com.ridehailing.driverservice.repository.DriverRepository;
import com.ridehailing.driverservice.service.DriverLocationService;
import com.ridehailing.driverservice.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverLocationService driverLocationService;

    private DriverService driverService;

    @BeforeEach
    void setUp() {
        driverService = new DriverService(driverRepository, driverLocationService);
    }

    @Test
    void registerDriver_savesDriverViaRepository() {
        Driver driver = Driver.builder()
                .name("Ravi Kumar")
                .licenseNumber("DL1234")
                .vehicleModel("Toyota Prius")
                .vehiclePlateNumber("KA-01-AB-1234")
                .build();

        when(driverRepository.save(driver)).thenReturn(driver);

        Driver saved = driverService.registerDriver(driver);

        assertThat(saved.getName()).isEqualTo("Ravi Kumar");
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    void getDriver_throwsWhenDriverNotFound() {
        UUID driverId = UUID.randomUUID();
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getDriver(driverId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(driverId.toString());
    }

    @Test
    void goOnline_updatesStatusAndRegistersLocationInRedis() {
        UUID driverId = UUID.randomUUID();
        Driver driver = Driver.builder().uuid(driverId).driverStatus(DriverStatus.OFFILNE).build();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Driver result = driverService.goOnline(driverId, 77.5946, 12.9716);

        assertThat(result.getDriverStatus()).isEqualTo(DriverStatus.AVAILABLE);
        verify(driverLocationService).updateLocation(driverId.toString(), 77.5946, 12.9716);
        verify(driverLocationService).setAvailability(driverId.toString(), true);
    }

    @Test
    void goOffline_updatesStatusAndRemovesFromRedis() {
        UUID driverId = UUID.randomUUID();
        Driver driver = Driver.builder().uuid(driverId).driverStatus(DriverStatus.AVAILABLE).build();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Driver result = driverService.goOffline(driverId);

        assertThat(result.getDriverStatus()).isEqualTo(DriverStatus.OFFILNE);
        verify(driverLocationService).removeDriver(driverId.toString());
    }

    @Test
    void markBusy_setsStatusBusyAndUpdatesRedisAvailability() {
        UUID driverId = UUID.randomUUID();
        Driver driver = Driver.builder().uuid(driverId).driverStatus(DriverStatus.AVAILABLE).build();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        driverService.markBusy(driverId);

        assertThat(driver.getDriverStatus()).isEqualTo(DriverStatus.BUSY);
        verify(driverLocationService).setAvailability(driverId.toString(), false);
    }

    @Test
    void releaseDriver_putsDriverBackToAvailable_compensatingSagaAction() {
        UUID driverId = UUID.randomUUID();
        Driver driver = Driver.builder().uuid(driverId).driverStatus(DriverStatus.BUSY).build();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        driverService.releaseDriver(driverId);

        assertThat(driver.getDriverStatus()).isEqualTo(DriverStatus.AVAILABLE);
        verify(driverLocationService).setAvailability(driverId.toString(), true);
    }

    @Test
    void updateLocation_onlyTouchesRedis_notPostgres() {
        UUID driverId = UUID.randomUUID();

        driverService.updateLocation(driverId, 77.60, 12.97);

        verify(driverLocationService).updateLocation(driverId.toString(), 77.60, 12.97);
        verify(driverRepository, never()).save(any(Driver.class));
        verify(driverRepository, never()).findById(any(UUID.class));
    }
}