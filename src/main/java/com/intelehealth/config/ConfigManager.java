package com.intelehealth.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

	private static ConfigManager instance;
	private final Properties prop = new Properties();
	private String project;
	private String env;

	private ConfigManager() {
		loadProperties();
	}

	public static synchronized ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	private void loadProperties() {
		System.out.println(System.getProperty("user.dir") + "/src/main/java/com/intelehealth/config/config.qa.properties");
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "/src/main/java/com/intelehealth/config/config.qa.properties")) {
			prop.load(fis);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load config.properties", e);
		}

		// Priority: Maven -Dproject / -Denv → default.* in properties file
		project = System.getProperty("project", prop.getProperty("default.project", "ida")).trim().toLowerCase();

		env = System.getProperty("env", prop.getProperty("default.env", "dev")).trim().toLowerCase();

		validate(); // fail fast if combo doesn't exist

		System.out.println("[ConfigManager] Project  : " + project);
		System.out.println("[ConfigManager] Env      : " + env);
		System.out.println("[ConfigManager] Web URL  : " + getSiteURL());
		System.out.println("[ConfigManager] API URL  : " + getApiURL());
	}

	/** Called from BaseTest @BeforeSuite when TestNG XML params arrive */
	public void setProjectAndEnv(String proj, String envParam) {
		if (proj != null && !proj.isBlank()) {
			this.project = System.getProperty("project", proj).trim().toLowerCase();
		}
		if (envParam != null && !envParam.isBlank()) {
			this.env = System.getProperty("env", envParam).trim().toLowerCase();
		}
		validate();
		System.out.println("[ConfigManager] Updated → project: " + project + " | env: " + env);
	}

	/** Fail fast — catch wrong project/env combo before tests start */
	private void validate() {
		String url = prop.getProperty(project + "." + env + ".url");
		if (url == null || url.isBlank()) {
			throw new RuntimeException("[ConfigManager] No URL found for project='" + project + "' env='" + env
					+ "'. Check config.properties.");
		}
	}

	// ── Getters — all follow same {project}.{env}.{key} pattern ──────────────

	public String getSiteURL() {
		return prop.getProperty(project + "." + env + ".url");
	}

	public String getApiURL() {
		return prop.getProperty(project + "." + env + ".api.url");
	}

	public String getUsername() {
		return prop.getProperty(project + "." + env + ".username");
	}

	public String getPassword() {
		return prop.getProperty(project + "." + env + ".password");
	}

	/*
	 * public String getBrowser() { return prop.getProperty("browser", "chrome"); }
	 */
	public String getBrowser() {
	    return System.getProperty("browser", prop.getProperty("browser", "chrome"));
	}
	/*
	 * public boolean isHeadless() { return
	 * Boolean.parseBoolean(prop.getProperty("headless", "false")); }
	 */
	public boolean isHeadless() {
	    // System property (-Dheadless=true from CI) takes priority over config file
	    return Boolean.parseBoolean(
	        System.getProperty("headless", prop.getProperty("headless", "false"))
	    );
	}
	public boolean isIncognito() {
		return Boolean.parseBoolean(prop.getProperty("incognito", "false"));
	}

	public String getProject() {
		return project;
	}

	public String getEnv() {
		return env;
	}
	/**
	 * Base URL for the EMR Middleware push endpoint.
	 * e.g. https://pathqa.intelehealth.org/EMR-Middleware/webapi
	 */
	public String getMiddlewareBaseUrl() {
	    return prop.getProperty(project + "." + env + ".middleware.url");
	}

	/**
	 * Base URL for the OpenMRS REST API.
	 * e.g. https://pathqa.intelehealth.org/openmrs/ws/rest/v1
	 */
	public String getOpenMrsBaseUrl() {
	    return prop.getProperty(project + "." + env + ".openmrs.url");
	}
	public String getAuthUrl() {
	    return prop.getProperty(project + "." + env + ".auth.url");
	}

	public String getAppointmentUrl() {
	    return prop.getProperty(project + "." + env + ".appointment.url");
	}
}