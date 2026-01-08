package com.stedprep.automation.pages;

import com.stedprep.automation.config.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class StudentLoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By usernameInput = By.id("studentLogin_username");
    private By passwordInput = By.id("studentLogin_password");

    private By signInButton =
            By.xpath("//button[.//span[text()='Sign In']]");

    public StudentLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    // -----------------------
    // OPEN LOGIN PAGE
    // -----------------------
    public void open() {
        driver.get(
                ConfigReader.get("base.url") +
                        ConfigReader.get("student.login.path")
        );
    }

    // -----------------------
    // LOGIN (ANT DESIGN SAFE)
    // -----------------------
    public void login(String username, String password) {

        WebElement user =
                wait.until(ExpectedConditions.elementToBeClickable(usernameInput));
        user.click();
        user.clear();
        user.sendKeys(username);

        WebElement pass =
                wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
        pass.click();
        pass.clear();
        pass.sendKeys(password);

        // 🔑 IMPORTANT: wait until button ENABLES
        wait.until(driver ->
                !driver.findElement(signInButton)
                        .getAttribute("class")
                        .contains("disabled")
        );

        WebElement signIn =
                wait.until(ExpectedConditions.elementToBeClickable(signInButton));
        signIn.click();
    }
}
