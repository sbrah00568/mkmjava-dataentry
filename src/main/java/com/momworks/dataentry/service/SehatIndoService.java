package com.momworks.dataentry.service;

import com.momworks.dataentry.configuration.SehatIndoProperties;
import com.momworks.dataentry.dto.SehatIndoDto;
import com.momworks.dataentry.dto.SehatIndoImunisasiDto;
import com.momworks.dataentry.enums.ImunisasiBaduta;
import com.momworks.dataentry.enums.ImunisasiBayi;
import com.momworks.dataentry.enums.SehatIndoColumn;
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
public class SehatIndoService {

    private final SehatIndoProperties sehatIndoProperties;

    private static final String BAYI = "bayi";
    private static final String SHEET_NAME = "Sheet1";
    private static final int FIRST_DATA_ROW = 2;
    private static final int FIRST_INDEX = 0;
    private static final String POINT_ZERO = ".0";
    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";

    public void execute(MultipartFile xlsxFile, String type) {
        AppiumDriver driver = null;
        try {
            // Specify device and application details
            UiAutomator2Options options = new UiAutomator2Options();
            options.setDeviceName(sehatIndoProperties.getDeviceName())
                    .setPlatformVersion(sehatIndoProperties.getPlatformVersion())
                    .setAppPackage(sehatIndoProperties.getAppPackage())
                    .setNoReset(true);

            // Initialize the AppiumDriver using the specified URL and options for AndroidDriver
            driver = new AndroidDriver(new URL(sehatIndoProperties.getAppiumUrl()), options);

            // Set up WebDriverWait to wait for elements to be clickable with a 10-second timeout
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Read data from xlsx file
            List<SehatIndoDto> dataImunisasi = onReadData(xlsxFile, type);

            // Automate login to apps
            onLogin(webDriverWait);

            // Automate data entry process
            onWriteData(webDriverWait, dataImunisasi);

        } catch (IOException e) {
            log.error("An error occurred: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private List<SehatIndoDto> onReadData(MultipartFile xlsxFile, String type) throws IOException {
        List<SehatIndoDto> dataImunisasi = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile.getInputStream())) {
            Sheet sheet = workbook.getSheet(SHEET_NAME);

            Map<Integer, String> headerMap = new HashMap<>();
            boolean isHeader = true;

            for (Row row : sheet) {
                int currentRowIndex = row.getRowNum();
                if (currentRowIndex < FIRST_DATA_ROW) {
                    continue; // Skip title rows
                }

                if (isHeader) {
                    extractHeaders(row, headerMap);
                    isHeader = false;
                } else {
                    SehatIndoDto sehatIndoDto = new SehatIndoDto();
                    sehatIndoDto.setType(type);
                    processRowData(row, headerMap, sehatIndoDto, type);
                    dataImunisasi.add(sehatIndoDto);
                }
            }
        }

        log.info("Data retrieval successful. File: '{}', Size: {}", xlsxFile.getOriginalFilename(), dataImunisasi.size());
        return dataImunisasi;
    }

    private void extractHeaders(Row row, Map<Integer, String> headerMap) {
        for (Cell cell : row) {
            headerMap.put(cell.getColumnIndex(), cell.toString());
        }
    }

