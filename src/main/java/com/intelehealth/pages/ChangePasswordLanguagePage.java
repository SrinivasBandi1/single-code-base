package com.intelehealth.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.intelehealth.base.BasePage;
import com.intelehealth.pages.LoginPage;
import com.intelehealth.util.ElementActions;

import io.qameta.allure.Step;

public class ChangePasswordLanguagePage extends BasePage {
	WebDriver driver;
	ElementActions elementActions;
	WebDriverWait wait;

	By DashboardMenu = By.xpath("//*[@data-test-id='labelDashboard']");
	By HamburgerMenu = By.xpath("//*[@data-test-id='iconProfileDropdown']");
	By ChangePasswordOption = By.xpath("//*[@data-test-id='btnChangePasswordMain']");
	By ChangeLanguageOption = By.xpath("//*[@data-test-id='btnSelectLanguageMain']");
	By ChangePasswordOldPWTextbox = By.xpath("//*[@data-test-id='etOldPass']");
	By ChangePasswordNewPWTextbox = By.xpath("//*[@data-test-id='etNewPass']");
	By ChangePasswordConfirmPWTextbox = By.xpath("//*[@data-test-id='etConfirmPass']");
	By ChangePasswordButton = By.xpath("//*[@data-test-id='btnSubmit']");
	By GeneratePasswordLink = By.xpath("//a[@data-test-id='linkGeneratePass']");
	By LogoutLink = By.xpath("//a[@data-test-id='linkLogout']");
	By LogoutConfirmYes = By.xpath("//button[@data-test-id='btnSubmitConfirmationModal']");
	By UsernameTextbox = By.xpath("//input[@data-test-id='etUsername']");
	By PasswordTextbox = By.xpath("//input[@data-test-id='etPassword']");
	By LoginButton = By.xpath("//button[@data-test-id='btnSubmit']");
	By SelectEnglishLanguage = By.xpath("//*[@data-test-id='radioEnglish']");
	By SelectRussianLanguage = By.xpath("//*[@data-test-id='radioRussian']");
	By SelectButton = By.xpath("//*[@data-test-id='btnSubmitSelectLanguageModal']");
	By ProfileLanguageEnglish = By.xpath("//h6[contains(text(),'Hello')]");

	// Constructor
	public ChangePasswordLanguagePage(WebDriver driver) {
		this.driver = driver;
		elementActions = new ElementActions(this.driver);
		wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
	}
	@Step("Change password from {0} to {1}")
	public boolean changePassword(String oldPassword, String newPassword, LoginPage loginPage) {
		openChangePassword();
		enterOldPassword(oldPassword);
		enterNewPassword(newPassword);
		enterConfirmPassword(newPassword);
		clickSubmit();
		waitForDashboard();
		loginPage.doLoginWithNewPassword("doctor1", newPassword);
		elementActions.doClick(DashboardMenu);
		openChangePassword();
		enterOldPassword(newPassword);
		enterNewPassword(oldPassword);
		enterConfirmPassword(oldPassword);
		clickSubmit();
		// Optionally, re-login to verify
		return isDashboardVisible();
	}
	/*
	 * @Step("Change password from {0} to {1}") public boolean changePassword(String
	 * oldPassword, String newPassword, LoginPage loginPage) { openChangePassword();
	 * enterOldPassword(oldPassword); enterNewPassword(newPassword);
	 * enterConfirmPassword(newPassword); clickSubmit(); boolean dashboardLoaded =
	 * isDashboardVisible(); if (!dashboardLoaded) return false; // Optionally,
	 * re-login to verify return dashboardLoaded; }
	 */

	@Step("Generate password for current password: {0}")
	public boolean generatePassword(String password) {
		openChangePassword();
		enterOldPassword(password);
		waitForElementClickable(GeneratePasswordLink);
		clickGeneratePassword();
		// Validate that password was generated (e.g., check for a toast or UI update)
		return isDashboardVisible(); // Or replace with more specific check if available
	}

	@Step("Login with new password for user: {0}")
	public boolean loginWithNewPassword(String username, String password) {
		logout();
		enterUsername(username);
		enterLoginPassword(password);
		clickLogin();
		return isDashboardVisible();
	}

