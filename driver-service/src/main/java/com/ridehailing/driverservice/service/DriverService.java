package com.ridehailing.driverservice.service;

import com.ridehailing.driverservice.model.Driver;
import com.ridehailing.driverservice.model.DriverStatus;
import com.ridehailing.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverLocationService driverLocationService;

    public Driver registerDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public Driver getDriver(UUID driverId){
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new NoSuchElementException("Driver not found: " + driverId));
    }

    public Driver goOnline(UUID driverId, double longitude, double latitude){
        Driver driver = getDriver(driverId);
        driver.setDriverStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);

        driverLocationService.updateLocation(driverId.toString(), longitude, latitude);
        driverLocationService.setAvailability(driverId.toString(), true);

        return driver;
    }

    public Driver goOffline(UUID driverId){
        Driver driver = getDriver(driverId);
        driver.setDriverStatus(DriverStatus.OFFILNE);
        driverRepository.save(driver);

        driverLocationService.removeDriver(driverId.toString());
        return driver;
    }

    public void updateLocation(UUID driverId, double longitude, double latitude){
        driverLocationService.updateLocation(driverId.toString(), longitude, latitude);
    }

    public void markBusy(UUID driverId){
        Driver driver = getDriver(driverId);
        driver.setDriverStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
        driverLocationService.setAvailability(driverId.toString(), false);
    }

    public void releaseDriver(UUID driverId) {
        Driver driver = getDriver(driverId);
        driver.setDriverStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
        driverLocationService.setAvailability(driverId.toString(), true);
    }
}
