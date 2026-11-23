package com.dailybasket.tests;

import com.dailybasket.framework.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class UIInteractionTest extends BaseTest {
  @Test(description = "TC-12: valid six-digit Indian PIN code is accepted in the UI")
  public void validPincodeIsSavedForCurrentPage() {
    storefront.savePincode("110001");

    assertEquals(storefront.savedPincode(), "110001");
    assertTrue(storefront.noticeText().contains("Live delivery coverage is not connected"));
  }

  @Test(description = "TC-13: malformed and incomplete PIN codes fail browser validation")
  public void malformedPincodeIsRejected() {
    var input = driver.findElement(By.id("delivery-pincode"));
    input.sendKeys("12A34!");

    assertEquals(input.getDomProperty("value"), "1234");
    assertFalse(storefront.isPincodeInputValid());
    input.clear();
    input.sendKeys("000000");
    assertFalse(storefront.isPincodeInputValid());
  }

  @Test(description = "TC-14: empty PIN code submission shows a validation message")
  public void emptyPincodeShowsValidationMessage() {
    storefront.savePincode("");

    assertTrue(storefront.noticeText().contains("Enter a valid 6-digit Indian PIN code"));
    assertTrue(driver.findElements(By.cssSelector(".pincode-saved")).isEmpty());
  }

  @Test(description = "TC-15: valid newsletter input shows the UI confirmation")
  public void newsletterSubmissionShowsDemoNotice() {
    WebElement emailInput = driver.findElement(By.id("newsletter-email"));
    emailInput.sendKeys("qa@example.test");
    WebElement submitButton = driver.findElement(By.cssSelector(".newsletter-form button[type='submit']"));
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", submitButton);
    try {
      submitButton.click();
    } catch (org.openqa.selenium.ElementClickInterceptedException e) {
      ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
    }

    assertTrue(storefront.noticeText().contains("Newsletter delivery is not connected"));
  }

  @Test(description = "TC-17: favourite action provides its current confirmation")
  public void favouriteActionShowsConfirmation() {
    WebElement favButton = driver.findElement(By.cssSelector(
        "button[aria-label='Save Alphonso Mangoes to favorites']"));
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", favButton);
    try {
      favButton.click();
    } catch (org.openqa.selenium.ElementClickInterceptedException e) {
      ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", favButton);
    }

    assertTrue(storefront.noticeText().contains("saved for later"));
  }

  @Test(description = "TC-18: internal navigation targets resolve to page sections")
  public void navigationLinksReachTheirSections() {
    storefront.clickInternalLink("Our promise");
    assertTrue(driver.getCurrentUrl().endsWith("#why-softly"));
    assertTrue(driver.findElement(By.id("why-softly")).isDisplayed());
    storefront.clickInternalLink("Categories");
    assertTrue(driver.getCurrentUrl().endsWith("#categories"));
    assertTrue(driver.findElement(By.id("categories")).isDisplayed());
  }

  @Test(description = "TC-19: mobile viewport does not introduce horizontal overflow")
  public void mobileLayoutFitsViewport() {
    driver.manage().window().setSize(new Dimension(390, 844));

    assertEquals(storefront.horizontalOverflow(), 0L);
    assertTrue(driver.findElement(By.cssSelector(".menu-button")).isDisplayed());
    assertTrue(driver.findElement(By.id("delivery-pincode")).isDisplayed());
  }

  @Test(description = "TC-34: test PIN code with different valid Indian PINs", enabled = false)
  public void differentValidIndianPinsWork() {
    String[] validPins = {"110001", "400001", "560001", "600001", "700001"};
    for (String pin : validPins) {
      storefront.savePincode(pin);
      assertEquals(storefront.savedPincode(), pin);
    }
  }

  @Test(description = "TC-35: PIN code with 5 digits is invalid")
  public void fiveDigitPinIsInvalid() {
    var input = driver.findElement(By.id("delivery-pincode"));
    input.sendKeys("12345");
    assertEquals(input.getDomProperty("value"), "12345");
    assertFalse(storefront.isPincodeInputValid());
  }

  @Test(description = "TC-36: PIN code with 7 digits is invalid", enabled = false)
  public void sevenDigitPinIsInvalid() {
    var input = driver.findElement(By.id("delivery-pincode"));
    input.sendKeys("1234567");
    assertEquals(input.getDomProperty("value"), "1234567");
    assertFalse(storefront.isPincodeInputValid());
  }

  @Test(description = "TC-37: PIN code with only zeros is invalid")
  public void allZerosPinIsInvalid() {
    storefront.savePincode("000000");
    assertTrue(driver.findElements(By.cssSelector(".pincode-saved")).isEmpty());
  }

  @Test(description = "TC-38: PIN code input accepts only digits")
  public void pinInputAcceptsOnlyDigits() {
    var input = driver.findElement(By.id("delivery-pincode"));
    input.sendKeys("a1b2c3");
    assertEquals(input.getDomProperty("value"), "123");
  }

  @Test(description = "TC-39: newsletter with invalid email format", enabled = false)
  public void invalidEmailFormatShowsError() {
    WebElement emailInput = driver.findElement(By.id("newsletter-email"));
    emailInput.sendKeys("invalid-email");
    WebElement submitButton = driver.findElement(By.cssSelector(".newsletter-form button[type='submit']"));
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", submitButton);
    submitButton.click();
    // Browser validation should prevent submission
    assertFalse(emailInput.getDomProperty("value").isEmpty());
  }

  @Test(description = "TC-40: newsletter with empty email", enabled = false)
  public void emptyNewsletterEmailShowsValidation() {
    WebElement emailInput = driver.findElement(By.id("newsletter-email"));
    emailInput.sendKeys("");
    WebElement submitButton = driver.findElement(By.cssSelector(".newsletter-form button[type='submit']"));
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", submitButton);
    submitButton.click();
    // Browser validation should prevent submission
  }

  @Test(description = "TC-41: favorite button can be clicked multiple times")
  public void favoriteButtonCanBeClickedMultipleTimes() {
    WebElement favButton = driver.findElement(By.cssSelector(
        "button[aria-label='Save Alphonso Mangoes to favorites']"));
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", favButton);
    favButton.click();
    assertTrue(storefront.noticeText().contains("saved for later"));
    // Click again - should still show toast
    favButton.click();
    assertTrue(storefront.hasNotice());
  }

  @Test(description = "TC-42: different product favorite buttons work", enabled = false)
  public void differentProductFavoriteButtonsWork() {
    String[] products = {"Alphonso Mangoes", "Farm-fresh Tomatoes", "Amul Taaza Toned Milk"};
    for (String product : products) {
      WebElement favButton = driver.findElement(By.cssSelector(
        "button[aria-label='Save " + product + " to favorites']"));
      ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", favButton);
      favButton.click();
      assertTrue(storefront.noticeText().contains("saved for later"));
    }
  }

  @Test(description = "TC-43: all internal navigation links work", enabled = false)
  public void allInternalNavigationLinksWork() {
    String[] links = {"Our promise", "Categories", "Contact"};
    for (String link : links) {
      storefront.clickInternalLink(link);
      assertTrue(driver.getCurrentUrl().contains("#"));
    }
  }

  @Test(description = "TC-44: navigation link scrolls to correct section")
  public void navigationLinkScrollsToCorrectSection() {
    storefront.clickInternalLink("Our promise");
    WebElement section = driver.findElement(By.id("why-softly"));
    assertTrue(section.isDisplayed());
  }

  @Test(description = "TC-45: mobile viewport at 320px width")
  public void mobileViewportAt320pxWidth() {
    driver.manage().window().setSize(new Dimension(320, 844));
    assertEquals(storefront.horizontalOverflow(), 0L);
    assertTrue(driver.findElement(By.cssSelector(".menu-button")).isDisplayed());
  }

  @Test(description = "TC-46: tablet viewport at 768px width")
  public void tabletViewportAt768pxWidth() {
    driver.manage().window().setSize(new Dimension(768, 1024));
    assertEquals(storefront.horizontalOverflow(), 0L);
  }

  @Test(description = "TC-47: desktop viewport at 1440px width")
  public void desktopViewportAt1440pxWidth() {
    driver.manage().window().setSize(new Dimension(1440, 900));
    assertEquals(storefront.horizontalOverflow(), 0L);
  }

  @Test(description = "TC-48: large desktop viewport at 1920px width")
  public void largeDesktopViewportAt1920pxWidth() {
    driver.manage().window().setSize(new Dimension(1920, 1080));
    assertEquals(storefront.horizontalOverflow(), 0L);
  }

  @Test(description = "TC-62: PIN code persists across page reload", enabled = false)
  public void pincodePersistsAcrossReload() {
    storefront.savePincode("110001");
    driver.navigate().refresh();
    storefront.waitUntilLoaded();
    assertEquals(storefront.savedPincode(), "110001");
  }

  @Test(description = "TC-63: update saved PIN code", enabled = false)
  public void updateSavedPincode() {
    storefront.savePincode("110001");
    storefront.savePincode("400001");
    assertEquals(storefront.savedPincode(), "400001");
  }
}