    private void processRowData(Row row, Map<Integer, String> headerMap, SehatIndoDto sehatIndoDto, String type) {
        for (Cell cell : row) {
            String columnName = headerMap.get(cell.getColumnIndex());

            if (SehatIndoColumn.NAMA_ANAK.getName().equals(columnName)) {
                sehatIndoDto.setNamaAnak(cell.toString());
            } else if (SehatIndoColumn.USIA_ANAK.getName().equals(columnName)) {
                sehatIndoDto.setUsiaAnak(cell.toString());
            } else if (SehatIndoColumn.TANGGAL_LAHIR_ANAK.getName().equals(columnName)) {
                sehatIndoDto.setTanggalLahirAnak(cell.toString());
                sehatIndoDto.setImunisasiRutin(isImunisasiRutin(sehatIndoDto, type));
            } else if (SehatIndoColumn.JENIS_KELAMIN_ANAK.getName().equals(columnName)) {
                sehatIndoDto.setJenisKelaminAnak(cell.toString());
            } else if (SehatIndoColumn.NAMA_ORANG_TUA.getName().equals(columnName)) {
                sehatIndoDto.setNamaOrangTua(cell.toString());
            } else if (SehatIndoColumn.PUSKESMAS.getName().equals(columnName)) {
                sehatIndoDto.setPuskesmas(cell.toString());
            } else {
                processImunisasiDetails(columnName, cell, sehatIndoDto, type);
            }

        }
    }

    private boolean isImunisasiRutin(SehatIndoDto sehatIndoDto, String type) {
        int month = Integer.valueOf(sehatIndoDto.getUsiaAnak().split(SPACE)[FIRST_INDEX], FIRST_INDEX);
        return month >= (type.equals(BAYI) ? 0 : 12) && month < (type.equals(BAYI) ? 12 : 24);
    }

    private void processImunisasiDetails(String columnName, Cell cell, SehatIndoDto sehatIndoDto, String type) {
        String code = BAYI.equals(type) ?
                ImunisasiBayi.getCode(columnName) : ImunisasiBaduta.getCode(columnName);

        if (sehatIndoDto.getDetailImunisasiMap() == null) {
            sehatIndoDto.setDetailImunisasiMap(new HashMap<>());
        }

        if (sehatIndoDto.getDetailImunisasiMap().get(code) == null) {
            sehatIndoDto.getDetailImunisasiMap().put(code, new SehatIndoImunisasiDto());
        }

        if (columnName.contains(SehatIndoColumn.TANGGAL.getName())) {
            sehatIndoDto.getDetailImunisasiMap().get(code).setTanggal(cell.toString());
        } else if (columnName.contains(SehatIndoColumn.POS.getName())) {
            sehatIndoDto.getDetailImunisasiMap().get(code).setPos(cell.toString());
        } else if (columnName.contains(SehatIndoColumn.STATUS.getName())) {
            sehatIndoDto.getDetailImunisasiMap().get(code)
                    .setStatus(Integer.valueOf(cell.toString().replace(POINT_ZERO, EMPTY_STRING)));
        }
    }


    private void onLogin(WebDriverWait webDriverWait) {
        // Input phone number
        WebElement inputPhoneNumber = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View//android.widget.EditText")));
        inputPhoneNumber.click();
        inputPhoneNumber.sendKeys(sehatIndoProperties.getPhoneNumber());

        // Removes focus after input phone number
        WebElement background = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View")));
        background.click();

        // Continue to next input pin
        WebElement continueButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View//android.view.View[@content-desc=\"Lanjut\"]")));
        continueButton.click();

        // Input pin
        WebElement inputPin = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View/android.view.View[2]//android.widget.EditText")));
        inputPin.click();
        inputPin.sendKeys(sehatIndoProperties.getPin());

        // Select save
        WebElement selectSave = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout" +
                        "/android.view.View/android.view.View/android.view.View//android.view.View[@content-desc=\"Simpan\"]")));
        selectSave.click();

        log.info("Login to the main menu was successful.");
    }

    private void onWriteData(WebDriverWait webDriverWait, List<SehatIndoDto> sehatIndoDtoList) {
        // Select "Imunisasi" button from layanan klaster
        WebElement imunisasiButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//android.widget.ScrollView//android.view.View[@content-desc=\"Imunisasi\"]")));
        imunisasiButton.click();

        for (SehatIndoDto sehatIndoDto : sehatIndoDtoList) {
            if (sehatIndoDto.isImunisasiRutin()) {
                // TODO: process input imunisasi rutin
            } else {
                // TODO: process input riwayat imunisasi
            }
        }

    }

}
