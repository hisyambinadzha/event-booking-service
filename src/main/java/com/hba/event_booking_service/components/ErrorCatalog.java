package com.hba.event_booking_service.components;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ErrorCatalog {
    private final Map<String,String> errors = new HashMap<>();
    
    public static final String _000 = "000";
    public static final String _100 = "100";
    public static final String _101 = "101";
    public static final String _102 = "102";
    public static final String _103 = "103";
    public static final String _104 = "104";
    public static final String _105 = "105";
    public static final String _106 = "106";
    public static final String _107 = "107";
    public static final String _108 = "108";
    public static final String _109 = "109";
    public static final String _110 = "110";
    public static final String _111 = "111";
    public static final String _112 = "112";
    public static final String _900 = "900";
    public static final String _901 = "901";
    public static final String _902 = "902";
    public static final String _999 = "999";

    public ErrorCatalog() {
        errors.put(_000, "Success.");
        errors.put(_100, "Validation failed.");
        errors.put(_101, "The email address is already in use. Please change your email address.");
        errors.put(_102, "The email address or password you entered is incorrect. Please try again.");
        errors.put(_103, "The email address you entered is not registered. Please register first.");
        errors.put(_104, "The event you looking for does not exist.");
        errors.put(_105, "The event you looking for is already past.");
        errors.put(_106, "The event you looking for is not open for booking.");
        errors.put(_107, "The event you looking for is already full.");
        errors.put(_108, "There are not enough seats available for your booking. \nSeats available: {0}");
        errors.put(_109, "You are not allowed to update your booking. \nReason: {0}");
        errors.put(_110, "Service return an empty result. \nReason: {0}");
        errors.put(_111, "You have already booked this event.");
        errors.put(_112, "No booking records found.");
        errors.put(_900, "This operation is not allowed. \nReason: {0}");
        errors.put(_901, "Unauthorized. \nReason: {0}");
        errors.put(_902, "Forbidden. \nReason: {0}");
        errors.put(_999, "An unexpected error has occurred.");
    }

    public String get(String code) {
        return errors.getOrDefault(code, "Unknown error.");
    }

    public String get(String code, Object... args) {
        String template = errors.getOrDefault(code, "Unknown error.");
        return MessageFormat.format(template, args);
    }
}
