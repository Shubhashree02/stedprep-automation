package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.ParentRegistrationPage;
import com.stedprep.automation.utils.TestDataUtils;
import org.testng.annotations.Test;

public class ParentRegistrationTest extends BaseTest {

    @Test
    public void verifyParentRegistrationWithOtp() {

        ParentRegistrationPage registrationPage =
                new ParentRegistrationPage(driver);

        // Step 0: Open registration page
        registrationPage.open();

        // 🔥 Generate UNIQUE email ONCE (new every run)
        String email = TestDataUtils.generateEmail();

        // Step 1: Fill parent registration details
        registrationPage.fillParentStepOne(
                TestDataUtils.generateFirstName(),
                TestDataUtils.generateLastName(),
                email, // SAME email used later for OTP
                TestDataUtils.generatePassword(),
                TestDataUtils.generatePhone()
        );

        // Step 2: Fetch OTP from Yopmail and verify
        registrationPage.verifyOtpFromYopmail(email);
    }
}