	@Step("Change language to Russian and back to English")
	public boolean changeLanguage() {
		openChangeLanguage();
		selectRussianLanguage();
		selectEnglishLanguage();
		clickSelect();
		return isEnglishLanguageDisplayed();
	}

	public boolean isDashboardVisible() {
		try {
			waitForElementVisible(DashboardMenu);
			return elementActions.doIsDisplayed(DashboardMenu);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isEnglishLanguageDisplayed() {
		try {
			waitForElementVisible(ProfileLanguageEnglish);
			return elementActions.doIsDisplayed(ProfileLanguageEnglish);
		} catch (Exception e) {
			return false;
		}
	}

	// --- Reusable Steps ---
	@Step("Open Change Password screen")
	public void openChangePassword() {
		clickHamburgerMenu();
		waitForElementClickable(ChangePasswordOption);
		clickChangePassword();
	}

	@Step("Open Change Language screen")
	public void openChangeLanguage() {
		clickHamburgerMenu();
		waitForElementClickable(ChangeLanguageOption);
		clickChangeLanguage();
	}

	@Step("Click on Hamburger Menu")
	public void clickHamburgerMenu() {
		waitForElementClickable(HamburgerMenu);
		elementActions.doClick(HamburgerMenu);
	}

	@Step("Click on Change Password option")
	public void clickChangePassword() {
		elementActions.doClick(ChangePasswordOption);
	}

	@Step("Enter old password: {0}")
	public void enterOldPassword(String password) {
		waitForElementVisible(ChangePasswordOldPWTextbox);
		elementActions.doSendKeys(ChangePasswordOldPWTextbox, password);
	}

	@Step("Enter new password: {0}")
	public void enterNewPassword(String password) {
		elementActions.doSendKeys(ChangePasswordNewPWTextbox, password);
	}

	@Step("Enter confirm password: {0}")
	public void enterConfirmPassword(String password) {
		elementActions.doSendKeys(ChangePasswordConfirmPWTextbox, password);
	}

	@Step("Click on Submit button")
	public void clickSubmit() {
		waitForElementClickable(ChangePasswordButton);
		elementActions.doClick(ChangePasswordButton);
	}

	@Step("Click on Generate Password link")
	public void clickGeneratePassword() {
		elementActions.doClick(GeneratePasswordLink);
	}

	@Step("Logout")
	public void logout() {
		waitForElementClickable(LogoutLink);
		elementActions.doClick(LogoutLink);
		waitForElementClickable(LogoutConfirmYes);
		elementActions.doClick(LogoutConfirmYes);
	}

	@Step("Enter username: {0}")
	public void enterUsername(String username) {
		waitForElementVisible(UsernameTextbox);
		elementActions.doSendKeys(UsernameTextbox, username);
	}

	@Step("Enter login password: {0}")
	public void enterLoginPassword(String password) {
		elementActions.doSendKeys(PasswordTextbox, password);
	}

	@Step("Click Login button")
	public void clickLogin() {
		waitForElementClickable(LoginButton);
		elementActions.doClick(LoginButton);
	}

	@Step("Click Change Language option")
	public void clickChangeLanguage() {
		elementActions.doClick(ChangeLanguageOption);
	}

	@Step("Select Russian language")
	public void selectRussianLanguage() {
		waitForElementClickable(SelectRussianLanguage);
		elementActions.JavaScriptExecutorClick(SelectRussianLanguage);
	}

	@Step("Select English language")
	public void selectEnglishLanguage() {
		waitForElementClickable(SelectEnglishLanguage);
		elementActions.JavaScriptExecutorClick(SelectEnglishLanguage);
	}

	@Step("Click Select button")
	public void clickSelect() {
		waitForElementClickable(SelectButton);
		elementActions.doClick(SelectButton);
	}

	@Step("Verify English language is displayed")
	public void verifyEnglishLanguageDisplayed() {
		waitForElementVisible(ProfileLanguageEnglish);
		elementActions.doIsDisplayed(ProfileLanguageEnglish);
	}

	// --- Wait helpers ---
	public void waitForElementClickable(By locator) {
		wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	public void waitForElementVisible(By locator) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForDashboard() {
		waitForElementVisible(DashboardMenu);
	}
}