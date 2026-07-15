package com.ridehailing.driverservice.service;

import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverLocationService {

    private static final String GEO_KEY = "drivers:locations";
    private static final String STATUS_KEY_PREFIX = "drivers:status:";

    private final RedisTemplate<String,Object> redisTemplate;
    private final GeoOperations<String,Object> geoOperations;

    public DriverLocationService(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.geoOperations = redisTemplate.opsForGeo();
    }

    public void updateLocation(String driverId, double  longitude, double latitude){
        geoOperations.add(GEO_KEY, new Point(longitude, latitude), driverId);
    }

    public void setAvailability(String driverId, boolean availability){
        redisTemplate.opsForValue().set(STATUS_KEY_PREFIX + driverId, availability ? "AVAILABLE" : "BUSY");
    }

    public boolean isAvailable(String driverId){
        Object status = redisTemplate.opsForValue().get(STATUS_KEY_PREFIX + driverId);
        return "AVAILABLE".equals(status);
    }

    public List<String> getNearByAvailableDrivers(double longitude, double latitude, double radiusKm){
        Circle searchArea = new Circle(new Point(longitude, latitude), new Distance(radiusKm, Metrics.KILOMETERS));

        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs
                .newGeoSearchArgs()
                .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
                geoOperations.search(GEO_KEY, GeoReference.fromCoordinate(longitude,latitude),
                        new Distance(radiusKm, Metrics.KILOMETERS), args);

        return results.getContent().stream()
                .map(result -> (String) result.getContent().getName())
                .filter(this::isAvailable)
                .collect(Collectors.toList());
    }

    public void removeDriver(String driverId) {
        geoOperations.remove(GEO_KEY, driverId);
        redisTemplate.delete(STATUS_KEY_PREFIX + driverId);
    }
}
