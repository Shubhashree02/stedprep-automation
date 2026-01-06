package com.stedprep.automation.pages;

import com.stedprep.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParentRegistrationPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public ParentRegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
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
    // STEP 1 LOCATORS (UNCHANGED)
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
    // OTP LOCATORS
    // =======================
    private By otpInputs =
            By.xpath("//input[@maxlength='1' and @inputmode='numeric']");

    private By verifyButton =
            By.xpath("//button[.//span[text()='Verify']]");

    // =======================
    // YOPMAIL LOCATORS
    // =======================
    private By yopmailInput = By.id("login");
    private By yopmailCheckBtn = By.id("refreshbut");
    private By mailFrame = By.id("ifmail");

    // =======================
    // COMMON ACTION
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
    // STEP 1 – REGISTRATION
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

    // =======================
    // STEP 2 – FULL OTP FLOW
    // =======================
    public void verifyOtpFromYopmail(String emailValue) {

        // store app window
        String appWindow = driver.getWindowHandle();

        // open new tab
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://yopmail.com");

        // open inbox
        String inbox = emailValue.split("@")[0];
        wait.until(ExpectedConditions.visibilityOfElementLocated(yopmailInput))
                .sendKeys(inbox);
        driver.findElement(yopmailCheckBtn).click();

        // switch to email iframe
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(mailFrame));

        // read mail text
        String mailText = driver.findElement(By.tagName("body")).getText();

        // extract 6-digit OTP
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(mailText);

        if (!matcher.find()) {
            throw new RuntimeException("OTP not found in Yopmail inbox");
        }

        String otp = matcher.group();

        // close yopmail tab
        driver.switchTo().defaultContent();
        Set<String> windows = driver.getWindowHandles();
        for (String w : windows) {
            if (!w.equals(appWindow)) {
                driver.switchTo().window(w).close();
            }
        }

        // back to app
        driver.switchTo().window(appWindow);

        // enter OTP
        enterOtp(otp);
        clickVerify();
    }

    // =======================
    // OTP ENTRY
    // =======================
    private void enterOtp(String otp) {

        wait.until(ExpectedConditions.numberOfElementsToBe(otpInputs, 6));

        List<WebElement> boxes = driver.findElements(otpInputs);

        for (int i = 0; i < otp.length(); i++) {
            boxes.get(i).clear();
            boxes.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }
    }

    private void clickVerify() {
        wait.until(ExpectedConditions.elementToBeClickable(verifyButton));
        driver.findElement(verifyButton).click();
    }
}
