package com.example.app;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class SeleniumMcpServer implements AutoCloseable {

    private ChromeDriverService service;

    public void start() throws IOException {
        WebDriverManager.chromedriver().setup();
        service = new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .withSilent(true)
                .build();
        service.start();
    }

    public URL getUrl() {
        return Optional.ofNullable(service)
                .map(ChromeDriverService::getUrl)
                .orElseThrow(() -> new IllegalStateException("Server is not started"));
    }

    @SuppressWarnings("null")
    public RemoteWebDriver createClient(ChromeOptions options) {
        if (service == null || !service.isRunning()) {
            throw new IllegalStateException("Selenium MCP server is not running");
        }
        URL url = getUrl();
        if (url == null) {
            throw new IllegalStateException("Failed to get server URL");
        }
        return new RemoteWebDriver(url, options);
    }

    @Override
    public void close() {
        if (service != null && service.isRunning()) {
            service.stop();
        }
    }

    public static void main(String[] args) {
        try (SeleniumMcpServer server = new SeleniumMcpServer()) {
            server.start();
            System.out.println("Selenium MCP server started at " + server.getUrl());
            System.out.println("Press CTRL+C to stop.");
            Thread.currentThread().join();
        } catch (IOException | InterruptedException ex) {
            System.err.println("Failed to start Selenium MCP server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
