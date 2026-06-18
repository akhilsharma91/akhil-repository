package com.example.app;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutomationExerciseCartTest {

    @Test
    @SuppressWarnings("null")
    void openAutomationExerciseAndAddProductToCart() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        @SuppressWarnings("null")
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        new File("test").mkdirs();
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test/extent-report.html");
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        ExtentTest extentTest = extent.createTest("Automation Exercise Cart Test");

        try {
            driver.get("https://automationexercise.com");
            wait.until(ExpectedConditions.titleContains("Automation Exercise"));
            extentTest.pass("Opened AutomationExercise homepage.");

            List<WebElement> productLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//a[contains(@href,'product_details') and not(contains(@style,'display:none'))]")
            ));
            assertFalse(productLinks.isEmpty(), "No product details links were found on the Automation Exercise homepage");

            WebElement clickableProduct = productLinks.stream()
                    .filter(element -> element.isDisplayed() && element.isEnabled())
                    .findFirst()
                    .orElse(productLinks.get(0));
            wait.until(ExpectedConditions.elementToBeClickable(clickableProduct));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableProduct);
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("product_details"),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add to cart') or contains(@class, 'add-to-cart')]"))));

            WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add to cart') or contains(@class,'add-to-cart') or contains(@class,'cart')]")));
            addToCartButton.click();

            WebElement viewCartLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'view cart') or contains(@href, 'view_cart') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'view cart now')]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewCartLink);

            wait.until(ExpectedConditions.urlContains("/view_cart"));
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl != null) {
                assertTrue(currentUrl.contains("/view_cart"), "Expected to be on the view cart page.");
            }
            extentTest.pass("Navigated to view cart page.");

            String pageSource = driver.getPageSource();
            if (pageSource != null) {
                String pageText = pageSource.toLowerCase();
                assertTrue(pageText.contains("cart"), "Expected the view cart page to include cart content.");
                extentTest.pass("Verified cart page content.");
            }
        } catch (Throwable t) {
            extentTest.fail(t);
            throw t;
        } finally {
            extent.flush();
            driver.quit();
        }
    }
}
