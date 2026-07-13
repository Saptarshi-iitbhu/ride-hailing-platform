package com.ridehailing.tripservice.kafka;

public class KafkaTopics {
    public static final String DRIVER_MATCHED = "driver-matched";
    public static final String PAYMENT_AUTHORIZED = "payment-authorized";
    public static final String PAYMENT_FAILED = "payment-failed";

    public static final String TRIP_CREATED = "trip-created";
    public static final String TRIP_CONFIRMED = "trip-confirmed";
    public static final String TRIP_CANCELLED = "trip-cancelled";
}
