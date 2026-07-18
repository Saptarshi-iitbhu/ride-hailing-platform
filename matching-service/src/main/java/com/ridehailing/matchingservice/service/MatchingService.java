package com.ridehailing.matchingservice.service;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private static final String GEO_KEY = "drivers:locations";
    private static final String STATUS_KEY_PREFIX = "driver:status:";

    private final RedisTemplate<String,Object> redisTemplate;
    private final GeoOperations<String,Object> geoOperations;

    public MatchingService(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.geoOperations = redisTemplate.opsForGeo();
    }

    public Optional<String> findNearestAvailableDriver(double pickupLng, double pickupLat, double radiusKm){
        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs
                .newGeoSearchArgs()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = geoOperations.search(GEO_KEY, GeoReference.fromCoordinate(pickupLng, pickupLat),
                new Distance(radiusKm, Metrics.KILOMETERS), args);

        List<String> nearByDriversId = results.getContent()
                .stream()
                .map(result -> (String) result.getContent().getName())
                .collect(Collectors.toList());

        return nearByDriversId.stream()
                .filter(this::isAvailable)
                .findFirst();
    }

    private boolean isAvailable(String driverId) {
        Object status = redisTemplate.opsForValue().get(STATUS_KEY_PREFIX + driverId);
        return "AVAILABLE".equals(status);
    }
}
