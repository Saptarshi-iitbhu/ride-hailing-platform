package com.ridehailing.matchingservice;

import com.ridehailing.matchingservice.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private GeoOperations<String, Object> geoOperations;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
        matchingService = new MatchingService(redisTemplate);
    }

    @Test
    void findNearestAvailableDriver_returnsDriver_whenNearbyAndAvailable() {
        GeoResult<RedisGeoCommands.GeoLocation<Object>> result =
                new GeoResult<>(new RedisGeoCommands.GeoLocation<>("driver-101", new Point(77.60, 12.97)),
                        new Distance(0.5, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = new GeoResults<>(List.of(result));

        when(geoOperations.search(eq("drivers:locations"), any(), any(Distance.class), any()))
                .thenReturn(geoResults);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("driver:status:driver-101")).thenReturn("AVAILABLE");

        Optional<String> matched = matchingService.findNearestAvailableDriver(77.60, 12.97, 5.0);

        assertThat(matched).contains("driver-101");
    }

    @Test
    void findNearestAvailableDriver_returnsEmpty_whenNearbyDriverIsBusy() {
        GeoResult<RedisGeoCommands.GeoLocation<Object>> result =
                new GeoResult<>(new RedisGeoCommands.GeoLocation<>("driver-102", new Point(77.60, 12.97)),
                        new Distance(0.5, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults = new GeoResults<>(List.of(result));

        when(geoOperations.search(eq("drivers:locations"), any(), any(Distance.class), any()))
                .thenReturn(geoResults);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("driver:status:driver-102")).thenReturn("BUSY");

        Optional<String> matched = matchingService.findNearestAvailableDriver(77.60, 12.97, 5.0);

        assertThat(matched).isEmpty();
    }

    @Test
    void findNearestAvailableDriver_returnsEmpty_whenNoDriversNearby() {
        GeoResults<RedisGeoCommands.GeoLocation<Object>> emptyResults = new GeoResults<>(List.of());

        when(geoOperations.search(eq("drivers:locations"), any(), any(Distance.class), any()))
                .thenReturn(emptyResults);

        Optional<String> matched = matchingService.findNearestAvailableDriver(77.60, 12.97, 5.0);

        assertThat(matched).isEmpty();
    }
}