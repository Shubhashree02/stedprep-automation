package com.stedprep.automation.pages;

import com.stedprep.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ParentRegistrationPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ParentRegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // =======================
    // Navigation
    // =======================
    public void open() {
        driver.get(
                ConfigReader.get("base.url")
                        + ConfigReader.get("parent.register.path")
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(firstName));
    }

    // =======================
    // Locators (LABEL-BASED — FIX)
    // =======================
    private By firstName =
            By.xpath("//label[text()='First Name']/following::input[1]");

    private By lastName =
            By.xpath("//label[text()='Last Name']/following::input[1]");

    private By email =
            By.xpath("//label[text()='Email']/following::input[1]");

    private By password =
            By.xpath("//label[text()='Password']/following::input[1]");

    private By confirmPassword =
            By.xpath("//label[text()='Confirm Password']/following::input[1]");

    private By phoneNumber =
            By.xpath("//input[@placeholder='Enter your phone number (optional)']");

    private By continueButton =
            By.xpath("//button[@type='submit']");

    // =======================
    // Actions (NORMAL sendKeys)
    // =======================
    public void type(By locator, String value) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        driver.findElement(locator).click();
        driver.findElement(locator).clear();
        driver.findElement(locator).sendKeys(value);
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        driver.findElement(continueButton).click();
    }

    // =======================
    // Composite Action
    // =======================
    public void fillParentStepOne(
            String fName,
            String lName,
            String emailValue,
            String pwd,
            String phone) {

        type(firstName, fName);
        type(lastName, lName);
        type(email, emailValue);
        type(password, pwd);
        type(confirmPassword, pwd);
        type(phoneNumber, phone);
        clickContinue();
    }
}
