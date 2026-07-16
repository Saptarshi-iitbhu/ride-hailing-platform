package com.ridehailing.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationRequest {
    @NotBlank String name;
    @NotBlank String licenseNumber;
    @NotBlank String vehicleModel;
    @NotBlank String vehiclePlateNumber;
}
