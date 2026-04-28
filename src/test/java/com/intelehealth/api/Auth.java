package com.intelehealth.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class Auth {
	/**
	 * GOing forward we will be using these methods for visit creation as CHW,
	 * starting the visit as doctor, sign and submit
	 */

	/**
	 * API Authorization methods
	 * 
	 */

	public static RequestSpecification buildRequestWithNurseAuthorization(/* Map<String, Object> body */) {
		return RestAssured.given().header("authorization", "Basic c3Jpbml2YXNkOkRvY3RvciMxMjM=").
		// basic("nurse1", "Nurse@123")
				contentType(ContentType.JSON);
		// .body(body); // Set content type to JSON

	}

	public static RequestSpecification buildRequestWithDoctorAuthorization(/* Map<String, Object> body */) {
		return RestAssured.given().header("authorization", "Basic ZG9jdG9yMTpEb2N0b3JAMTIz").
		// basic("nurse1", "Nurse@123")
				contentType(ContentType.JSON);
		// .body(body); // Set content type to JSON

	}
}
