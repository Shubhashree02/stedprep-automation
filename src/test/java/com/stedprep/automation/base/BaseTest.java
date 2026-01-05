package com.stedprep.automation.base;
import java.time.Duration;


import com.stedprep.automation.config.ConfigReader;
import com.stedprep.automation.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();

        // 🔥 GLOBAL WAIT FOR APP LOAD
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get(ConfigReader.get("base.url"));
    }


//    @AfterMethod
//    public void tearDown() {
//        DriverFactory.quitDriver();
//    }
}
