package com.stedprep.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {

    private WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    // Example locator
    private By pageTitle = By.tagName("h1");

    // Example action
    public String getPageTitleText() {
        return driver.findElement(pageTitle).getText();
    }
}
