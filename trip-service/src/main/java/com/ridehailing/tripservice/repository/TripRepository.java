package com.ridehailing.tripservice.repository;

import com.ridehailing.tripservice.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
}
