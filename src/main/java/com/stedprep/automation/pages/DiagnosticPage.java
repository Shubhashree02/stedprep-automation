package com.stedprep.automation.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Robust Page Object Model for Student Diagnostic Test flow.
 * - WebDriverWait only (no Thread.sleep)
 * - StaleElementReferenceException handled with retry
 * - Selenium locators and native click where possible (JS only when needed).
 */
public class DiagnosticPage {

    private static final Duration DEFAULT_WAIT_SEC = Duration.ofSeconds(15); // For page loads
    private static final Duration ELEMENT_WAIT_SEC = Duration.ofSeconds(3); // For question-loop (DOM usually updates in under 1s)
    private static final Duration POLL_INTERVAL_MS = Duration.ofMillis(20);
    private static final int MAX_RETRIES = 3;

    private final WebDriver driver;
    private final WebDriverWait wait;

    // After login: Start Diagnostic Test
    private static final By START_DIAGNOSTIC_BTN =
            By.xpath("//button[contains(.,'Start Diagnostic Test')]");

    // Section page: verify section loaded
    private static final By SECTION_TITLE =
            By.xpath("//h3[contains(normalize-space(),'Section')]");

    // Start Section
    private static final By START_SECTION_BTN =
            By.xpath("//button[contains(.,'Start Section')]");

    // Next button (any button containing Next, excluding Next Section)
    private static final By NEXT_BTN =
            By.xpath("//button[contains(.,'Next') and not(contains(.,'Next Section'))]");

    // Next Section button (normalize-space in case of extra spaces)
    private static final By NEXT_SECTION_BTN =
            By.xpath("//button[contains(normalize-space(.),'Next Section') or contains(.,'Next Section')]");

    // Submit (final question of last section)
    private static final By SUBMIT_BTN =
            By.xpath("//button[contains(.,'Submit')]");

    // Option rows: clickable option (cursor-pointer inside answer row)
    private static final By OPTION_CLICKABLE =
            By.cssSelector("div[class*='justify-between'][class*='items-center'] div[class*='cursor-pointer']");

    // Selected state: option circle turned blue (bg-blue-500)
    private static final By OPTION_SELECTED =
            By.cssSelector("div[class*='bg-blue-500']");

    // Section heading on question page (h4 e.g. "Section 1: Verbal Reasoning")
    private static final By SECTION_HEADING_H4 =
            By.xpath("//h4[contains(normalize-space(),'Section')]");

