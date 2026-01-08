package com.stedprep.automation.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YopmailPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By inboxInput = By.id("login");
    private By mailFrame = By.id("ifmail");

    public YopmailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ====================================
    // FETCH STUDENT LOGIN CREDENTIALS (STABLE)
    // ====================================
    public String[] fetchStudentCredentials(String parentEmail) {

        String inbox = parentEmail.split("@")[0];

        driver.get("https://yopmail.com");

        WebElement inboxField =
                wait.until(ExpectedConditions.visibilityOfElementLocated(inboxInput));

        // HARD CLEAR
        inboxField.click();
        inboxField.sendKeys(Keys.CONTROL, "a");
        inboxField.sendKeys(Keys.DELETE);
        inboxField.sendKeys(inbox);
        inboxField.sendKeys(Keys.ENTER);

        String username = null;
        String password = null;

        Instant endTime = Instant.now().plus(Duration.ofMinutes(3)); // ⏱ MAX WAIT

        while (Instant.now().isBefore(endTime)) {

            try {
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(mailFrame));

                String bodyText = driver.findElement(By.tagName("body")).getText();

                Matcher userMatcher =
                        Pattern.compile("Username:\\s*(\\S+)").matcher(bodyText);
                Matcher passMatcher =
                        Pattern.compile("Password:\\s*(\\S+)").matcher(bodyText);

                if (userMatcher.find() && passMatcher.find()) {
                    username = userMatcher.group(1);
                    password = passMatcher.group(1);
                    break;
                }

            } catch (Exception ignored) {
            } finally {
                driver.switchTo().defaultContent();
            }

            // 🔄 WAIT + REFRESH
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            driver.navigate().refresh();
        }

        if (username == null || password == null) {
            throw new RuntimeException(
                    "❌ Student credentials email not received within 3 minutes"
            );
        }

        return new String[]{username, password};
    }
}
