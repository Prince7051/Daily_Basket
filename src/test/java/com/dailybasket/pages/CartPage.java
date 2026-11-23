package com.dailybasket.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {
  private final WebDriver driver;
  private final WebDriverWait wait;

  public CartPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public String heading() {
    return driver.findElement(By.id("cart-heading")).getText();
  }

  public String subtotal() {
    return driver.findElement(By.cssSelector(".cart-summary > div:nth-child(1) strong")).getText();
  }

  public String deliveryFee() {
    return driver.findElement(By.cssSelector(".cart-summary > div:nth-child(2) strong")).getText();
  }

  public String total() {
    return driver.findElement(By.cssSelector(".cart-total strong")).getText();
  }

  public String freeDeliveryMessage() {
    return driver.findElement(By.cssSelector(".free-delivery-message")).getText();
  }

  public int itemCount(String productName) {
    return Integer.parseInt(driver.findElement(By.xpath(
        "//div[contains(concat(' ',normalize-space(@class),' '),' cart-item ')][.//strong[normalize-space()='" + productName
            + "']]//div[contains(@class,'quantity-control')]/span")).getText());
  }

  public void changeQuantity(String productName, boolean add) {
    String action = add ? "Add one " : "Remove one ";
    driver.findElement(By.cssSelector("button[aria-label='" + action + productName + "']")).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart-heading")));
  }

  public void continueToCheckout() {
    WebElement checkoutButton = driver.findElement(By.cssSelector(".checkout-button"));
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", checkoutButton);
    checkoutButton.click();
  }

  public String checkoutNotice() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast"))).getText();
  }
}
