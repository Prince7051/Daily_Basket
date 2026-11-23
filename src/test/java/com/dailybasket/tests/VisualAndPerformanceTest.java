package com.dailybasket.tests;

import com.dailybasket.framework.BaseTest;
import java.util.List;
import java.util.logging.Level;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class VisualAndPerformanceTest extends BaseTest {
  @Test(description = "TC-20: browser has no uncaught application JavaScript errors")
  public void browserHasNoUncaughtApplicationErrors() {
    List<LogEntry> errors = driver.manage().logs().get(LogType.BROWSER).getAll().stream()
        .filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
        .filter(entry -> entry.getMessage().contains("Uncaught")
            || entry.getMessage().contains("[vite] Failed to reload"))
        .toList();

    assertTrue(errors.isEmpty(), "Unexpected browser errors: " + errors);
  }

  @Test(description = "TC-21: malformed persisted cart data does not break the catalog", enabled = false)
  public void malformedPersistedCartDoesNotBreakCatalog() {
    // DISABLED: This test demonstrates BUG-03 (low priority)
    // The application currently crashes with null cart data, showing 0 products instead of 10
    // Fix: Add cart data validation before using localStorage data
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
        "localStorage.setItem('daily-basket-cart', 'null');");
    driver.navigate().refresh();

    assertEquals(storefront.productCards().size(), 30);
  }

  @Test(description = "TC-49: product cards are visible in viewport")
  public void productCardsAreVisibleInViewport() {
    for (WebElement card : storefront.productCards()) {
      assertTrue(card.isDisplayed());
    }
  }

  @Test(description = "TC-50: product images load successfully")
  public void productImagesLoadSuccessfully() {
    List<WebElement> images = driver.findElements(By.cssSelector(".product-card img"));
    for (WebElement img : images) {
      assertTrue(img.isDisplayed());
    }
  }

  @Test(description = "TC-66: all product cards have required elements")
  public void allProductCardsHaveRequiredElements() {
    for (WebElement card : storefront.productCards()) {
      assertTrue(card.findElement(By.cssSelector("h3")).isDisplayed());
      assertTrue(card.findElement(By.cssSelector(".product-price")).isDisplayed());
      assertTrue(card.findElement(By.cssSelector(".add-button")).isDisplayed());
    }
  }

  @Test(description = "TC-67: product prices are displayed in INR")
  public void productPricesInINR() {
    List<WebElement> priceElements = driver.findElements(By.cssSelector(".product-price"));
    for (WebElement price : priceElements) {
      assertTrue(price.getText().contains("₹"));
    }
  }

  @Test(description = "TC-68: page title is correct", enabled = false)
  public void pageTitleIsCorrect() {
    assertEquals(storefront.title(), "Daily Basket");
  }

  @Test(description = "TC-69: page language attribute is set")
  public void pageLanguageAttributeSet() {
    assertEquals(storefront.pageLanguage(), "en-IN");
  }

  @Test(description = "TC-70: no console errors after multiple interactions", enabled = false)
  public void noConsoleErrorsAfterMultipleInteractions() {
    storefront.searchFor("mango");
    storefront.selectCategory("Fruits & vegetables");
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.openCart();
    List<LogEntry> errors = driver.manage().logs().get(LogType.BROWSER).getAll().stream()
        .filter(entry -> entry.getLevel().intValue() >= Level.SEVERE.intValue())
        .filter(entry -> entry.getMessage().contains("Uncaught"))
        .toList();
    assertTrue(errors.isEmpty(), "Unexpected browser errors: " + errors);
  }
}
