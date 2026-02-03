package com.stedprep.automation.pages;

import com.stedprep.automation.config.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ForgotPasswordPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.js = (JavascriptExecutor) driver;
    }

    // =======================
    // Navigation
    // =======================
    
    /**
     * Navigate to parent login page
     */
    public void openParentLoginPage() {
        driver.get(
                ConfigReader.get("base.url") +
                        ConfigReader.get("parent.login.path")
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(forgotPasswordLink));
        System.out.println("✅ Navigated to parent login page");
    }

    // =======================
    // Locators
    // =======================
    
    // Forgot Password link on login page
    private By forgotPasswordLink = 
            By.xpath("//button[@type='button' and contains(@class,'ant-btn-link')]//span[text()='Forgot Password?']");
    
    // Forgot Password page elements
    private By emailInput = 
            By.id("email"); // Using ID as it's the most reliable
    
    private By sendVerificationCodeButton = 
            By.xpath("//button[@type='submit']//span[text()='Send Verification Code']");
    
    // Notification/Toast message locators (appear in top right corner)
    // Note: The app uses ant-notification, not ant-message
    private By notificationContainer = 
            By.xpath("//div[contains(@class,'ant-notification-notice')]");
    
    private By notificationMessage = 
            By.xpath("//div[contains(@class,'ant-notification-notice-message')]");
    
    private By successNotification = 
            By.xpath("//div[contains(@class,'ant-notification-notice-success')]");
    
    private By errorNotification = 
            By.xpath("//div[contains(@class,'ant-notification-notice-error')]");
    
    // Legacy toast message locators (for backward compatibility)
    private By toastMessageContainer = 
            By.xpath("//div[contains(@class,'ant-message')]");
    
    private By successToastMessage = 
            By.xpath("//div[contains(@class,'ant-message-success')]//span[contains(@class,'ant-message-custom-content')] | //div[contains(@class,'ant-message-success')]//span[contains(@class,'ant-message-notice-content')] | //div[contains(@class,'ant-message-success')]");
    
    private By errorToastMessage = 
            By.xpath("//div[contains(@class,'ant-message-error')]//span[contains(@class,'ant-message-custom-content')] | //div[contains(@class,'ant-message-error')]//span[contains(@class,'ant-message-notice-content')] | //div[contains(@class,'ant-message-error')]");
    
    // More specific locators for toast message text
    private By toastMessageText = 
            By.xpath("//div[contains(@class,'ant-message')]//span[contains(@class,'ant-message-custom-content') or contains(@class,'ant-message-notice-content')] | //div[contains(@class,'ant-message')]//div[contains(@class,'ant-message-notice-message')]");
    
    // Regular success/error messages (if not toast)
    private By successMessage = 
            By.xpath("//div[contains(@class,'success') and not(contains(@class,'ant-message'))]");
    
    private By errorMessage = 
            By.xpath("//div[contains(@class,'error') and not(contains(@class,'ant-message'))]");

    // Verify OTP page (after Send Verification Code)
    private By verifyOtpHeading = By.xpath("//h3[contains(@class,'ant-typography') and contains(.,'Verify OTP')]");
    private By otpInputs = By.cssSelector("div.ant-flex.ant-flex-gap-small input.ant-input[maxlength='1']");
    private By verifyCodeButton = By.xpath("//button[@type='submit']//span[text()='Verify Code']");

    // Reset Password page (after Verify Code)
    private By resetPasswordPageText = By.xpath("//p[@class='text-gray-500' and contains(.,'Create a new password for your account')]");
    private By newPasswordInput = By.id("teacherPasswordForm_newPassword");
    private By confirmPasswordInput = By.id("teacherPasswordForm_confirmPassword");
    private By resetPasswordButton = By.xpath("//button[@type='submit']//span[text()='Reset Password']");

    // =======================
    // Actions
    // =======================
    
    /**
     * Click on the "Forgot Password?" link
     */
    public void clickForgotPasswordLink() {
        try {
            WebElement forgotPasswordBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(forgotPasswordLink)
            );
            
            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", forgotPasswordBtn);
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            
            // Click using JavaScript for reliability
            js.executeScript("arguments[0].click();", forgotPasswordBtn);
            System.out.println("✅ Clicked 'Forgot Password?' link");
            
            // Wait for page transition and verify URL
            wait.until(ExpectedConditions.urlContains("/forgot-password"));
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("/forgot-password") && currentUrl.contains("role=parent")) {
                System.out.println("✅ Successfully navigated to forgot password page: " + currentUrl);
            } else {
                throw new RuntimeException("❌ Did not navigate to forgot password page. Current URL: " + currentUrl);
            }
            
            // Wait for email input to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            System.out.println("✅ Forgot password page loaded successfully");
            
        } catch (TimeoutException e) {
            throw new RuntimeException("❌ Forgot Password link not found or page did not load", e);
        }
    }

    /**
     * Navigate to parent login and click forgot password link
     * This is a convenience method that combines both actions
     */
    public void navigateToForgotPassword() {
        openParentLoginPage();
        clickForgotPasswordLink();
    }

    /**
     * Enter email address in the forgot password form
     */
    public void enterEmail(String email) {
        try {
            WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(emailInput));
            
            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", emailField);
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            
            // Click to focus
            emailField.click();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            
            // Clear using multiple methods for reliability
            emailField.clear();
            js.executeScript("arguments[0].value = '';", emailField);
            emailField.sendKeys(Keys.CONTROL + "a");
            emailField.sendKeys(Keys.DELETE);
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            
            // Enter email
            emailField.sendKeys(email);
            System.out.println("✅ Entered email: " + email);
            
            // Wait a moment for validation
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to enter email: " + e.getMessage(), e);
        }
    }

    /**
     * Click the "Send Verification Code" button. Returns immediately; caller should then
     * wait for OTP page (waitForVerifyOtpPage()) and get OTP from Yopmail.
     */
    public void clickSendVerificationCode() {
        try {
            WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(sendVerificationCodeButton));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", sendButton);
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            wait.until(driver -> {
                WebElement btn = driver.findElement(sendVerificationCodeButton);
                return btn.isEnabled() && !btn.getAttribute("class").contains("disabled");
            });
            js.executeScript("arguments[0].click();", sendButton);
            System.out.println("✅ Clicked Send Verification Code");
        } catch (TimeoutException e) {
            throw new RuntimeException("❌ Send Verification Code button not found or not clickable", e);
        }
    }
    
    /**
     * Capture notification message immediately after click (no delay)
     * Checks continuously every 5ms to catch it before it disappears
     * The app uses ant-notification, not ant-message
     * Notification appears and disappears VERY quickly, so we check extremely frequently
     */
    private String captureToastImmediately() {
        // Check immediately with NO delay, then poll extremely frequently
        // Use 5ms intervals for maximum responsiveness (200 times per second)
        for (int attempt = 0; attempt < 2000; attempt++) { // 2000 attempts * 5ms = 10 seconds
            try {
                // Method 1: Direct query for message div - FASTEST approach
                // Check the message div directly first (most specific, fastest)
                String message = (String) js.executeScript(
                    "try { " +
                    "  // Direct query for message div - fastest path " +
                    "  var messageDivs = document.querySelectorAll('div.ant-notification-notice-message'); " +
                    "  for (var i = 0; i < messageDivs.length; i++) { " +
                    "    var div = messageDivs[i]; " +
                    "    var rect = div.getBoundingClientRect(); " +
                    "    if (rect.width > 0 && rect.height > 0) { " +
                    "      var text = div.textContent || div.innerText || ''; " +
                    "      text = text.trim(); " +
                    "      if (text.length > 0) { " +
                    "        return text; " +
                    "      } " +
                    "    } " +
                    "  } " +
                    "  " +
                    "  // Fallback: check notification containers " +
                    "  var containers = document.querySelectorAll('div[class*=\"ant-notification-notice\"]'); " +
                    "  for (var i = 0; i < containers.length; i++) { " +
                    "    var container = containers[i]; " +
                    "    var rect = container.getBoundingClientRect(); " +
                    "    if (rect.width > 0 && rect.height > 0) { " +
                    "      var msgDiv = container.querySelector('div.ant-notification-notice-message'); " +
                    "      if (msgDiv) { " +
                    "        var text = msgDiv.textContent || msgDiv.innerText || ''; " +
                    "        text = text.trim(); " +
                    "        if (text.length > 0) { " +
                    "          return text; " +
                    "        } " +
                    "      } " +
                    "    } " +
                    "  } " +
                    "} catch(e) {} " +
                    "return null;"
                );
                
                if (message != null && !message.isEmpty() && message.length() > 0) {
                    System.out.println("📢 Notification Message: " + message);
                    return message;
                }
                
                // Method 2: Use WebDriver findElements as backup (slower but reliable)
                try {
                    java.util.List<WebElement> messageDivs = driver.findElements(notificationMessage);
                    for (WebElement msgDiv : messageDivs) {
                        try {
                            if (msgDiv.isDisplayed()) {
                                String text = msgDiv.getText().trim();
                                if (!text.isEmpty()) {
                                    System.out.println("📢 Notification Message (WebDriver): " + text);
                                    return text;
                                }
                            }
                        } catch (StaleElementReferenceException e) {
                            continue;
                        }
                    }
                    
                    // Fallback: check containers
                    java.util.List<WebElement> notifications = driver.findElements(notificationContainer);
                    for (WebElement notification : notifications) {
                        try {
                            if (notification.isDisplayed()) {
                                try {
                                    WebElement msgDiv = notification.findElement(notificationMessage);
                                    String text = msgDiv.getText().trim();
                                    if (!text.isEmpty()) {
                                        System.out.println("📢 Notification Message (WebDriver container): " + text);
                                        return text;
                                    }
                                } catch (NoSuchElementException e) {
                                    // Continue
                                }
                            }
                        } catch (StaleElementReferenceException e) {
                            continue;
                        }
                    }
                } catch (Exception e) {
                    // Continue polling
                }
                
            } catch (Exception e) {
                // Continue checking
            }
            
            // Extremely short delay - 5ms (200 times per second)
            try { Thread.sleep(5); } catch (InterruptedException ignored) {}
        }
        
        System.out.println("⚠️ No notification message captured after 10 seconds");
        return null;
    }

    /**
     * Complete forgot password flow: enter email and send verification code
     */
    public void requestPasswordReset(String email) {
        enterEmail(email);
        clickSendVerificationCode();
    }

    /**
     * Wait for and get any toast message (success or error)
     * Toast messages appear in the top right corner
     */
    public String waitForToastMessage() {
        try {
            // Wait for toast container to appear (with shorter timeout for quick check)
            WebDriverWait toastWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            toastWait.until(ExpectedConditions.presenceOfElementLocated(toastMessageContainer));
            
            // Small delay to ensure message text is fully loaded
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            
            // Check for error toast first
            try {
                java.util.List<WebElement> errorToasts = driver.findElements(errorToastMessage);
                for (WebElement toast : errorToasts) {
                    if (toast.isDisplayed()) {
                        String message = toast.getText().trim();
                        if (!message.isEmpty()) {
                            System.out.println(message);
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // No error toast found, continue
            }
            
            // Check for success toast
            try {
                java.util.List<WebElement> successToasts = driver.findElements(successToastMessage);
                for (WebElement toast : successToasts) {
                    if (toast.isDisplayed()) {
                        String message = toast.getText().trim();
                        if (!message.isEmpty()) {
                            System.out.println(message);
                            return message;
                        }
                    }
                }
            } catch (Exception e) {
                // No success toast found
            }
            
        } catch (Exception e) {
            // No toast message appeared yet
        }
        
        return null;
    }

    /**
     * Check for success message (toast or regular)
     */
    public String getSuccessMessage() {
        // First check for toast message
        String toastMsg = waitForToastMessage();
        if (toastMsg != null && !toastMsg.isEmpty()) {
            // Check if it's a success message
            try {
                java.util.List<WebElement> successToasts = driver.findElements(successToastMessage);
                for (WebElement toast : successToasts) {
                    if (toast.isDisplayed() && toast.getText().trim().equals(toastMsg)) {
                        return toastMsg;
                    }
                }
            } catch (Exception e) {
                // Continue to check regular messages
            }
        }
        
        // Check for regular success message
        try {
            java.util.List<WebElement> successMsgs = driver.findElements(successMessage);
            for (WebElement msg : successMsgs) {
                if (msg.isDisplayed()) {
                    String message = msg.getText().trim();
                    if (!message.isEmpty()) {
                        System.out.println(message);
                        return message;
                    }
                }
            }
        } catch (Exception e) {
            // No success message found
        }
        
        return null;
    }

    /**
     * Check for error message (toast or regular)
     */
    public String getErrorMessage() {
        // First check for toast message
        String toastMsg = waitForToastMessage();
        if (toastMsg != null && !toastMsg.isEmpty()) {
            // Check if it's an error message
            try {
                java.util.List<WebElement> errorToasts = driver.findElements(errorToastMessage);
                for (WebElement toast : errorToasts) {
                    if (toast.isDisplayed() && toast.getText().trim().equals(toastMsg)) {
                        return toastMsg;
                    }
                }
            } catch (Exception e) {
                // Continue to check regular messages
            }
        }
        
        // Check for regular error message
        try {
            java.util.List<WebElement> errorMsgs = driver.findElements(errorMessage);
            for (WebElement msg : errorMsgs) {
                if (msg.isDisplayed()) {
                    String message = msg.getText().trim();
                    if (!message.isEmpty()) {
                        System.out.println(message);
                        return message;
                    }
                }
            }
        } catch (Exception e) {
            // No error message found
        }
        
        return null;
    }

    /**
     * Get any message (success or error) and print it
     * This method checks for both toast and regular messages
     */
    public String getAnyMessage() {
        // Wait a moment for messages to appear
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        // Check for toast messages first (they appear faster)
        String toastMsg = waitForToastMessage();
        if (toastMsg != null && !toastMsg.isEmpty()) {
            return toastMsg;
        }
        
        // Check for regular success message
        String successMsg = getSuccessMessage();
        if (successMsg != null && !successMsg.isEmpty()) {
            return successMsg;
        }
        
        // Check for regular error message
        String errorMsg = getErrorMessage();
        if (errorMsg != null && !errorMsg.isEmpty()) {
            return errorMsg;
        }
        
        return null;
    }

    /**
     * Verify if verification code was sent successfully
     */
    public boolean isVerificationCodeSent() {
        // Wait for message to appear
        String message = getAnyMessage();
        
        if (message == null || message.isEmpty()) {
            return false;
        }
        
        String lowerMessage = message.toLowerCase();
        
        // Check if it's an error message
        if (lowerMessage.contains("not found") || 
            lowerMessage.contains("invalid") || 
            lowerMessage.contains("error") ||
            lowerMessage.contains("failed")) {
            return false;
        }
        
        // Check if it's a success message
        if (lowerMessage.contains("sent") || 
            lowerMessage.contains("verification") ||
            lowerMessage.contains("success") ||
            lowerMessage.contains("code")) {
            return true;
        }
        
        // If message doesn't clearly indicate success or error, check URL
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("/verify") || currentUrl.contains("/reset")) {
            return true;
        }
        
        return false;
    }

    /** Wait until the Verify OTP page is visible (heading "Verify OTP"). */
    public void waitForVerifyOtpPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(verifyOtpHeading));
        System.out.println("✅ Verify OTP page loaded");
    }

    /** Enter the 6-digit OTP into the six single-digit inputs. */
    public void enterOtp(String otp) {
        if (otp == null || otp.length() != 6) {
            throw new IllegalArgumentException("OTP must be 6 digits");
        }
        java.util.List<WebElement> inputs = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(otpInputs));
        if (inputs.size() < 6) {
            throw new RuntimeException("Expected 6 OTP inputs, found " + inputs.size());
        }
        for (int i = 0; i < 6; i++) {
            inputs.get(i).clear();
            inputs.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }
        System.out.println("✅ Entered OTP");
    }

    /** Click the Verify Code button. */
    public void clickVerifyCode() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(verifyCodeButton));
        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", btn);
        }
        System.out.println("✅ Clicked Verify Code");
    }

    /** Wait until the Reset Password page is visible (text "Create a new password for your account"). */
    public void waitForResetPasswordPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(resetPasswordPageText));
        System.out.println("✅ Reset Password page loaded");
    }

    /** Enter new password in the Reset Password form. */
    public void enterNewPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(newPasswordInput));
        field.clear();
        field.sendKeys(password);
        System.out.println("✅ Entered new password");
    }

    /** Enter confirm password in the Reset Password form. */
    public void enterConfirmPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordInput));
        field.clear();
        field.sendKeys(password);
        System.out.println("✅ Entered confirm password");
    }

    /** Click the Reset Password button. */
    public void clickResetPassword() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(resetPasswordButton));
        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", btn);
        }
        System.out.println("✅ Clicked Reset Password");
    }

    /** If a success message appears after reset, capture and print it. Otherwise do nothing. */
    public void printSuccessMessageIfPresent() {
        try {
            java.util.List<WebElement> success = driver.findElements(successNotification);
            for (WebElement el : success) {
                if (el.isDisplayed()) {
                    String msg = el.getText();
                    if (msg != null && !msg.trim().isEmpty()) {
                        System.out.println("📢 Success: " + msg.trim());
                        return;
                    }
                }
            }
            java.util.List<WebElement> toasts = driver.findElements(successToastMessage);
            for (WebElement el : toasts) {
                if (el.isDisplayed()) {
                    String msg = el.getText();
                    if (msg != null && !msg.trim().isEmpty()) {
                        System.out.println("📢 Success: " + msg.trim());
                        return;
                    }
                }
            }
        } catch (Exception ignored) {
            // No success message is ok
        }
    }
}
