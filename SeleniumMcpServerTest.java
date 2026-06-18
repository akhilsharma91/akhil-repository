package com.example.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeleniumMcpServerTest {

    @Test
    void testSeleniumMcpServerStartsAndServesRemoteBrowser() throws Exception {
        WebDriverManager.chromedriver().setup();
        try (SeleniumMcpServer server = new SeleniumMcpServer()) {
            server.start();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            RemoteWebDriver driver = server.createClient(options);
            try {
                String html = "<html><head><title>MCP Server</title></head><body><div id='status'>OK</div></body></html>";
                String encodedHtml = URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
                String dataUrl = "data:text/html;charset=utf-8," + encodedHtml;

                driver.get(dataUrl);
                assertEquals("MCP Server", driver.getTitle());
                assertEquals("OK", driver.findElement(By.id("status")).getText());
            } finally {
                driver.quit();
            }
        }
    }
}
