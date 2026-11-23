package com.dailybasket.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StorefrontPage {
  private final WebDriver driver;
  private final WebDriverWait wait;
  private final By productCards = By.cssSelector(".product-card");
  private final By productTitles = By.cssSelector(".product-card h3");
  private final By notice = By.cssSelector(".toast");

  public StorefrontPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void waitUntilLoaded() {
    wait.until(ExpectedConditions.titleContains("Daily Basket"));
    wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(productCards, 0));
  }

  public List<WebElement> productCards() {
    return driver.findElements(productCards);
  }

  public List<String> productNames() {
    return driver.findElements(productTitles).stream().map(WebElement::getText).toList();
  }

  public void selectCategory(String category) {
    WebElement tab = driver.findElement(By.xpath(
        "//button[@role='tab' and normalize-space()=" + xpathLiteral(category) + "]"));
    scrollAndClick(tab);
  }

  public void searchFor(String query) {
    WebElement search = driver.findElement(By.id("product-search"));
    search.clear();
    search.sendKeys(query);
  }

  public void clearSearch() {
    driver.findElement(By.cssSelector("button[aria-label='Clear search']")).click();
  }

  public void sortBy(String value) {
    new Select(driver.findElement(By.cssSelector("select[aria-label='Sort groceries']")))
        .selectByValue(value);
  }

  public void addProduct(String productName, int quantity) {
    By product = By.xpath("//article[contains(@class,'product-card')][.//h3[normalize-space()="
        + xpathLiteral(productName) + "]]");
    WebElement productCard = driver.findElement(product);
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", productCard);
    for (int i = 0; i < quantity; i++) {
      WebElement addButton = productCard.findElement(By.cssSelector(".add-button"));
      scrollAndClick(addButton);
    }
  }

  public void savePincode(String pin) {
    WebElement input = driver.findElement(By.id("delivery-pincode"));
    input.clear();
    input.sendKeys(pin);
    driver.findElement(By.cssSelector("button[aria-label='Save delivery PIN code']")).click();
  }

  public String savedPincode() {
    return driver.findElement(By.cssSelector(".pincode-saved")).getText();
  }

  public String noticeText() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(notice)).getText();
  }

  public boolean hasNotice() {
    return !driver.findElements(notice).isEmpty();
  }

  public String title() {
    return driver.getTitle();
  }

  public String pageLanguage() {
    return driver.findElement(By.tagName("html")).getDomAttribute("lang");
  }

  public boolean isPincodeInputValid() {
    return (Boolean) ((JavascriptExecutor) driver).executeScript(
        "return document.getElementById('delivery-pincode').checkValidity()");
  }

  public void openCart() {
    WebElement cartButton = driver.findElement(By.cssSelector("button[aria-label^='Open cart']"));
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", cartButton);
    try {
      cartButton.click();
    } catch (org.openqa.selenium.ElementClickInterceptedException e) {
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartButton);
    }
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='dialog']")));
  }

  public void clickInternalLink(String linkText) {
    WebElement link = driver.findElement(By.linkText(linkText));
    scrollAndClick(link);
  }

  public long horizontalOverflow() {
    return (Long) ((JavascriptExecutor) driver).executeScript(
        "return document.documentElement.scrollWidth - document.documentElement.clientWidth");
  }

  private void scrollAndClick(WebElement element) {
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center'});", element);
    try {
      wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    } catch (org.openqa.selenium.ElementClickInterceptedException e) {
      // Fallback to JavaScript click if element is still intercepted
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
  }

  private String xpathLiteral(String value) {
    if (!value.contains("'")) {
      return "'" + value + "'";
    }
    return "concat('" + value.replace("'", "',\"'\",'") + "')";
  }
}
