package com.momworks;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;

import java.net.MalformedURLException;
import java.net.URL;

public class DataEntryApplication {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        // Set desired capabilities for the Android emulator
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName("Pixel 6 API 30")
                .setPlatformVersion("11")
                .setChromedriverExecutable("/usr/local/bin/chromedriver")
                .setAppPackage("com.android.chrome")
                .setAppActivity("com.google.android.apps.chrome.Main")
                .setNoReset(true);

        // Initialize the Appium Driver
        AppiumDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);

        // Open Google
        driver.get("https://www.google.com");

        Thread.sleep(2000);

        // Action
        driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"com.android.chrome:id/url_bar\"]")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"com.android.chrome:id/url_bar\"]")).sendKeys("Makima");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//android.widget.TextView[@resource-id=\"com.android.chrome:id/line_1\" and @text=\"Makima\"]")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//android.widget.TextView[@text=\"Images\"]")).click();


        // Close the driver
        driver.quit();
    }
}
