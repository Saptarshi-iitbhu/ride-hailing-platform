package com.ridehailing.driverservice.dto;

import com.ridehailing.driverservice.model.Driver;
import com.ridehailing.driverservice.model.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    UUID id;
    String name;
    String vehicleModel;
    String vehiclePlateNumber;
    DriverStatus status;
    Double rating;

    public static DriverResponse fromEntity(Driver driver) {
        DriverResponse response = new DriverResponse();
        response.setId(driver.getUuid());
        response.setName(driver.getName());
        response.setVehicleModel(driver.getVehicleModel());
        response.setVehiclePlateNumber(driver.getVehiclePlateNumber());
        response.setStatus(driver.getDriverStatus());
        response.setRating(driver.getRating());
        return response;
    }
}