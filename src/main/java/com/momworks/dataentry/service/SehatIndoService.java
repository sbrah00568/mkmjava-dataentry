package com.momworks.dataentry.service;

import com.momworks.dataentry.configuration.SehatIndoProperties;
import com.momworks.dataentry.dto.SehatIndoDto;
import com.momworks.dataentry.util.Automation;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

import static com.momworks.dataentry.constant.SehatIndoConstants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class SehatIndoService {

    private final SehatIndoProperties properties;
    private final SehatIndoDataService dataSvc;

    public void performDataEntry(MultipartFile xlsxFile) {
        AppiumDriver appiumDriver = null;
        try {
            // Specify device and application details
            UiAutomator2Options options = new UiAutomator2Options();
            options.setDeviceName(properties.getDeviceName())
                    .setPlatformVersion(properties.getPlatformVersion())
                    .setAppPackage(properties.getAppPackage())
                    .setNoReset(true);

            // Initialize the AppiumDriver using the specified URL and options for AndroidDriver
            appiumDriver = new AndroidDriver(new URL(properties.getAppiumUrl()), options);

            // Initialize automation util with 10 seconds waiting duration
            Automation automation = new Automation(appiumDriver, Duration.ofSeconds(10));

            // Perform login process to sehat indonesiaku apps
            automation.clickAndTypes(XPATH_LOGIN_NOMOR_TELEPON_FIELD, properties.getPhoneNumber());
            automation.click(XPATH_LOGIN_NOMOR_TELEPON_DISMISS);
            automation.click(XPATH_LOGIN_LANJUT_BUTTON);
            automation.clickAndTypes(XPATH_INPUT_PIN_FIELD, properties.getPin());
            automation.click(XPATH_INPUT_PIN_SIMPAN_BUTTON);
            log.info("Success login to 'Beranda'.");

            // Perform data entry process
            automation.click(XPATH_BERANDA_IMUNISASI_MENU);
            dataSvc.retrieveData(xlsxFile).forEach(sehatIndoDto -> {
                if (sehatIndoDto.getImunisasiRutinMap() != null) {
                    handleImunisasiRutin(automation, sehatIndoDto);
                }
            });

        } catch (IOException e) {
            log.error("An error occurred: {}", e.getMessage(), e);
        } finally {
            if (appiumDriver != null) {
                appiumDriver.quit();
            }
        }
    }

    private void handleImunisasiRutin(Automation automation, SehatIndoDto sehatIndoDto) {
        automation.click(XPATH_IMUNISASI_IMUNISASI_RUTIN_MENU);

        // Set tanggal imunisasi rutin
        automation.click(XPATH_IMUNISASI_RUTIN_TANGGAL_SEEKBAR);
        int centerX = automation.getCenterX();
        int centerY = automation.getCenterY();
        automation.scroll(centerX - (centerX / 2), centerY, centerX, (centerY + (centerY / 8))); // set day
        automation.scroll(centerX, centerY, centerX, (centerY + (centerY / 8))); // set month
        automation.scroll(centerX, centerY, centerX, centerY); // set year
        automation.tap(centerX, (int) (((double) centerY / 2) * 2.5)); // tap oke button

        // Set pos imunisasi rutin
        automation.click(XPATH_IMUNISASI_RUTIN_POS_IMUNISASI_DROPDOWN);
        automation.click(XPATH_POS_IMUNISASI_SEARCH_LOGO);
        automation.clickAndTypes(XPATH_POS_IMUNISASI_CARI_DISINI_FIELD, dataSvc.getPosName(sehatIndoDto));
        automation.click(XPATH_POS_IMUNISASI_CARI_BUTTON);
        String hasilPencarian;
        try {
            hasilPencarian = dataSvc.getHasilPencarian(automation.getElements(XPATH_HASIL_PENCARIAN_PAGE));
        } catch (TimeoutException e) {
            log.info("Pos imunisasi rutin not found. Set as 'DALAM GEDUNG' instead.");
            automation.click(XPATH_HASIL_PENCARIAN_TIDAK_DITEMUKAN_BACK_BUTTON);
            automation.click(XPATH_POS_IMUNISASI_SEARCH_LOGO);
            automation.clickAndTypes(XPATH_POS_IMUNISASI_CARI_DISINI_FIELD, DALAM_GEDUNG);
            automation.click(XPATH_POS_IMUNISASI_CARI_BUTTON);
            hasilPencarian = dataSvc.getHasilPencarian(automation.getElements(XPATH_HASIL_PENCARIAN_PAGE));
        }
        automation.click(String.format(XPATH_HASIL_PENCARIAN_ELEMENT, hasilPencarian));
        automation.click(XPATH_HASIL_PENCARIAN_PILIH_BUTTON);

        automation.click(XPATH_IMUNISASI_RUTIN_BACK_BUTTON);
    }

}
