package com.stedprep.automation.driver;

import com.stedprep.automation.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver() {

        String browser = ConfigReader.get("browser");
        if (browser == null) browser = "chrome";

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();
                if ("true".equalsIgnoreCase(ConfigReader.get("headless"))) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--start-maximized");

                driver.set(new ChromeDriver(options));
                break;

            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
