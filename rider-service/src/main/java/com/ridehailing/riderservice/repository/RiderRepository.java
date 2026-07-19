package com.ridehailing.riderservice.repository;

import com.ridehailing.riderservice.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RiderRepository extends JpaRepository<Rider,UUID> {
    Optional<Rider> findByPhoneNumber(String phoneNumber);
}
