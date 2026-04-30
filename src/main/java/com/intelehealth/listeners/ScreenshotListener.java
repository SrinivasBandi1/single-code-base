package com.intelehealth.listeners;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class ScreenshotListener extends TestListenerAdapter {

	private static WebDriver driver;

	public static void setDriver(WebDriver driver) {
		ScreenshotListener.driver = driver;
	}

	/*
	 * @Override public void onTestFailure(ITestResult result) {
	 * 
	 * captureScreenshot(result.getName() + "_failure"); }
	 */
	@Override
	public void onTestFailure(ITestResult result) {

		// ✅ Ignore SkipException (VERY IMPORTANT)
		if (result.getThrowable() instanceof org.testng.SkipException) {
			System.out.println("⚠️ Skipped test detected in failure hook. Ignoring screenshot.");
			return;
		}

		captureScreenshot(result.getName() + "_failure");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		System.out.println("⚠️ Test skipped: " + result.getName());
		// ❌ DO NOT take screenshot for skipped tests
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		captureScreenshot(result.getName() + "_success");
	}

	/*
	 * private void captureScreenshot(String fileName) { if (driver == null) {
	 * System.out.println("⚠️ Screenshot skipped: driver is null."); return; } if
	 * (driver instanceof org.openqa.selenium.remote.RemoteWebDriver) { var
	 * remoteDriver = (org.openqa.selenium.remote.RemoteWebDriver) driver; if
	 * (remoteDriver.getSessionId() == null) {
	 * System.out.println("⚠️ Screenshot skipped: driver session already quit.");
	 * return; } }
	 * 
	 * if (driver != null) { File srcFile = ((TakesScreenshot)
	 * driver).getScreenshotAs(OutputType.FILE); String timeStamp = new
	 * SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); String screenshotPath
	 * = "./screenshots/" + fileName + "_" + timeStamp + ".png"; File destFile = new
	 * File(screenshotPath);
	 * 
	 * try { FileUtils.copyFile(srcFile, destFile); //
	 * System.out.println("Screenshot captured: " + screenshotPath); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 * 
	 * }
	 * 
	 */
	private void captureScreenshot(String fileName) {

		try {
			if (driver == null) {
				System.out.println("⚠️ Screenshot skipped: driver is null.");
				return;
			}

			if (!(driver instanceof TakesScreenshot)) {
				System.out.println("⚠️ Screenshot skipped: driver does not support screenshots.");
				return;
			}

			if (driver instanceof org.openqa.selenium.remote.RemoteWebDriver) {
				var remoteDriver = (org.openqa.selenium.remote.RemoteWebDriver) driver;
				if (remoteDriver.getSessionId() == null) {
					System.out.println("⚠️ Screenshot skipped: driver session already quit.");
					return;
				}
			}

			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String screenshotPath = "./screenshots/" + fileName + "_" + timeStamp + ".png";
			File destFile = new File(screenshotPath);

			FileUtils.copyFile(srcFile, destFile);

		} catch (Exception e) {
			System.out.println("⚠️ Screenshot capture failed: " + e.getMessage());
		}
	}
}
