package com.momworks.dataentry.sehatindonesiaku;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DataEntryService {

    private final DataEntryPropertiesDto dataEntryPropertiesDto;

    public void startEntryData() {
        try {
            // Specify device and application details
            UiAutomator2Options options = new UiAutomator2Options();
            options.setDeviceName("Medium Phone API 31")
                    .setPlatformVersion("12")
                    .setAppPackage(dataEntryPropertiesDto.getAppPackage())
                    .setNoReset(true);

            // Initialize the AppiumDriver using the specified URL and options for AndroidDriver
            AppiumDriver driver = new AndroidDriver(new URL(dataEntryPropertiesDto.getAppiumUrl()), options);

            // Set up WebDriverWait to wait for elements to be clickable with a 10-second timeout
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Automation action
            onLogin(webDriverWait);

            driver.quit();

        } catch (IOException e) {
            String errorMessage = e.getMessage();
            System.out.println("some error occurred:: " + errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    public void onLogin(WebDriverWait webDriverWait) {
        // Input phone number
        WebElement inputPhoneNumber = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View//android.widget.EditText")));
        inputPhoneNumber.click();
        inputPhoneNumber.sendKeys(dataEntryPropertiesDto.getPhoneNumber());

        // Removes focus after input phone number
        WebElement background = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View")));
        background.click();

        // Continue to next input pin
        WebElement continueButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.view.View[@content-desc=\"Lanjut\"]")));
        continueButton.click();

        // Input pin
        WebElement inputPin = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View/android.view.View[2]//android.widget.EditText")));
        inputPin.click();
        inputPin.sendKeys(dataEntryPropertiesDto.getPin());

        // Select save
        WebElement selectSave = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View//android.view.View[@content-desc=\"Simpan\"]")));
        selectSave.click();
    }

}
