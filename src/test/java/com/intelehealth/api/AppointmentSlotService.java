package com.intelehealth.api;

import com.intelehealth.config.ConfigManager;
import com.intelehealth.util.DateTimeUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AppointmentSlotService {

    private final ConfigManager config;
    private static final DateTimeFormatter SLOT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    public AppointmentSlotService() {
        this.config = ConfigManager.getInstance();
    }

    // ── Step 1: Login ─────────────────────────────────────────────────────

    private String fetchBearerToken() {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "nurse1");
        loginBody.put("password", "Nurse@123");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .post(config.getAuthUrl() + "/auth/login");

        response.then().statusCode(200);

        String token = response.jsonPath().getString("token");
        System.out.println("[AppointmentSlotService] Token fetched successfully");
        return token;
    }

    // ── Step 2: Call slot API for a given date ────────────────────────────

    private List<Map<String, Object>> callSlotApi(String token,
                                                   String fromDate,
                                                   String toDate) {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .queryParam("fromDate",   fromDate)
                .queryParam("toDate",     toDate)
                .queryParam("speciality", "general physician")
                .get(config.getAppointmentUrl()
                        + "/api/appointment/getAppointmentSlots");

        response.then().statusCode(200);

        System.out.println("[AppointmentSlotService] Slots fetched for "
                + fromDate + " → " + response.asPrettyString());

        return response.jsonPath().getList("dates");
    }

    // ── Step 3: Try today, fall back to tomorrow if empty ─────────────────

    private List<Map<String, Object>> fetchAvailableSlots(String token) {

        // Try today first
        List<Map<String, Object>> slots = callSlotApi(
                token,
                DateTimeUtils.getTodayForSlotApi(),
                DateTimeUtils.getTodayForSlotApi()
        );

        // No slots today (night time / all booked) → try tomorrow
        if (slots == null || slots.isEmpty()) {
            System.out.println("[AppointmentSlotService] "
                    + "No slots today → falling back to tomorrow...");

            slots = callSlotApi(
                    token,
                    DateTimeUtils.getTomorrowForSlotApi(),
                    DateTimeUtils.getTomorrowForSlotApi()
            );
        }

        return slots;
    }

    // ── Step 4: Pick first slot that is at least 30 mins in the future ────

    public Map<String, Object> getFirstAvailableSlot() {
        String token = fetchBearerToken();
        List<Map<String, Object>> slots = fetchAvailableSlots(token);

        if (slots == null || slots.isEmpty()) {
            throw new RuntimeException(
                "[AppointmentSlotService] No slots found for today or tomorrow. "
                + "Check speciality or date range.");
        }

        LocalTime now = LocalTime.now();

        Map<String, Object> chosen = slots.stream()
                .filter(slot -> {
                    try {
                        // Filter out slots already passed or too close
                        LocalTime slotTime = LocalTime.parse(
                                (String) slot.get("slotTime"),
                                SLOT_TIME_FORMATTER);
                        return slotTime.isAfter(now.plusMinutes(30));
                    } catch (Exception e) {
                        return true; // include slot if time parse fails
                    }
                })
                .findFirst()
                .orElseGet(() -> {
                    // All today's slots are past → just take first from list
                    // (will be tomorrow's slots due to fallback above)
                    System.out.println("[AppointmentSlotService] "
                            + "All today slots past, using first from fallback");
                    return slots.get(0);
                });

        System.out.println("[AppointmentSlotService] Chosen slot → "
                + "date="  + chosen.get("slotDate")
                + " | day="   + chosen.get("slotDay")
                + " | time="  + chosen.get("slotTime")
                + " | dr="    + chosen.get("drName"));

        return chosen;
    }
}