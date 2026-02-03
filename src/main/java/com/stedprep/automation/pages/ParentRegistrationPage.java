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

    // Phone number error indicators
    private By phoneErrorText = 
            By.xpath("//input[@placeholder='Enter your phone number (optional)']/ancestor::div[contains(@class,'ant-form-item')]//div[contains(@class,'ant-form-item-explain-error')]");
    private By phoneErrorClass = 
            By.xpath("//input[@placeholder='Enter your phone number (optional)' and (contains(@class,'error') or contains(@class,'invalid') or contains(@class,'ant-input-status-error'))]");

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
//    private By refreshBtn = By.id("refreshbut");

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
    
    // ZIP Code error/warning indicators
    private By zipCodeWarning = 
            By.xpath("//input[@id='Student information_students_0_zipCode']/ancestor::div[contains(@class,'ant-form-item')]//div[contains(@class,'ant-form-item-explain-warning') or contains(@class,'warning')]");
    private By zipCodeError = 
            By.xpath("//input[@id='Student information_students_0_zipCode']/ancestor::div[contains(@class,'ant-form-item')]//div[contains(@class,'ant-form-item-explain-error') or contains(@class,'error')]");
    
    // Success/Error message indicators after submit - Only target actual message/notification elements
    private By successMessage = 
            By.xpath("//div[contains(@class,'ant-message-success')]//span[contains(@class,'ant-message-custom-content') or contains(@class,'ant-message-notice-content')]");
    private By errorMessage = 
            By.xpath("//div[contains(@class,'ant-message-error')]//span[contains(@class,'ant-message-custom-content') or contains(@class,'ant-message-notice-content')]");
    private By processingIndicator = 
            By.xpath("//div[contains(@class,'ant-spin') or contains(@class,'loading') or contains(@class,'spinner')]");
    private By notificationMessage = 
            By.xpath("//div[contains(@class,'ant-notification-notice-message')]");
    private By toastMessage = 
            By.xpath("//div[contains(@class,'ant-message')]//span[contains(@class,'ant-message-custom-content')]");
    // Form-level error messages (not field-specific)
    private By formErrorMessage = 
            By.xpath("//div[contains(@class,'ant-alert-error')] | //div[contains(@class,'error-message') and not(ancestor::div[contains(@class,'ant-form-item')])]");
    // Success page heading after registration
    private By successPageHeading = 
            By.xpath("//h3[contains(@class,'ant-typography') and contains(.,'Account Created Successfully')] | //h3[contains(.,'Account Created Successfully')]");

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
    // PHONE NUMBER ERROR HANDLING
    // =======================
    
    // Check if phone number field has validation error
    private boolean hasPhoneNumberError() {
        try {
            // Wait a bit for error to appear after typing
            Thread.sleep(500);
            
            // Check for error text
            List<WebElement> errorTexts = driver.findElements(phoneErrorText);
            if (!errorTexts.isEmpty()) {
                for (WebElement error : errorTexts) {
                    if (error.isDisplayed() && !error.getText().trim().isEmpty()) {
                        System.out.println("⚠️ Phone number error detected: " + error.getText());
                        return true;
                    }
                }
            }
            
            // Check for error class on input field
            WebElement phoneField = driver.findElement(phoneNumber);
            String classAttr = phoneField.getAttribute("class");
            if (classAttr != null && (classAttr.contains("error") || classAttr.contains("invalid") || classAttr.contains("ant-input-status-error"))) {
                System.out.println("⚠️ Phone number error class detected: " + classAttr);
                return true;
            }
            
            // Check for aria-invalid attribute
            String ariaInvalid = phoneField.getAttribute("aria-invalid");
            if ("true".equals(ariaInvalid)) {
                System.out.println("⚠️ Phone number aria-invalid detected");
                return true;
            }
            
            return false;
        } catch (Exception e) {
            // If we can't check, assume no error (to avoid false positives)
            return false;
        }
    }
    
    // Clear phone number field thoroughly
    private void clearPhoneNumber() {
        try {
            WebElement phoneField = wait.until(ExpectedConditions.elementToBeClickable(phoneNumber));
            phoneField.click();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            
            // Clear using multiple methods
            phoneField.clear();
            js.executeScript("arguments[0].value = '';", phoneField);
            phoneField.sendKeys(Keys.CONTROL + "a");
            phoneField.sendKeys(Keys.DELETE);
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            System.out.println("⚠️ Error clearing phone number field: " + e.getMessage());
        }
    }
    
    // List of valid USA phone numbers to try
    private String[] validUSAPhoneNumbers = {
        "3125551234",      // Chicago area code
        "2125551234",      // New York area code
        "3105551234",      // Los Angeles area code
        "7135551234",      // Houston area code
        "6025551234",      // Phoenix area code
        "2155551234",      // Philadelphia area code
        "2105551234",      // San Antonio area code
        "6195551234",      // San Diego area code
        "2145551234",      // Dallas area code
        "4085551234",      // San Jose area code
        "3125555678",      // Another Chicago number
        "2125555678",      // Another New York number
        "3105555678"       // Another LA number
    };

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
        
        // ✅ PHONE NUMBER WITH ERROR HANDLING
        // Try the provided phone number first
        type(phoneNumber, phone);
        
        // Wait a moment to ensure form is ready
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        
        // Click continue button first - error will appear after clicking
        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", continueBtn);
        js.executeScript("arguments[0].click();", continueBtn);
        
        // Wait a moment for error to appear (if any)
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        // Check for errors AFTER clicking Continue
        if (hasPhoneNumberError()) {
            System.out.println("⚠️ Phone number error detected with: " + phone);
            System.out.println("🔄 Clearing and trying valid USA phone numbers...");
            
            // Clear the invalid number
            clearPhoneNumber();
            
            // Try valid USA phone numbers
            boolean phoneValid = false;
            for (String validPhone : validUSAPhoneNumbers) {
                System.out.println("🔍 Trying phone number: " + validPhone);
                
                // Type the new phone number
                try {
                    WebElement phoneField = wait.until(ExpectedConditions.elementToBeClickable(phoneNumber));
                    phoneField.click();
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                    phoneField.sendKeys(validPhone);
                    try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                } catch (Exception e) {
                    System.out.println("⚠️ Error typing phone number " + validPhone + ": " + e.getMessage());
                    clearPhoneNumber();
                    continue;
                }
                
                // Click Continue button again to check if this number is valid
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                continueBtn = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", continueBtn);
                js.executeScript("arguments[0].click();", continueBtn);
                
                // Wait for error to appear (if any)
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                
                // Check if this number is valid (no error)
                if (!hasPhoneNumberError()) {
                    System.out.println("✅ Valid phone number found: " + validPhone);
                    phoneValid = true;
                    break; // Success - exit loop
                } else {
                    System.out.println("❌ Phone number " + validPhone + " still has error, trying next...");
                    clearPhoneNumber();
                }
            }
            
            if (!phoneValid) {
                throw new RuntimeException(
                    "❌ Failed to find a valid phone number after trying all options. " +
                    "Please check the phone number validation rules."
                );
            }
        } else {
            System.out.println("✅ Phone number accepted: " + phone);
        }
        
        System.out.println("✅ Clicked Continue button - proceeding to OTP");
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

