package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.ParentRegistrationPage;
import com.stedprep.automation.utils.TestDataUtils;
import org.testng.annotations.Test;

public class ParentRegistrationTest extends BaseTest {

    @Test
    public void verifyParentRegistrationStepOne() {

        ParentRegistrationPage registrationPage =
                new ParentRegistrationPage(driver);

        // 🔥 REQUIRED CHANGE
        registrationPage.open();

        registrationPage.fillParentStepOne(
                TestDataUtils.generateFirstName(),
                TestDataUtils.generateLastName(),
                TestDataUtils.generateEmail(),
                TestDataUtils.generatePassword(),
                TestDataUtils.generatePhone()
        );
    }
}
