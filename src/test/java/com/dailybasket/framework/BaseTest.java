package com.dailybasket.framework;

import com.dailybasket.pages.StorefrontPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.logging.Level;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {
  protected WebDriver driver;
  protected StorefrontPage storefront;

  @BeforeMethod
  public void startBrowser() {
    WebDriverManager.chromedriver().setup();

    LoggingPreferences logging = new LoggingPreferences();
    logging.enable(LogType.BROWSER, Level.ALL);

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless=new", "--window-size=1920,2000", "--disable-dev-shm-usage");
    options.setCapability("goog:loggingPrefs", logging);

    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

    String baseUrl = System.getProperty("storefront.url", "http://localhost:5173");
    driver.get(baseUrl);
    storefront = new StorefrontPage(driver);
    storefront.waitUntilLoaded();
  }

  @AfterMethod(alwaysRun = true)
  public void stopBrowser() {
    if (driver != null) {
      driver.quit();
      driver = null;
    }
  }
}
