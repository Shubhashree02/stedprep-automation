package com.stedprep.automation.pages;

import com.stedprep.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ParentLoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By emailInput = By.id("parentLogin_email");
    private By passwordInput = By.id("parentLogin_password");
    private By signInButton = By.xpath("//button[@type='submit'] | //button[.//span[text()='Sign In']] | //button[.//span[text()='Log In']]");

    // Subscription modal (shown for new parents after first login)
    private By subscriptionModalTitle = By.xpath("//h2[contains(.,'Subscription Required')]");
    private By couponInput = By.id("couponCode");
    private By applyCouponButton = By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[normalize-space()='Apply']]");
    private By okButton = By.xpath("//div[contains(@class,'ant-modal')]//button[.//span[normalize-space()='OK']]");

    public ParentLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void open() {
        driver.get(
                ConfigReader.get("base.url") +
                        ConfigReader.get("parent.login.path")
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(emailInput));
        System.out.println("✅ Parent login page loaded");
    }

    public void login(String email, String password) {
        WebElement emailEl = wait.until(ExpectedConditions.elementToBeClickable(emailInput));
        emailEl.click();
        emailEl.clear();
        emailEl.sendKeys(email);

        WebElement passEl = wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
        passEl.click();
        passEl.clear();
        passEl.sendKeys(password);

        wait.until(d -> {
            WebElement btn = d.findElement(signInButton);
            String cls = btn.getAttribute("class");
            return cls == null || !cls.contains("disabled");
        });

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(signInButton));
        btn.click();
        System.out.println("✅ Parent login submitted");
    }

    /**
     * If the \"Subscription Required\" modal appears after login, apply the WELCOMEFREE coupon
     * and click OK. If the modal is not present, this method does nothing.
     */
    public void handleSubscriptionModalIfPresent() {
        try {
            // Short wait to see if the modal appears
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(subscriptionModalTitle));
        } catch (Exception e) {
            // Modal not shown, nothing to do
            return;
        }

        try {
            WebElement couponField = wait.until(ExpectedConditions.elementToBeClickable(couponInput));
            couponField.click();
            couponField.clear();
            couponField.sendKeys("WELCOMEFREE");

            // Wait for Apply button to enable, then click
            wait.until(d -> {
                WebElement applyBtn = d.findElement(applyCouponButton);
                String cls = applyBtn.getAttribute("class");
                return cls == null || !cls.contains("disabled");
            });
            WebElement applyBtn = wait.until(ExpectedConditions.elementToBeClickable(applyCouponButton));
            applyBtn.click();
            System.out.println("✅ Applied WELCOMEFREE coupon");

            // Click OK to close the modal, if present
            try {
                WebElement okBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(okButton));
                okBtn.click();
                System.out.println("✅ Closed subscription modal with OK");
            } catch (Exception ignored) {
                // If no OK button appears, ignore
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to handle subscription modal: " + e.getMessage());
        }
    }
}
