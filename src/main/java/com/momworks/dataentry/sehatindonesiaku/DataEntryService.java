package com.momworks.dataentry.sehatindonesiaku;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataEntryService {

    private final DataEntryPropertiesDto dataEntryPropertiesDto;

    // TODO: kejarType is consumed by on write data method
    public void execute(MultipartFile xlsxFile, String kejarType) {
        AppiumDriver driver = null;
        try {
            // Specify device and application details
            UiAutomator2Options options = new UiAutomator2Options();
            options.setDeviceName("Medium Phone API 31")
                    .setPlatformVersion("12")
                    .setAppPackage(dataEntryPropertiesDto.getAppPackage())
                    .setNoReset(true);

            // Initialize the AppiumDriver using the specified URL and options for AndroidDriver
            driver = new AndroidDriver(new URL(dataEntryPropertiesDto.getAppiumUrl()), options);

            // Set up WebDriverWait to wait for elements to be clickable with a 10-second timeout
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Start automation
            List<Map<String, String>> dataImunisasi = onReadData(xlsxFile); // TODO: dataImunisasi is consumed by on write data method
            onLogin(webDriverWait);
            // TODO: on write data

        } catch (IOException e) {
            log.error("some error occurred:: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private List<Map<String, String>> onReadData(MultipartFile xlsxFile) throws IOException {
        List<Map<String, String>> dataImunisasi = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile.getInputStream())) {
            Sheet sheet = workbook.getSheet("Sheet1");

            Map<Integer, String> headerMap = new HashMap<>();

            boolean isHeader = true;
            int countRow = 0;
            for (Row row : sheet) {
                // skip title
                if (countRow >= 2) {
                    if (isHeader) {
                        // set header
                        isHeader = false;
                        int countHeaderCell = 0;
                        for (Cell cell : row) {
                            headerMap.put(countHeaderCell, cell.toString());
                            countHeaderCell++;
                        }
                    } else {
                        // set body
                        Map<String, String> bodyMap = new HashMap<>();
                        int countBodyCell = 0;
                        for (Cell cell : row) {
                            bodyMap.put(headerMap.get(countBodyCell), cell.toString());
                            countBodyCell++;
                        }
                        dataImunisasi.add(bodyMap);
                    }
                }
                countRow++;
            }
        }

        return  dataImunisasi;
    }

    private void onLogin(WebDriverWait webDriverWait) {
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
