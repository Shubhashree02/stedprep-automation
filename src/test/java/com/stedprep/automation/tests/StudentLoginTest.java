package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.StudentLoginPage;
import com.stedprep.automation.utils.StudentCredentialStore;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class StudentLoginTest extends BaseTest {

    @Test
    public void verifyStudentLogin() {

        if (StudentCredentialStore.username == null) {
            throw new RuntimeException(
                    "Student credentials missing. Run ParentRegistrationTest first."
            );
        }

        StudentLoginPage loginPage = new StudentLoginPage(driver);

        // 🔹 Open student login page
        loginPage.open();

        // 🔹 Login using credentials from Yopmail
        loginPage.login(
                StudentCredentialStore.username,
                StudentCredentialStore.password
        );

        // 🔹 SUCCESS ASSERTION (URL-BASED – STABLE)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.urlContains("/student/welcome"));
        Assert.assertTrue(
                driver.getCurrentUrl().contains("/student/welcome"),
                "Student login failed. Expected /student/welcome URL"
        );
    }
}
