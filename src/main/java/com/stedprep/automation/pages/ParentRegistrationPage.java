package com.stedprep.automation.pages;

import com.stedprep.automation.config.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParentRegistrationPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public ParentRegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
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
    // STEP 1 – PARENT INFO
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
    // STEP 2 – OTP
    // =======================
    private By otpInputs =
            By.xpath("//input[@maxlength='1' and @inputmode='numeric']");
    private By verifyButton =
            By.xpath("//button[.//span[text()='Verify']]");

    // Yopmail
    private By yopmailInput = By.id("login");
    private By yopmailCheckBtn = By.id("refreshbut");
    private By mailFrame = By.id("ifmail");

    // =======================
    // STEP 3 – STUDENT INFO
    // =======================
    private By city =
            By.xpath("//label[text()='City']/following::input[1]");
    private By zipCode =
            By.xpath("//label[text()='ZIP Code']/following::input[1]");

    private By studentFirstName =
            By.id("Student information_students_0_firstName");
    private By studentLastName =
            By.id("Student information_students_0_lastName");
    private By studentEmail =
            By.id("Student information_students_0_email");

    private By iseeTestDate =
            By.id("Student information_students_0_iseeTestDate");

    private By calendarDate =
            By.xpath("//td[contains(@class,'ant-picker-cell') and not(contains(@class,'disabled'))]");

    // Grade (Ant Design)
    private By gradeSelector =
            By.xpath("//input[@id='Student information_students_0_currentGrade']/ancestor::div[contains(@class,'ant-select-selector')]");

    // Checkboxes (UPDATED – FINAL)
    private By privacyCheckbox =
            By.id("Student information_privacyRefundAgreement");

    private By termsCheckbox =
            By.id("Student information_termsAgreement");

    // Submit
    private By submitAndFinishBtn =
            By.xpath("//button[.//span[text()='Submit & Finish']]");

    // =======================
    // COMMON TYPE
    // =======================
    private void type(By locator, String value) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.click();
        el.clear();
        el.sendKeys(value);
    }

    // =======================
    // STEP 1
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
        driver.findElement(continueButton).click();
    }

    // =======================
    // STEP 2 – OTP
    // =======================
    public void verifyOtpFromYopmail(String emailValue) {

        String appWindow = driver.getWindowHandle();
        String inbox = emailValue.split("@")[0];

        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://yopmail.com");

        wait.until(ExpectedConditions.visibilityOfElementLocated(yopmailInput))
                .sendKeys(inbox);
        driver.findElement(yopmailCheckBtn).click();

        String otp = null;

        for (int i = 0; i < 5; i++) {
            try {
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(mailFrame));
                String text = driver.findElement(By.tagName("body")).getText();
                Matcher m = Pattern.compile("\\b\\d{6}\\b").matcher(text);
                if (m.find()) {
                    otp = m.group();
                    break;
                }
            } catch (Exception ignored) {
            } finally {
                driver.switchTo().defaultContent();
            }

            try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
            driver.findElement(yopmailCheckBtn).click();
        }

        if (otp == null) {
            throw new RuntimeException("OTP not received from Yopmail");
        }

        for (String w : driver.getWindowHandles()) {
            if (!w.equals(appWindow)) {
                driver.switchTo().window(w).close();
            }
        }
        driver.switchTo().window(appWindow);

        List<WebElement> boxes =
                wait.until(ExpectedConditions.numberOfElementsToBe(otpInputs, 6));

        for (int i = 0; i < otp.length(); i++) {
            boxes.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }

        wait.until(ExpectedConditions.elementToBeClickable(verifyButton)).click();
    }

    // =======================
    // STEP 3 – STUDENT (FINAL FIX)
    // =======================
    public void fillStudentStepThree() {

        wait.until(ExpectedConditions.visibilityOfElementLocated(city));

        type(city, "Chicago");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-select-item-option')]")
        )).click();

        type(zipCode, "60601");

        // DO NOT REMOVE
        type(studentFirstName, "Alex");
        type(studentLastName, "Smith");
        type(studentEmail, "stedprep_test@yopmail.com");

        js.executeScript("window.scrollBy(0,400);");

        // ---------- ISEE TEST DATE ----------
        WebElement testDate =
                wait.until(ExpectedConditions.elementToBeClickable(iseeTestDate));
        testDate.click();
        wait.until(ExpectedConditions.elementToBeClickable(calendarDate)).click();

        // ---------- GRADE (ANT DESIGN – CORRECT WAY) ----------
        WebElement selector =
                wait.until(ExpectedConditions.elementToBeClickable(gradeSelector));

        js.executeScript("arguments[0].click();", selector);

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        // KEY FIX: send keys to active element
        WebElement activeInput = driver.switchTo().activeElement();
        activeInput.sendKeys(Keys.ARROW_DOWN);
        activeInput.sendKeys(Keys.ENTER);
// ---------- CHECKBOXES (FINAL & SAFE) ----------
        WebElement privacy =
                wait.until(ExpectedConditions.presenceOfElementLocated(privacyCheckbox));
        js.executeScript("arguments[0].click();", privacy);

        WebElement terms =
                wait.until(ExpectedConditions.presenceOfElementLocated(termsCheckbox));
        js.executeScript("arguments[0].click();", terms);

        // ---------- SUBMIT ----------
        WebElement submit =
                wait.until(ExpectedConditions.elementToBeClickable(submitAndFinishBtn));
        js.executeScript("arguments[0].click();", submit);
    }
}
