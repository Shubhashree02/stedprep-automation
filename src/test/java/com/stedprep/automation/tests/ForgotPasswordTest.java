package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.ForgotPasswordPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ForgotPasswordTest extends BaseTest {

    // Static email for testing
    private static final String TEST_EMAIL = "utsavshah09@yopmail.com";

    @Test
    public void verifyForgotPasswordFlow() {
        
        String parentEmail = TEST_EMAIL;
        System.out.println("📧 Using test email: " + parentEmail);
        
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(driver);
        
        // Step 1: Navigate to parent login page and click forgot password link
        forgotPasswordPage.navigateToForgotPassword();
        
        // Step 2: Enter the email
        forgotPasswordPage.enterEmail(parentEmail);
        
        // Step 3: Click "Send Verification Code" button
        // This will automatically print the toast message that appears
        forgotPasswordPage.clickSendVerificationCode();
        
        // Step 4: Verify if verification code was sent successfully
        // Message is already printed by clickSendVerificationCode() method
        boolean isSent = forgotPasswordPage.isVerificationCodeSent();
        
        if (!isSent) {
            String errorMsg = forgotPasswordPage.getErrorMessage();
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Assert.fail("Failed to send verification code: " + errorMsg);
            } else {
                Assert.fail("Failed to send verification code - no response received");
            }
        }
    }
}
