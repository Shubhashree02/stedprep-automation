package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.ParentRegistrationPage;
import com.stedprep.automation.utils.TestDataUtils;
import org.testng.annotations.Test;

public class ParentRegistrationTest extends BaseTest {

    @Test
    public void verifyCompleteParentRegistrationFlow() {

        ParentRegistrationPage registrationPage =
                new ParentRegistrationPage(driver);

        // 🔹 Generate data ONCE
        String firstName = TestDataUtils.generateFirstName();
        String lastName = TestDataUtils.generateLastName();
        String email = TestDataUtils.generateEmail();   // SAME email used for OTP
        String password = TestDataUtils.generatePassword();
        String phone = TestDataUtils.generatePhone();

        // 🔹 Step 0 – Open Registration Page
        registrationPage.open();

        // 🔹 Step 1 – Parent Information
        registrationPage.fillParentStepOne(
                firstName,
                lastName,
                email,
                password,
                phone
        );

        // 🔹 Step 2 – OTP Verification (Yopmail)
        registrationPage.verifyOtpFromYopmail(email);

        // 🔹 Step 3 – Student Information
        registrationPage.fillStudentStepThree();

        // ✅ Flow ends at Submit & Finish
    }
}
