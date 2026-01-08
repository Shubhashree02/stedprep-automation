package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.ParentRegistrationPage;
import com.stedprep.automation.utils.TestDataUtils;
import org.testng.annotations.Test;
import com.stedprep.automation.pages.YopmailPage;
import com.stedprep.automation.utils.StudentCredentialStore;


public class ParentRegistrationTest extends BaseTest {

    @Test
    public void verifyCompleteParentRegistrationFlow() {

        ParentRegistrationPage registrationPage =
                new ParentRegistrationPage(driver);

        // 🔹 Generate data ONCE
        String firstName = TestDataUtils.generateFirstName();
        String lastName = TestDataUtils.generateLastName();

        String parentEmail = TestDataUtils.generateEmail();          // OTP email
        String studentEmail = TestDataUtils.generateStudentEmail(); // ✅ NEW

        String password = TestDataUtils.generatePassword();
        String phone = TestDataUtils.generatePhone();

        // 🔹 Step 0 – Open Registration Page
        registrationPage.open();

        // 🔹 Step 1 – Parent Information
        registrationPage.fillParentStepOne(
                firstName,
                lastName,
                parentEmail,
                password,
                phone
        );

        // 🔹 Step 2 – OTP Verification (Yopmail)
        registrationPage.verifyOtpFromYopmail(parentEmail);

        // 🔹 Step 3 – Student Information (FIXED)
        registrationPage.fillStudentStepThree(studentEmail);

        // ✅ Flow ends at Submit & Finish
        // 🔹 Read student credentials from Yopmail
        YopmailPage yopmailPage = new YopmailPage(driver);
        String[] creds = yopmailPage.fetchStudentCredentials(parentEmail);

// 🔹 Store for next test
        StudentCredentialStore.username = creds[0];
        StudentCredentialStore.password = creds[1];

    }
}
