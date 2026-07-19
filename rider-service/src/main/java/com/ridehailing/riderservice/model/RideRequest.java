package com.ridehailing.riderservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rider_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    private Rider rider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideRequestedStatus status;

    private double pickupLat;
    private double pickupLng;
    private double dropoffLat;
    private double dropoffLng;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onCreate(){
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if(status == null){
            status = RideRequestedStatus.PENDING;
        }
    }

    @PreUpdate
    void onUpdate(){
        updatedAt = Instant.now();
    }
}
