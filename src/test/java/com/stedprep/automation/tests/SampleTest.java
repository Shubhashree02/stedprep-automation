package com.stedprep.automation.tests;

import com.stedprep.automation.base.BaseTest;
import com.stedprep.automation.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SampleTest extends BaseTest {

    @Test
    public void verifyHomePageTitle() {
        HomePage homePage = new HomePage(driver);
        String titleText = homePage.getPageTitleText();
        Assert.assertNotNull(titleText);
    }
}
