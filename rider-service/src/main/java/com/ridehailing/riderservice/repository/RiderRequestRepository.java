package com.ridehailing.riderservice.repository;

import com.ridehailing.riderservice.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RiderRequestRepository extends JpaRepository<RideRequest, UUID> {
    List<RideRequest> findByRiderId(UUID riderId);
}