//        driver.findElement(refreshBtn).click();

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
            // Use navigate().refresh() instead of clicking refresh button (more reliable)
            try {
                driver.navigate().refresh();
            } catch (Exception e) {
                // Fallback: try to click refresh button if it exists
                try {
                    WebElement refreshBtn = wait.until(ExpectedConditions.elementToBeClickable(yopmailCheckBtn));
                    refreshBtn.click();
                } catch (Exception ex) {
                    // If refresh button doesn't exist, just refresh the page
                    driver.navigate().refresh();
                }
            }
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
    // SUBMIT MESSAGE HANDLING
    // =======================
    
    // Helper method to check if text is a valid message (not form labels)
    private boolean isValidMessage(String text) {
        if (text == null || text.trim().isEmpty() || text.length() < 5) {
            return false;
        }
        
        String lowerText = text.toLowerCase().trim();
        
        // Filter out form labels and common page elements
        String[] formLabels = {
            "city", "state", "zip code", "first name", "last name", 
            "targeted school", "isee level", "email", "isee test date",
            "study plan duration", "select duration", "current school",
            "grade", "add another student", "privacy policy", "refund policy",
            "terms and conditions", "submit & finish", "submit", "finish"
        };
        
        for (String label : formLabels) {
            if (lowerText.equals(label) || lowerText.startsWith(label + "\n") || lowerText.startsWith(label + " ")) {
                return false;
            }
        }
        
        // Check if it contains actual message keywords
        String[] successKeywords = {"success", "completed", "saved", "submitted", "registered", "created", "done"};
        String[] errorKeywords = {"error", "failed", "invalid", "unable", "cannot", "try again", "please", "required"};
        
        for (String keyword : successKeywords) {
            if (lowerText.contains(keyword)) {
                return true;
            }
        }
        
        for (String keyword : errorKeywords) {
            if (lowerText.contains(keyword)) {
                return true;
            }
        }
        
        // If it's a short message (likely a notification), accept it
        if (text.length() < 200 && !text.contains("\n") && !text.contains("  ")) {
            return true;
        }
        
        return false;
    }
    
    // Wait for and fetch success or error message after submit
    private String waitForSubmitMessage() {
        String message = null;
        WebDriverWait messageWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
        try {
            System.out.println("⏳ Waiting for processing to complete...");
            
            // First, check if processing indicator is showing
            boolean processingShown = false;
            try {
                List<WebElement> processing = driver.findElements(processingIndicator);
                for (WebElement proc : processing) {
                    if (proc.isDisplayed()) {
                        processingShown = true;
                        System.out.println("⏳ Processing indicator found, waiting for it to disappear...");
                        break;
                    }
                }
            } catch (Exception e) {
                // No processing indicator found
            }
            
            // If processing is shown, wait for it to disappear
            if (processingShown) {
                try {
                    // Wait up to 25 seconds for processing to complete
                    for (int i = 0; i < 25; i++) {
                        Thread.sleep(1000);
                        boolean stillProcessing = false;
                        List<WebElement> processing = driver.findElements(processingIndicator);
                        for (WebElement proc : processing) {
                            if (proc.isDisplayed()) {
                                stillProcessing = true;
                                break;
                            }
                        }
                        if (!stillProcessing) {
                            System.out.println("✅ Processing completed");
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    // Continue to check for messages
                }
            }
            
            // Wait for message to appear (use WebDriverWait for better reliability)
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            
            // Check for success page heading first (most reliable indicator)
            try {
                WebElement successHeading = messageWait.until(ExpectedConditions.presenceOfElementLocated(successPageHeading));
                if (successHeading != null && successHeading.isDisplayed()) {
                    message = successHeading.getText().trim();
                    if (!message.isEmpty()) {
                        System.out.println("✅ SUCCESS PAGE HEADING DETECTED: " + message);
                        return message;
                    }
                }
            } catch (Exception e) {
                // Continue to check other success indicators
            }
            
            // Check for success message - wait for it to appear
            try {
                // Try to wait for success message to appear
                List<WebElement> successMessages = messageWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(successMessage));
                for (WebElement msg : successMessages) {
                    if (msg.isDisplayed()) {
                        // Get text from the message element
                        message = msg.getText().trim();
                        
                        // Validate it's a real message, not form labels
                        if (isValidMessage(message)) {
                            System.out.println("✅ SUCCESS MESSAGE: " + message);
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // Continue to check error message
            }
            
            // Check for error message - wait for it to appear
            try {
                List<WebElement> errorMessages = messageWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(errorMessage));
                for (WebElement msg : errorMessages) {
                    if (msg.isDisplayed()) {
                        message = msg.getText().trim();
                        if (isValidMessage(message)) {
                            System.out.println("❌ ERROR MESSAGE: " + message);
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // No error message found, continue checking
            }
            
            // Check for form-level error messages (not field-specific)
            try {
                List<WebElement> formErrors = driver.findElements(formErrorMessage);
                for (WebElement formError : formErrors) {
                    if (formError.isDisplayed()) {
                        message = formError.getText().trim();
                        if (isValidMessage(message)) {
                            System.out.println("❌ FORM ERROR MESSAGE: " + message);
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // No form error found
            }
            
            // Check for ZIP code validation error specifically (this is a known error)
            try {
                List<WebElement> zipErrors = driver.findElements(zipCodeError);
                for (WebElement zipError : zipErrors) {
                    if (zipError.isDisplayed()) {
                        String zipErrorText = zipError.getText().trim();
                        if (zipErrorText.contains("Unable to validate") || zipErrorText.contains("validate ZIP code")) {
                            message = zipErrorText;
                            System.out.println("❌ ZIP CODE VALIDATION ERROR: " + message);
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // No ZIP error found
            }
            
            // Check for notification messages
            try {
                List<WebElement> notifications = driver.findElements(notificationMessage);
                for (WebElement notif : notifications) {
                    if (notif.isDisplayed()) {
                        message = notif.getText().trim();
                        if (isValidMessage(message)) {
                            // Check if it's success or error
                            String lowerMsg = message.toLowerCase();
                            if (lowerMsg.contains("success") || lowerMsg.contains("completed") || lowerMsg.contains("saved")) {
                                System.out.println("✅ SUCCESS MESSAGE: " + message);
                            } else if (lowerMsg.contains("error") || lowerMsg.contains("failed") || lowerMsg.contains("invalid")) {
                                System.out.println("❌ ERROR MESSAGE: " + message);
                            } else {
                                System.out.println("📢 NOTIFICATION MESSAGE: " + message);
                            }
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // No notification found
            }
            
            // Check for toast messages
            try {
                List<WebElement> toasts = driver.findElements(toastMessage);
                for (WebElement toast : toasts) {
                    if (toast.isDisplayed()) {
                        message = toast.getText().trim();
                        if (isValidMessage(message)) {
                            String lowerMsg = message.toLowerCase();
                            if (lowerMsg.contains("success") || lowerMsg.contains("completed") || lowerMsg.contains("saved")) {
                                System.out.println("✅ SUCCESS MESSAGE: " + message);
                            } else if (lowerMsg.contains("error") || lowerMsg.contains("failed") || lowerMsg.contains("invalid")) {
                                System.out.println("❌ ERROR MESSAGE: " + message);
                            } else {
                                System.out.println("📢 TOAST MESSAGE: " + message);
                            }
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // No toast found
            }
            
            // If no message found, check URL change as indicator
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("/success") || currentUrl.contains("/complete") || currentUrl.contains("/welcome")) {
                message = "Success - Page redirected to: " + currentUrl;
                System.out.println("✅ SUCCESS - URL changed: " + currentUrl);
                return message;
            }
            
            if (message == null || message.isEmpty()) {
                System.out.println("⚠️ No success or error message found after submit");
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error waiting for submit message: " + e.getMessage());
        }
        
        return message;
    }
    
    // =======================
    // ZIP CODE ERROR HANDLING
    // =======================
    
    // Check if ZIP code has warning or error
    private boolean hasZipCodeError() {
        try {
            // Wait a bit for warning/error to appear after typing
            Thread.sleep(500);
            
            // Check for warning text
            List<WebElement> warnings = driver.findElements(zipCodeWarning);
            if (!warnings.isEmpty()) {
                for (WebElement warning : warnings) {
                    if (warning.isDisplayed() && !warning.getText().trim().isEmpty()) {
                        String warningText = warning.getText().trim();
                        System.out.println("⚠️ ZIP code warning detected: " + warningText);
                        // Check if it's a mismatch warning (ZIP doesn't match city)
                        if (warningText.toLowerCase().contains("belongs to") || 
                            warningText.toLowerCase().contains("verify") ||
                            warningText.toLowerCase().contains("mismatch")) {
                            return true;
                        }
                    }
                }
            }
            
            // Check for error text
            List<WebElement> errors = driver.findElements(zipCodeError);
            if (!errors.isEmpty()) {
                for (WebElement error : errors) {
                    if (error.isDisplayed() && !error.getText().trim().isEmpty()) {
                        System.out.println("⚠️ ZIP code error detected: " + error.getText());
                        return true;
                    }
                }
            }
            
            // Check for error class on ZIP code field
            WebElement zipField = driver.findElement(zipCode);
            String classAttr = zipField.getAttribute("class");
            if (classAttr != null && (classAttr.contains("error") || classAttr.contains("invalid") || classAttr.contains("ant-input-status-error"))) {
                System.out.println("⚠️ ZIP code error class detected: " + classAttr);
                return true;
            }
            
            // Check for aria-invalid attribute
            String ariaInvalid = zipField.getAttribute("aria-invalid");
            if ("true".equals(ariaInvalid)) {
                System.out.println("⚠️ ZIP code aria-invalid detected");
                return true;
            }
            
            return false;
        } catch (Exception e) {
            // If we can't check, assume no error (to avoid false positives)
            return false;
        }
    }
    
    // Clear ZIP code field thoroughly
    private void clearZipCode() {
        try {
            WebElement zipField = wait.until(ExpectedConditions.elementToBeClickable(zipCode));
            zipField.click();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            
            // Clear using multiple methods
            zipField.clear();
            js.executeScript("arguments[0].value = '';", zipField);
            zipField.sendKeys(Keys.CONTROL + "a");
            zipField.sendKeys(Keys.DELETE);
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            System.out.println("⚠️ Error clearing ZIP code field: " + e.getMessage());
        }
    }
    
    // Map cities to valid ZIP codes
    private String getZipCodeForCity(String cityName) {
        // Map of city names to valid ZIP codes
        java.util.Map<String, String> cityZipMap = new java.util.HashMap<>();
        cityZipMap.put("Chicago", "60601");
        cityZipMap.put("New York", "10001");
        cityZipMap.put("Los Angeles", "90001");
        cityZipMap.put("Houston", "77001");
        cityZipMap.put("Phoenix", "85001");
        cityZipMap.put("Philadelphia", "19101");
        cityZipMap.put("San Antonio", "78201");
        cityZipMap.put("San Diego", "92101");
        cityZipMap.put("Dallas", "75201");
        cityZipMap.put("San Jose", "95101");
        
        // Return ZIP code for the city, or default to 60601 if not found
        return cityZipMap.getOrDefault(cityName, "60601");
    }
    
    // Get alternative ZIP codes for a city
    private String[] getAlternativeZipCodes(String cityName) {
        java.util.Map<String, String[]> cityAltZipMap = new java.util.HashMap<>();
        cityAltZipMap.put("Chicago", new String[]{"60602", "60603", "60604", "60605", "60606"});
        cityAltZipMap.put("New York", new String[]{"10002", "10003", "10004", "10005", "10006"});
        cityAltZipMap.put("Los Angeles", new String[]{"90002", "90003", "90004", "90005", "90006"});
        cityAltZipMap.put("Houston", new String[]{"77002", "77003", "77004", "77005", "77006"});
        cityAltZipMap.put("Phoenix", new String[]{"85002", "85003", "85004", "85005", "85006"});
        cityAltZipMap.put("Philadelphia", new String[]{"19102", "19103", "19104", "19105", "19106"});
        cityAltZipMap.put("San Antonio", new String[]{"78202", "78203", "78204", "78205", "78206"});
        cityAltZipMap.put("San Diego", new String[]{"92102", "92103", "92104", "92105", "92106"});
        cityAltZipMap.put("Dallas", new String[]{"75202", "75203", "75204", "75205", "75206"});
        cityAltZipMap.put("San Jose", new String[]{"95102", "95103", "95104", "95105", "95106"});
        
        // Return alternative ZIP codes for the city, or default array if not found
        return cityAltZipMap.getOrDefault(cityName, new String[]{"60602", "60603", "60604", "60605", "60606"});
    }

    // =======================
    // STEP 3 – STUDENT (FINAL FIX)
    // =======================
    public void fillStudentStepThree(String studentEmailValue) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(city));

        // List of USA cities to try in sequence
        String[] usaCities = {"Chicago", "New York", "Los Angeles", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"};
        
        WebDriverWait dropdownWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        int maxRetriesPerCity = 3;
        int retryDelayMs = 2000; // Increased from 1000 to 2000ms
        boolean dropdownClicked = false;
        String selectedCity = null; // Store the selected city name

        // Try each city until one works
        for (String cityName : usaCities) {
            System.out.println("🔍 Trying city: " + cityName);
            
            // Clear field before trying new city
            try {
                WebElement cityField = wait.until(ExpectedConditions.elementToBeClickable(city));
                cityField.click();
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                js.executeScript("arguments[0].value = '';", cityField);
                cityField.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
                cityField.sendKeys(org.openqa.selenium.Keys.DELETE);
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            } catch (Exception e) {
                // Ignore - will try to clear in inner loop
            }
            
            for (int attempt = 1; attempt <= maxRetriesPerCity; attempt++) {
                try {
                    // Clear previous search and type new city - use robust clearing method
                    WebElement cityField = wait.until(ExpectedConditions.elementToBeClickable(city));
                    cityField.click();
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                    
                    // Clear using multiple methods to ensure it's empty
                    cityField.clear();
                    js.executeScript("arguments[0].value = '';", cityField);
                    cityField.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
                    cityField.sendKeys(org.openqa.selenium.Keys.DELETE);
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                    
                    // Now type the new city name
                    cityField.sendKeys(cityName);

                    // Wait for dropdown to populate - increased delay
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

                    // Check if dropdown option appears
                    try {
                        WebElement dropdownOption = dropdownWait.until(
                                ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-select-item-option')]")
                                )
                        );

                        js.executeScript("arguments[0].scrollIntoView({block:'center'});", dropdownOption);
                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                        js.executeScript("arguments[0].click();", dropdownOption);
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

                        dropdownClicked = true;
                        selectedCity = cityName; // Store the selected city
                        System.out.println("✅ Successfully selected city: " + cityName);
                        break; // Success - exit both loops
                        
                    } catch (org.openqa.selenium.TimeoutException e) {
                        // No dropdown option found for this city
                        System.out.println("⚠️ No dropdown option found for " + cityName + " (Attempt " + attempt + "/" + maxRetriesPerCity + ")");
                        if (attempt < maxRetriesPerCity) {
                            try { Thread.sleep(retryDelayMs); } catch (InterruptedException ignored) {}
                            continue; // Retry same city
                        } else {
                            // Move to next city - clear field first
                            System.out.println("❌ No results for " + cityName + ", trying next city...");
                            try {
                                WebElement cityFieldToClear = wait.until(ExpectedConditions.elementToBeClickable(city));
                                cityFieldToClear.click();
                                js.executeScript("arguments[0].value = '';", cityFieldToClear);
                                cityFieldToClear.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
                                cityFieldToClear.sendKeys(org.openqa.selenium.Keys.DELETE);
                                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                            } catch (Exception ignored) {}
                            break; // Break inner loop, continue to next city
                        }
                    }

                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    if (attempt < maxRetriesPerCity) {
                        System.out.println("⚠️ Stale/missing city dropdown for " + cityName + ", retrying... (Attempt " + attempt + "/" + maxRetriesPerCity + ")");
                        try { Thread.sleep(retryDelayMs); } catch (InterruptedException ignored) {}
                        continue;
                    } else {
                        System.out.println("❌ Failed to select " + cityName + ", trying next city...");
                        // Clear field before next city
                        try {
                            WebElement cityFieldToClear = wait.until(ExpectedConditions.elementToBeClickable(city));
                            cityFieldToClear.click();
                            js.executeScript("arguments[0].value = '';", cityFieldToClear);
                            cityFieldToClear.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
                            cityFieldToClear.sendKeys(org.openqa.selenium.Keys.DELETE);
                            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                        } catch (Exception ignored) {}
                        break; // Move to next city
                    }
                } catch (WebDriverException e) {
                    if (e.getMessage() != null && e.getMessage().contains("target frame detached")) {
                        if (attempt < maxRetriesPerCity) {
                            System.out.println("⚠️ Frame detached on city dropdown for " + cityName + ", retrying... (Attempt " + attempt + "/" + maxRetriesPerCity + ")");
                            try { Thread.sleep(retryDelayMs); } catch (InterruptedException ignored) {}
                            continue;
                        } else {
                            System.out.println("❌ Frame detached error for " + cityName + ", trying next city...");
                            // Clear field before next city
                            try {
                                WebElement cityFieldToClear = wait.until(ExpectedConditions.elementToBeClickable(city));
                                cityFieldToClear.click();
                                js.executeScript("arguments[0].value = '';", cityFieldToClear);
                                cityFieldToClear.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
                                cityFieldToClear.sendKeys(org.openqa.selenium.Keys.DELETE);
                                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                            } catch (Exception ignored) {}
                            break; // Move to next city
                        }
                    }
                    if (attempt < maxRetriesPerCity) {
                        System.out.println("⚠️ Error selecting " + cityName + ", retrying... (Attempt " + attempt + "/" + maxRetriesPerCity + ")");
                        try { Thread.sleep(retryDelayMs); } catch (InterruptedException ignored) {}
                        continue;
                    } else {
                        System.out.println("❌ Error with " + cityName + ", trying next city...");
                        // Clear field before next city
                        try {
                            WebElement cityFieldToClear = wait.until(ExpectedConditions.elementToBeClickable(city));
                            cityFieldToClear.click();
                            js.executeScript("arguments[0].value = '';", cityFieldToClear);
                            cityFieldToClear.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
                            cityFieldToClear.sendKeys(org.openqa.selenium.Keys.DELETE);
                            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                        } catch (Exception ignored) {}
                        break; // Move to next city
                    }
                }
            }
            
            // If we successfully clicked, exit the outer loop
            if (dropdownClicked) {
                break;
            }
        }

        if (!dropdownClicked) {
            throw new RuntimeException("❌ Failed to select any city from the list after trying all options");
        }

        // Wait after city selection
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // ✅ ZIP CODE WITH ERROR HANDLING
        // Fill ZIP Code (try default first)
        type(zipCode, "60601");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        // Check for ZIP code warnings/errors after entering
        if (hasZipCodeError()) {
            System.out.println("⚠️ ZIP code error/warning detected with: 60601");
            
            // If we have a selected city, use its ZIP code
            if (selectedCity != null) {
                String correctZipCode = getZipCodeForCity(selectedCity);
                System.out.println("🔄 Clearing ZIP code and trying correct ZIP for " + selectedCity + ": " + correctZipCode);
                
                // Clear the invalid ZIP code
                clearZipCode();
                
                // Type the correct ZIP code for the selected city
                try {
                    WebElement zipField = wait.until(ExpectedConditions.elementToBeClickable(zipCode));
                    zipField.click();
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                    zipField.sendKeys(correctZipCode);
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    
                    // Check if error is resolved
                    if (hasZipCodeError()) {
                        System.out.println("⚠️ ZIP code " + correctZipCode + " still has error, trying alternative ZIP codes...");
                        
                        // Try alternative ZIP codes for the city
                        String[] alternativeZips = getAlternativeZipCodes(selectedCity);
                        boolean zipValid = false;
                        
                        for (String altZip : alternativeZips) {
                            System.out.println("🔍 Trying ZIP code: " + altZip);
                            clearZipCode();
                            
                            try {
                                zipField = wait.until(ExpectedConditions.elementToBeClickable(zipCode));
                                zipField.click();
                                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                                zipField.sendKeys(altZip);
                                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                            } catch (Exception e) {
                                System.out.println("⚠️ Error typing ZIP code " + altZip + ": " + e.getMessage());
                                continue;
                            }
                            
                            // Check if this ZIP code is valid (no error)
                            if (!hasZipCodeError()) {
                                System.out.println("✅ Valid ZIP code found: " + altZip);
                                zipValid = true;
                                break;
                            } else {
                                System.out.println("❌ ZIP code " + altZip + " still has error, trying next...");
                            }
                        }
                        
                        if (!zipValid) {
                            System.out.println("⚠️ Could not find valid ZIP code, but continuing anyway...");
                        }
                    } else {
                        System.out.println("✅ ZIP code " + correctZipCode + " accepted for " + selectedCity);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error updating ZIP code: " + e.getMessage());
                }
            } else {
                System.out.println("⚠️ No city selected, cannot determine correct ZIP code");
            }
        } else {
            System.out.println("✅ ZIP code accepted: 60601");
        }

        // DO NOT REMOVE - Fill student name fields
        type(studentFirstName, "Alex");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        type(studentLastName, "Smith");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        type(studentEmail, studentEmailValue);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        js.executeScript("window.scrollBy(0,400);");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // ---------- ISEE TEST DATE ----------
        WebElement testDate =
                wait.until(ExpectedConditions.elementToBeClickable(iseeTestDate));
        testDate.click();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        wait.until(ExpectedConditions.elementToBeClickable(calendarDate)).click();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // ---------- GRADE (ANT DESIGN – CORRECT WAY) ----------
        WebElement selector =
                wait.until(ExpectedConditions.elementToBeClickable(gradeSelector));

        js.executeScript("arguments[0].click();", selector);

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // KEY FIX: send keys to active element
        WebElement activeInput = driver.switchTo().activeElement();
        activeInput.sendKeys(Keys.ARROW_DOWN);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        activeInput.sendKeys(Keys.ENTER);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
// ---------- CHECKBOXES (FINAL & SAFE) ----------
        WebElement privacy =
                wait.until(ExpectedConditions.presenceOfElementLocated(privacyCheckbox));
        js.executeScript("arguments[0].click();", privacy);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        WebElement terms =
                wait.until(ExpectedConditions.presenceOfElementLocated(termsCheckbox));
        js.executeScript("arguments[0].click();", terms);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // ---------- SUBMIT ----------
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        WebElement submit =
                wait.until(ExpectedConditions.elementToBeClickable(submitAndFinishBtn));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", submit);
        js.executeScript("arguments[0].click();", submit);
        System.out.println("✅ Clicked Submit & Finish button");
        
        // ✅ Wait for processing, then success or error message
        String submitMessage = waitForSubmitMessage();
        
        if (submitMessage != null && !submitMessage.isEmpty()) {
            // Check if it's an error message
            String lowerMessage = submitMessage.toLowerCase();
            if (lowerMessage.contains("error") || lowerMessage.contains("failed") || 
                lowerMessage.contains("invalid") || lowerMessage.contains("try again") ||
                lowerMessage.contains("cannot") || lowerMessage.contains("unable")) {
                throw new RuntimeException("❌ Submission failed with error: " + submitMessage);
            } else if (lowerMessage.contains("success") || lowerMessage.contains("completed") || 
                      lowerMessage.contains("saved") || lowerMessage.contains("submitted") ||
                      lowerMessage.contains("redirected") || submitMessage.contains("Success")) {
                System.out.println("✅ Submission successful - Full message: " + submitMessage);
                // Additional wait to ensure everything is processed
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                System.out.println("✅ Ready to proceed to Yopmail for credentials");
            } else {
                // Ambiguous message - check URL or wait a bit more
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.contains("/success") || currentUrl.contains("/complete") || currentUrl.contains("/welcome")) {
                    System.out.println("✅ Submission appears successful (URL changed) - Message: " + submitMessage);
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                    System.out.println("✅ Ready to proceed to Yopmail for credentials");
                } else {
                    System.out.println("⚠️ Ambiguous message received: " + submitMessage);
                    System.out.println("⚠️ Proceeding to Yopmail, but please verify submission was successful");
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                }
            }
        } else {
            // No message found - check URL as fallback
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("/success") || currentUrl.contains("/complete") || currentUrl.contains("/welcome")) {
                System.out.println("✅ No message but URL changed - assuming success");
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                System.out.println("✅ Ready to proceed to Yopmail for credentials");
            } else {
                System.out.println("⚠️ No success or error message found after submit");
                System.out.println("⚠️ Proceeding to Yopmail, but please verify submission was successful");
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
    }
}
