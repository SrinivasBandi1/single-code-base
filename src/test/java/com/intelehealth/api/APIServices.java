package com.intelehealth.api;

import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import com.google.gson.GsonBuilder;
import com.intelehealth.config.ConfigManager;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class APIServices {
	/*
	 * private static final String VISIT_PUSH_ENDPOINT =
	 * "https://pathqa.intelehealth.org/EMR-Middleware/webapi/push/pushdata";
	 * private static final String START_VISIT_ENDPOINT =
	 * "https://pathqa.intelehealth.org/openmrs/ws/rest/v1/encounter"; private
	 * static final String SIGN_AND_SUBMIT_ENDPOINT =
	 * "https://pathqa.intelehealth.org/openmrs/ws/rest/v1/encounter"; private
	 * static final String SEARCH_PATIENT_ENDPOINT =
	 * "https://pathqa.intelehealth.org/openmrs/ws/rest/v1/patient?q=%s&v=custom:(uuid,identifiers:(identifierType:(name),identifier),person)";
	 */
	// ── Dynamic URL resolution via ConfigManager ───────────────────────────────
	// Resolves once per class-load from: GitHub Actions env → -Dproject/-Denv →
	// TestNG XML → config.properties

	private static final String VISIT_PUSH_ENDPOINT;
	private static final String START_VISIT_ENDPOINT;
	private static final String SIGN_AND_SUBMIT_ENDPOINT;
	private static final String SEARCH_PATIENT_ENDPOINT;

	static {
		ConfigManager cfg = ConfigManager.getInstance();
		String middlewareBase = cfg.getMiddlewareBaseUrl();
		String openmrsBase = cfg.getOpenMrsBaseUrl();

		VISIT_PUSH_ENDPOINT = middlewareBase + "/push/pushdata";
		START_VISIT_ENDPOINT = openmrsBase + "/encounter";
		SIGN_AND_SUBMIT_ENDPOINT = openmrsBase + "/encounter";
		SEARCH_PATIENT_ENDPOINT = openmrsBase
				+ "/patient?q=%s&v=custom:(uuid,identifiers:(identifierType:(name),identifier),person)";
		System.out.println("Resolved API Endpoints:");
		System.out.println("VISIT_PUSH_ENDPOINT: " + VISIT_PUSH_ENDPOINT);
		System.out.println("START_VISIT_ENDPOINT: " + START_VISIT_ENDPOINT);
		System.out.println("SIGN_AND_SUBMIT_ENDPOINT: " + SIGN_AND_SUBMIT_ENDPOINT);
		System.out.println("SEARCH_PATIENT_ENDPOINT: " + SEARCH_PATIENT_ENDPOINT);
	}

	public static String createVisitUsingRestAssured(RequestSpecification request) {
		Response response = request.body(PayloadGenerator.createVisitUsingRestAssured()).post(VISIT_PUSH_ENDPOINT);
		System.out.println(response.asPrettyString());
		System.out.println(
				"======================================================================================================"
						+ response.jsonPath().getString("data.patientlist[0].openmrs_id"));
		return response.jsonPath().getString("data.patientlist[0].openmrs_id");

	}

	public static String createPriorityVisitUsingRestAssured(RequestSpecification request) {
		Response response = request.body(PayloadGenerator.createPriorityVisitUsingRestAssured())
				.post(VISIT_PUSH_ENDPOINT);
		System.out.println(response.asPrettyString());
		System.out.println(
				"======================================================================================================"
						+ response.jsonPath().getString("data.patientlist[0].openmrs_id"));
		return response.jsonPath().getString("data.patientlist[0].openmrs_id");

	}
	/*
	 * public static void createAppointmentUsingRestAssured(RequestSpecification
	 * request) { Response createVisitResponse =
	 * request.body(PayloadGenerator.createVisitUsingRestAssured()).post(
	 * VISIT_PUSH_ENDPOINT);
	 * 
	 * Map<String, Object> payload = PayloadGenerator.createAppointmentPayload();
	 * 
	 * // ✅ Proper JSON logging
	 * System.out.println(PayloadGenerator.toJson(payload));
	 * 
	 * Response response = request .body(payload) .post(VISIT_PUSH_ENDPOINT);
	 * 
	 * System.out.println("Appointment created → " + response.asPrettyString());
	 * response.then().statusCode(200); }
	 */
	// ── Create appointment (full chain) ───────────────────────────────────

	public static void createAppointmentUsingRestAssured(RequestSpecification request) {

		// Step 1: Create visit → extract openMrsId and patientId
		Response visitResponse = request.body(PayloadGenerator.createVisitUsingRestAssured()).post(VISIT_PUSH_ENDPOINT);

		System.out.println(visitResponse.asPrettyString());

		String openMrsId = visitResponse.jsonPath().getString("data.patientlist[0].openmrs_id"); 

		String patientId = visitResponse.jsonPath().getString("data.patientlist[0].uuid");

		System.out.println("[APIServices] openMrsId : " + openMrsId);
		System.out.println("[APIServices] patientId : " + patientId);

		// Step 2: Build appointment payload with real values
		Map<String, Object> payload = PayloadGenerator.createAppointmentPayload(openMrsId, patientId);

		System.out.println("[APIServices] Appointment payload → "
				+ new GsonBuilder().setPrettyPrinting().create().toJson(payload));

		// Step 3: Push appointment
		Response response = request.body(payload).post(VISIT_PUSH_ENDPOINT);

		System.out.println("[APIServices] Appointment response → " + response.asPrettyString());

		response.then().statusCode(200);
	}

	public static void startVisitUsingRestAssured(RequestSpecification request) {
		request.body(PayloadGenerator.startVisitNote()).post(START_VISIT_ENDPOINT);
	}

	public static void signAndSubmitUsingRestAssured(RequestSpecification request) {
		request.body(PayloadGenerator.signAndSubmit()).post(SIGN_AND_SUBMIT_ENDPOINT);
	}

	public static void searchPatient(RequestSpecification request, String openMRSID) {
		request.get(String.format(SEARCH_PATIENT_ENDPOINT, openMRSID)).then().assertThat().body("results[0].uuid",
				notNullValue());
		;
	}
}
