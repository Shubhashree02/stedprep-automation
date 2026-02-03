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
     * Click the "Send Verification Code" button and immediately catch toast message
     */
    public void clickSendVerificationCode() {
        try {
            WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(sendVerificationCodeButton));
            
            // Scroll into view
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", sendButton);
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            
            // Wait until button is enabled
            wait.until(driver -> {
                WebElement btn = driver.findElement(sendVerificationCodeButton);
                return btn.isEnabled() && !btn.getAttribute("class").contains("disabled");
            });
            
            // Set up MutationObserver BEFORE clicking to catch notification the moment it appears
            // Store captured message in window object so we can retrieve it
            js.executeScript(
                "window.__capturedNotificationMsg = null; " +
                "var observer = new MutationObserver(function(mutations) { " +
                "  mutations.forEach(function(mutation) { " +
                "    mutation.addedNodes.forEach(function(node) { " +
                "      if (node.nodeType === 1) { " +
                "        if (node.classList && node.classList.contains('ant-notification-notice')) { " +
                "          var msgDiv = node.querySelector('div.ant-notification-notice-message'); " +
                "          if (msgDiv && !window.__capturedNotificationMsg) { " +
                "            window.__capturedNotificationMsg = (msgDiv.textContent || msgDiv.innerText || '').trim(); " +
                "            observer.disconnect(); " +
                "          } " +
                "        } " +
                "        var msgDivs = node.querySelectorAll('div.ant-notification-notice-message'); " +
                "        if (msgDivs.length > 0 && !window.__capturedNotificationMsg) { " +
                "          window.__capturedNotificationMsg = (msgDivs[0].textContent || msgDivs[0].innerText || '').trim(); " +
                "          observer.disconnect(); " +
                "        } " +
                "      } " +
                "    }); " +
                "    if (mutation.type === 'childList' && mutation.target) { " +
                "      var msgDivs = mutation.target.querySelectorAll('div.ant-notification-notice-message'); " +
                "      if (msgDivs.length > 0 && !window.__capturedNotificationMsg) { " +
                "        for (var i = 0; i < msgDivs.length; i++) { " +
                "          var text = (msgDivs[i].textContent || msgDivs[i].innerText || '').trim(); " +
                "          if (text.length > 0) { " +
                "            window.__capturedNotificationMsg = text; " +
                "            observer.disconnect(); " +
                "            break; " +
                "          } " +
                "        } " +
                "      } " +
                "    } " +
                "  }); " +
                "}); " +
                "observer.observe(document.body, { childList: true, subtree: true, attributes: false }); " +
                "observer.observe(document.documentElement, { childList: true, subtree: true, attributes: false }); " +
                "window.__notificationObserver = observer;"
            );
            
            // Now click the button
            js.executeScript("arguments[0].click();", sendButton);
            
            // Immediate check (no delay) - notification might appear instantly
            try {
                String immediateCheck = (String) js.executeScript(
                    "var msgDivs = document.querySelectorAll('div.ant-notification-notice-message'); " +
                    "for (var i = 0; i < msgDivs.length; i++) { " +
                    "  var text = (msgDivs[i].textContent || msgDivs[i].innerText || '').trim(); " +
                    "  if (text.length > 0) { return text; } " +
                    "} " +
                    "return null;"
                );
                if (immediateCheck != null && !immediateCheck.isEmpty()) {
                    js.executeScript("if (window.__notificationObserver) { window.__notificationObserver.disconnect(); window.__notificationObserver = null; }");
                    System.out.println("📢 Notification Message (Immediate): " + immediateCheck);
                    return;
                }
            } catch (Exception e) {
                // Continue to polling
            }
            
            // Poll for the captured message (check every 5ms) - wait up to 10 seconds
            for (int i = 0; i < 2000; i++) { // 2000 attempts * 5ms = 10 seconds
                try {
                    String capturedMessage = (String) js.executeScript("return window.__capturedNotificationMsg;");
                    if (capturedMessage != null && !capturedMessage.isEmpty()) {
                        // Clean up observer
                        js.executeScript("if (window.__notificationObserver) { window.__notificationObserver.disconnect(); window.__notificationObserver = null; }");
                        System.out.println("📢 Notification Message (MutationObserver): " + capturedMessage);
                        return;
                    }
                } catch (Exception e) {
                    // Continue
                }
                try { Thread.sleep(5); } catch (InterruptedException ignored) {}
            }
            
            // Clean up observer
            js.executeScript("if (window.__notificationObserver) { window.__notificationObserver.disconnect(); window.__notificationObserver = null; }");
            
            // Fallback: Check immediately with NO delay - notification appears and disappears very quickly
            String toastMessage = captureToastImmediately();
            if (toastMessage != null && !toastMessage.isEmpty()) {
                // Message is already printed
                return;
            }
            
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
}