    public DiagnosticPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_WAIT_SEC);
        this.wait.pollingEvery(POLL_INTERVAL_MS);
    }

    /** Verify diagnostic welcome page is loaded (Start Diagnostic Test button visible). */
    public boolean isDiagnosticPageLoaded() {
        try {
            WebDriverWait w = new WebDriverWait(driver, DEFAULT_WAIT_SEC);
            w.pollingEvery(POLL_INTERVAL_MS);
            w.until(ExpectedConditions.visibilityOfElementLocated(START_DIAGNOSTIC_BTN));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Click Start Diagnostic Test. Retries on StaleElementReferenceException. */
    public void clickStartDiagnostic() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(START_DIAGNOSTIC_BTN));
                btn.click();
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == MAX_RETRIES) throw new RuntimeException("clickStartDiagnostic failed after " + MAX_RETRIES + " retries", e);
            }
        }
    }

    /** Verify section intro page is loaded (Section title visible). */
    public boolean isSectionPageLoaded() {
        try {
            WebDriverWait w = new WebDriverWait(driver, DEFAULT_WAIT_SEC);
            w.pollingEvery(POLL_INTERVAL_MS);
            w.until(ExpectedConditions.visibilityOfElementLocated(SECTION_TITLE));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Click Start Section. Scrolls into view, retries on stale; uses JS click if native click is intercepted (e.g. header overlay). */
    public void clickStartSection() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(START_SECTION_BTN));
                js.executeScript("arguments[0].scrollIntoView({block:'center', behavior:'instant'});", btn);
                try {
                    btn.click();
                } catch (ElementClickInterceptedException e) {
                    js.executeScript("arguments[0].click();", btn);
                }
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == MAX_RETRIES) throw new RuntimeException("clickStartSection failed after " + MAX_RETRIES + " retries", e);
            }
        }
    }

    /** Wait until URL changes to question page: contains /student/diagnostic-test/, sectionName=, and questionNumber=. */
    public void waitForQuestionPageUrl() {
        WebDriverWait urlWait = new WebDriverWait(driver, DEFAULT_WAIT_SEC);
        urlWait.pollingEvery(POLL_INTERVAL_MS);
        urlWait.until(d -> {
            String url = d.getCurrentUrl();
            return url != null && url.contains("/student/diagnostic-test/")
                    && url.contains("sectionName=")
                    && url.contains("questionNumber=");
        });
    }

    /** Wait for section heading (h4) on question page, get text, print it if found, return text. */
    public String getAndPrintSectionHeading() {
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(SECTION_HEADING_H4));
        String text = heading.getText();
        if (text != null && !text.isEmpty()) {
            System.out.println("Section title: " + text);
        }
        return text;
    }

    /** Select first available option. Scrolls into view; uses JS click if intercepted by fixed header. Retries on stale. */
    public void selectFirstOption() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait optionWait = new WebDriverWait(driver, ELEMENT_WAIT_SEC);
        optionWait.pollingEvery(POLL_INTERVAL_MS);
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                WebElement first = optionWait.until(d -> {
                    java.util.List<WebElement> opts = d.findElements(OPTION_CLICKABLE);
                    return opts.isEmpty() ? null : opts.get(0);
                });
                wait.until(ExpectedConditions.elementToBeClickable(first));
                js.executeScript("arguments[0].scrollIntoView({block:'center', behavior:'instant'});", first);
                try {
                    first.click();
                } catch (ElementClickInterceptedException e) {
                    js.executeScript("arguments[0].click();", first);
                }
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == MAX_RETRIES) throw new RuntimeException("selectFirstOption failed after " + MAX_RETRIES + " retries", e);
            }
        }
    }

    /** Click Next button. Re-locates element each time. Retries on stale. */
    public void clickNext() {
        WebDriverWait elementWait = new WebDriverWait(driver, ELEMENT_WAIT_SEC);
        elementWait.pollingEvery(POLL_INTERVAL_MS);
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                WebElement btn = elementWait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN));
                btn.click();
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == MAX_RETRIES) throw new RuntimeException("clickNext failed after " + MAX_RETRIES + " retries", e);
            }
        }
    }

    /** True if Next button (ant primary) is enabled. */
    public boolean isNextButtonEnabled() {
        try {
            WebElement btn = driver.findElement(NEXT_BTN);
            return btn != null && btn.isEnabled() && btn.getAttribute("disabled") == null;
        } catch (Exception e) {
            return false;
        }
    }

    /** True if Next Section button is visible. */
    public boolean isNextSectionVisible() {
        try {
            return !driver.findElements(NEXT_SECTION_BTN).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /** True if Next Section button is visible and enabled (answer given). */
    public boolean isNextSectionEnabled() {
        try {
            WebElement btn = driver.findElement(NEXT_SECTION_BTN);
            return btn != null && btn.isDisplayed() && btn.isEnabled() && btn.getAttribute("disabled") == null;
        } catch (Exception e) {
            return false;
        }
    }

    /** True if Submit button is visible (final question). */
    public boolean isSubmitVisible() {
        try {
            return !driver.findElements(SUBMIT_BTN).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /** True if Submit button is visible and enabled (answer given). */
    public boolean isSubmitEnabled() {
        try {
            WebElement btn = driver.findElement(SUBMIT_BTN);
            return btn != null && btn.isDisplayed() && btn.isEnabled() && btn.getAttribute("disabled") == null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Complete one section: select option → wait for option color to change (blue) → click Next; if Next not there, click Next Section or Submit.
     * KISS: check option color changed, then click Next.
     */
    public void completeSection() {
        WebDriverWait optionsPresentWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        optionsPresentWait.pollingEvery(POLL_INTERVAL_MS);
        WebDriverWait optionBlueWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        optionBlueWait.pollingEvery(POLL_INTERVAL_MS);
        WebDriverWait tryNextWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        tryNextWait.pollingEvery(POLL_INTERVAL_MS);
        // Wait for new question: no option selected (blue gone). Skip on first question (page may have other blue elements).
        WebDriverWait newQuestionWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        newQuestionWait.pollingEvery(POLL_INTERVAL_MS);
        boolean firstQuestion = true;

        while (true) {
            // After Next (not on first question): wait for new question (no blue), then options present.
            if (!firstQuestion) {
                newQuestionWait.until(d -> d.findElements(OPTION_SELECTED).isEmpty());
            }
            optionsPresentWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(OPTION_CLICKABLE, 0));

            // Select first option of this question (retries on stale)
            selectFirstOption();

            // Wait for option color to change (blue) — then Next is active. KISS: check color, then click Next.
            optionBlueWait.until(ExpectedConditions.presenceOfElementLocated(OPTION_SELECTED));

            // Click Next (short timeout). Retry on stale. If Next not there = last question → click Next Section or Submit and return.
            try {
                for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                    try {
                        WebElement nextBtn = tryNextWait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN));
                        nextBtn.click();
                        firstQuestion = false;
                        break;
                    } catch (StaleElementReferenceException ex) {
                        if (attempt == MAX_RETRIES) throw ex;
                    }
                }
            } catch (TimeoutException e) {
                // Next not there = last question. Button (Next Section / Submit) is only enabled after option is blue; we already waited for blue.
                // Wait for Submit or Next Section to become clickable (re-find by locator each poll).
                WebDriverWait lastBtnWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                lastBtnWait.pollingEvery(POLL_INTERVAL_MS);
                try {
                    WebElement btn = lastBtnWait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BTN));
                    btn.click();
                    return;
                } catch (TimeoutException ignoreSubmit) {
                    try {
                        WebElement btn = lastBtnWait.until(ExpectedConditions.elementToBeClickable(NEXT_SECTION_BTN));
                        btn.click();
                        return;
                    } catch (TimeoutException ignoreNextSection) {
                        throw new RuntimeException("Last question but neither Submit nor Next Section found (button enabled only after option turns blue)", e);
                    }
                }
            }
        }
    }
}
