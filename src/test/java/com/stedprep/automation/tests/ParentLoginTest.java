package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.ParentLoginPage;
import com.stedprep.automation.utils.ParentCredentialStore;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class ParentLoginTest extends BaseTest {

    @Test
    public void verifyParentLogin() {
        if (ParentCredentialStore.email == null || ParentCredentialStore.email.isEmpty()
                || ParentCredentialStore.password == null || ParentCredentialStore.password.isEmpty()) {
            throw new RuntimeException("Parent credentials missing. Run ParentRegistrationTest first.");
        }

        ParentLoginPage loginPage = new ParentLoginPage(driver);
        loginPage.open();
        loginPage.login(ParentCredentialStore.email, ParentCredentialStore.password);

        // Handle subscription modal for new parents (coupon WELCOMEFREE), if it appears
        loginPage.handleSubscriptionModalIfPresent();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(driver -> {
            String url = driver.getCurrentUrl();
            return url.contains("/parent/") && !url.contains("/parent/login");
        });
        Assert.assertTrue(
                driver.getCurrentUrl().contains("/parent/") && !driver.getCurrentUrl().contains("/parent/login"),
                "Parent login failed. Expected redirect away from /parent/login"
        );
    }
}
