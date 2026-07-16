package com.ridehailing.driverservice.controller;

import com.ridehailing.driverservice.dto.DriverRegistrationRequest;
import com.ridehailing.driverservice.dto.DriverResponse;
import com.ridehailing.driverservice.dto.LocationUpdateRequest;
import com.ridehailing.driverservice.model.Driver;
import com.ridehailing.driverservice.repository.DriverRepository;
import com.ridehailing.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponse> registerDriver(@Valid @RequestBody DriverRegistrationRequest request){
        Driver driver = Driver.builder()
                .name(request.getName())
                .licenseNumber(request.getLicenseNumber())
                .vehicleModel(request.getVehicleModel())
                .vehiclePlateNumber(request.getVehiclePlateNumber())
                .build();

        Driver savedDriver = driverService.registerDriver(driver);
        return ResponseEntity.ok(DriverResponse.fromEntity(savedDriver));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable UUID id){
        Driver driver = driverService.getDriver(id);
        return ResponseEntity.ok(DriverResponse.fromEntity(driver));
    }

    @PostMapping("/{id}/online")
    public ResponseEntity<DriverResponse> goOnline(@PathVariable UUID id, @Valid @RequestBody LocationUpdateRequest request){
        Driver driver = driverService.goOnline(id, request.getLongitude(), request.getLatitude());
        return ResponseEntity.ok(DriverResponse.fromEntity(driver));
    }

    @PostMapping("/{id}/offline")
    public ResponseEntity<DriverResponse> goOffline(@PathVariable UUID id){
        Driver driver = driverService.goOffline(id);
        return ResponseEntity.ok(DriverResponse.fromEntity(driver));
    }

    @PutMapping("{id}/location")
    public ResponseEntity<Void> updateLocation(@PathVariable UUID id, @Valid @RequestBody LocationUpdateRequest request){
        driverService.updateLocation(id, request.getLongitude(), request.getLatitude());
        return ResponseEntity.noContent().build();
    }
}
