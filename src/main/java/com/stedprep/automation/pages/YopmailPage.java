package com.stedprep.automation.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YopmailPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By inboxInput = By.id("login");
    private By inboxCheckBtn = By.id("refreshbut");
    private By inboxFrame = By.id("ifinbox");
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

    /**
     * Open Yopmail inbox for the given email, open the latest (first) email, and return its body text.
     * Used by Forgot Password flow to read the verification/reset email.
     */
    public String openLatestEmailAndGetBody(String parentEmail) {
        String inbox = parentEmail.split("@")[0];
        driver.get("https://yopmail.com");
        WebElement inboxField = wait.until(ExpectedConditions.visibilityOfElementLocated(inboxInput));
        inboxField.click();
        inboxField.sendKeys(Keys.CONTROL, "a");
        inboxField.sendKeys(Keys.DELETE);
        inboxField.sendKeys(inbox);
        inboxField.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(inboxCheckBtn));

        for (int i = 0; i < 10; i++) {
            try {
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(inboxFrame));
                java.util.List<WebElement> mailLinks = driver.findElements(By.cssSelector("a[href*='mail']"));
                if (!mailLinks.isEmpty()) {
                    mailLinks.get(0).click();
                    break;
                }
            } catch (Exception ignored) {
            } finally {
                driver.switchTo().defaultContent();
            }
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            driver.navigate().refresh();
        }

        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(mailFrame));
        String body = driver.findElement(By.tagName("body")).getText();
        driver.switchTo().defaultContent();
        return body;
    }

    private static final Pattern OTP_6_DIGIT = Pattern.compile("\\b(\\d{6})\\b");

    /**
     * Same approach as registration OTP (ParentRegistrationPage.verifyOtpFromYopmail):
     * open Yopmail in new tab, enter inbox, click Check button, then poll ifmail frame for 6-digit OTP
     * with refresh until found. No clicking on individual emails - ifmail shows latest after Check/refresh.
     */
    public String getOtpFromLatestEmail(String parentEmail, String appWindowHandle) {
        String originalHandle = driver.getWindowHandle();
        Set<String> before = driver.getWindowHandles();
        ((JavascriptExecutor) driver).executeScript("window.open('', '_blank');");
        Set<String> after = driver.getWindowHandles();
        after.removeAll(before);
        String yopmailHandle = after.iterator().next();
        driver.switchTo().window(yopmailHandle);

        try {
            String inbox = parentEmail.split("@")[0];
            driver.get("https://yopmail.com");

            WebElement inboxField = wait.until(ExpectedConditions.visibilityOfElementLocated(inboxInput));
            inboxField.sendKeys(inbox);
            driver.findElement(inboxCheckBtn).click();

            String otp = null;
            for (int i = 0; i < 10; i++) {
                try {
                    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(mailFrame));
                    String text = driver.findElement(By.tagName("body")).getText();
                    Matcher m = OTP_6_DIGIT.matcher(text);
                    if (m.find()) {
                        otp = m.group(1);
                        break;
                    }
                } catch (Exception ignored) {
                } finally {
                    driver.switchTo().defaultContent();
                }
                try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
                try {
                    driver.navigate().refresh();
                } catch (Exception e) {
                    try {
                        WebElement refreshBtn = wait.until(ExpectedConditions.elementToBeClickable(inboxCheckBtn));
                        refreshBtn.click();
                    } catch (Exception ex) {
                        driver.navigate().refresh();
                    }
                }
            }

            if (otp == null) {
                throw new RuntimeException("❌ OTP not received from Yopmail (Password Reset email)");
            }
            System.out.println("📬 OTP from Yopmail: " + otp);
            return otp;
        } finally {
            driver.close();
            driver.switchTo().window(appWindowHandle != null ? appWindowHandle : originalHandle);
        }
    }
}
