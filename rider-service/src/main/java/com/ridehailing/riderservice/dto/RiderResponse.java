package com.ridehailing.riderservice.dto;

import com.ridehailing.riderservice.model.Rider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderResponse {
    UUID id;
    String name;
    Double rating;

    public static RiderResponse fromEntity(Rider rider){
        return new RiderResponse(rider.getId(), rider.getName(), rider.getRating());
    }
}
