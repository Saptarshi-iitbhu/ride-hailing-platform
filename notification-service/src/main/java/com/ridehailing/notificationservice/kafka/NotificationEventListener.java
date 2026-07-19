package com.ridehailing.notificationservice.kafka;

import com.ridehailing.notificationservice.dto.TripCancelledEvent;
import com.ridehailing.notificationservice.dto.TripConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventListener {

    @KafkaListener(topics = KafkaTopics.TRIP_CONFIRMED, groupId = "notification-service")
    public void onTripConfirmed(TripConfirmedEvent event) {
        log.info("📱 Notification sent to rider: Trip {} confirmed - your driver is on the way!", event.getTripId());
    }

    @KafkaListener(topics = KafkaTopics.TRIP_CANCELLED, groupId = "notification-service")
    public void onTripCancelled(TripCancelledEvent event) {
        log.info("📱 Notification sent to rider: Trip {} cancelled - {}", event.getTripId(), event.getReason());
    }
}