package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.ForgotPasswordPage;
import com.stedprep.automation.pages.YopmailPage;
import com.stedprep.automation.utils.ParentCredentialStore;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ForgotPasswordTest extends BaseTest {

    @Test
    public void verifyForgotPasswordFlow() {
        if (ParentCredentialStore.email == null || ParentCredentialStore.email.isEmpty()) {
            throw new RuntimeException("Parent credentials missing. Run ParentRegistrationTest first.");
        }
        String parentEmail = ParentCredentialStore.email;
        System.out.println("📧 Using parent email from registration: " + parentEmail);

        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(driver);

        forgotPasswordPage.navigateToForgotPassword();
        forgotPasswordPage.enterEmail(parentEmail);
        forgotPasswordPage.clickSendVerificationCode();

        forgotPasswordPage.waitForVerifyOtpPage();
        String appWindowHandle = driver.getWindowHandle();
        YopmailPage yopmailPage = new YopmailPage(driver);
        String otp = yopmailPage.getOtpFromLatestEmail(parentEmail, appWindowHandle);
        Assert.assertNotNull(otp, "OTP should be retrieved from Yopmail");
        forgotPasswordPage.enterOtp(otp);
        forgotPasswordPage.clickVerifyCode();

        forgotPasswordPage.waitForResetPasswordPage();
        forgotPasswordPage.enterNewPassword("Admin@123");
        forgotPasswordPage.enterConfirmPassword("Admin@123");
        forgotPasswordPage.clickResetPassword();
        forgotPasswordPage.printSuccessMessageIfPresent();
    }
}
