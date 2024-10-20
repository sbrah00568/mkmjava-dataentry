package com.momworks.dataentry.util;

import io.appium.java_client.AppiumDriver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Automation {

    private final AppiumDriver driver;
    private final WebDriverWait driverWait;
    private final PointerInput finger;

    public Automation(AppiumDriver driver, Duration waitDuration) {
        this.driver = driver;
        this.driverWait = new WebDriverWait(driver, waitDuration);
        this.finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    }

    public int getScreenWidth() {
        return driver.manage().window().getSize().getWidth();
    }

    public int getScreenHeight() {
        return driver.manage().window().getSize().getHeight();
    }

    public int getCenterX() {
        return getScreenWidth() / 2;
    }

    public int getCenterY() {
        return getScreenHeight() / 2;
    }

    public Elements getElements(String xpath) {
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        Document doc = Jsoup.parse(driver.getPageSource(), Parser.xmlParser());
        return doc.getAllElements();
    }

    public void click(String xpath) {
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    public void clickAndTypes(String xpath, CharSequence... keysToSend) {
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
        driverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).sendKeys(keysToSend);
    }

    public void scroll(int startX, int startY, int endX, int endY) {
        Sequence seq = new Sequence(finger, 1);
        seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
    }

    public void tap(int targetX, int targetY) {
        Sequence seq = new Sequence(finger, 1);
        seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), targetX, targetY));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
    }

}
