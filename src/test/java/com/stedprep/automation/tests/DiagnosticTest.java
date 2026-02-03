package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.DiagnosticPage;
import com.stedprep.automation.pages.StudentLoginPage;
import com.stedprep.automation.utils.StudentCredentialStore;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class DiagnosticTest extends BaseTest {

    @Test
    public void verifyDiagnosticAllSections() {

        if (StudentCredentialStore.username == null || StudentCredentialStore.password == null) {
            throw new RuntimeException("Student credentials missing. Run ParentRegistrationTest first.");
        }

        StudentLoginPage loginPage = new StudentLoginPage(driver);
        loginPage.open();
        loginPage.login(StudentCredentialStore.username, StudentCredentialStore.password);

        new WebDriverWait(driver, Duration.ofSeconds(40))
                .until(ExpectedConditions.urlContains("/student/welcome"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/student/welcome"),
                "Student login failed. Expected /student/welcome URL");

        DiagnosticPage diagnosticPage = new DiagnosticPage(driver);

        Assert.assertTrue(diagnosticPage.isDiagnosticPageLoaded(), "Diagnostic page not loaded");
        diagnosticPage.clickStartDiagnostic();

        int totalSections = 4; // All 4 sections: each has section intro → Start Section → questions → Next Section (or Submit on last)
        for (int sectionNum = 1; sectionNum <= totalSections; sectionNum++) {
            Assert.assertTrue(diagnosticPage.isSectionPageLoaded(),
                    "Section page not loaded for section " + sectionNum);
            System.out.println("\n" + "=".repeat(60));
            System.out.println("SECTION " + sectionNum);
            System.out.println("=".repeat(60));

            diagnosticPage.clickStartSection();

            diagnosticPage.waitForQuestionPageUrl();
            diagnosticPage.getAndPrintSectionHeading();

            diagnosticPage.completeSection();

            if (sectionNum < totalSections) {
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//h3[contains(normalize-space(),'Section')]")));
            }
        }

        System.out.println("\nSuccessfully processed " + totalSections + " section(s)");
    }
}
