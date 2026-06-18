package com.example.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeleniumSampleTest {

    @Test
    void testHeadlessChromeLoadsInlineHtml() throws Exception {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        String html = "<html><head><title>Test Page</title></head><body><h1 id='message'>Selenium Ready</h1></body></html>";
        String encodedHtml = URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
        String dataUrl = "data:text/html;charset=utf-8," + encodedHtml;

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(dataUrl);
            assertEquals("Test Page", driver.getTitle());
            assertEquals("Selenium Ready", driver.findElement(By.id("message")).getText());
        } finally {
            driver.quit();
        }
    }
}
