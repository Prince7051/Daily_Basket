package com.dailybasket.tests;

import com.dailybasket.framework.BaseTest;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BasicStorefrontTest extends BaseTest {
  @Test(description = "TC-01: storefront loads with grocery products and India locale")
  public void storefrontLoadsCatalogInIndianLocale() {
    assertTrue(storefront.title().contains("Daily Basket"));
    assertEquals(storefront.pageLanguage(), "en-IN");
    assertEquals(storefront.productCards().size(), 30);
    assertTrue(driver.findElement(org.openqa.selenium.By.cssSelector(".announcement")).getText().contains("₹499"));
  }

  @Test(description = "TC-02: each grocery category filters to matching products")
  public void categoryFilterShowsOnlyMatchingGroceries() {
    storefront.selectCategory("Dairy & eggs");

    assertTrue(storefront.productCards().size() > 0);
    assertTrue(storefront.productNames().stream()
        .allMatch(name -> name.contains("Milk") || name.contains("Eggs") || name.contains("Butter") || name.contains("Cheese") || name.contains("Curd") || name.contains("Paneer")));
    assertEquals(driver.findElement(org.openqa.selenium.By.xpath(
        "//button[@role='tab' and normalize-space()='Dairy & eggs']")).getDomAttribute("aria-selected"),
        "true");
  }

  @Test(description = "TC-03: search returns a matching grocery case-insensitively")
  public void searchFindsMatchingGrocery() {
    storefront.searchFor("mAnGo");

    assertEquals(storefront.productCards().size(), 1);
    assertEquals(storefront.productNames().get(0), "Alphonso Mangoes");
  }

  @Test(description = "TC-04: unmatched search shows an empty state and can be cleared")
  public void unmatchedSearchShowsEmptyState() {
    storefront.searchFor("not-a-real-grocery-!?");

    assertTrue(driver.findElement(org.openqa.selenium.By.cssSelector(".empty-results")).isDisplayed());
    assertTrue(driver.findElement(org.openqa.selenium.By.cssSelector(".empty-results")).getText()
        .contains("No groceries found"));

    driver.findElement(org.openqa.selenium.By.cssSelector(".empty-results .text-link")).click();
    assertEquals(storefront.productCards().size(), 30);
  }

  @Test(description = "TC-05: low-to-high sorting orders grocery prices numerically")
  public void sortOrdersPricesAscending() {
    storefront.sortBy("price-low");
    List<Integer> prices = visiblePrices();

    List<Integer> sorted = new ArrayList<>(prices);
    sorted.sort(Integer::compareTo);
    assertEquals(prices, sorted);
  }

  @Test(description = "TC-06: high-to-low sorting orders grocery prices numerically")
  public void sortOrdersPricesDescending() {
    storefront.sortBy("price-high");
    List<Integer> prices = visiblePrices();

    List<Integer> sorted = new ArrayList<>(prices);
    sorted.sort((first, second) -> Integer.compare(second, first));
    assertEquals(prices, sorted);
  }

  @Test(description = "TC-22: search with empty string shows all products")
  public void emptySearchShowsAllProducts() {
    storefront.searchFor("");
    assertEquals(storefront.productCards().size(), 30);
  }

  @Test(description = "TC-23: search with spaces only shows all products")
  public void spaceOnlySearchShowsAllProducts() {
    storefront.searchFor("   ");
    assertEquals(storefront.productCards().size(), 30);
  }

  @Test(description = "TC-30: search for partial product name returns matching products")
  public void partialProductNameSearchWorks() {
    storefront.searchFor("milk");
    assertTrue(storefront.productNames().stream().anyMatch(name -> name.toLowerCase().contains("milk")));
  }

  @Test(description = "TC-25: search is case insensitive for product categories")
  public void searchIsCaseInsensitive() {
    storefront.searchFor("TOMATO");
    assertTrue(storefront.productNames().stream().anyMatch(name -> name.toLowerCase().contains("tomato")));
  }

  @Test(description = "TC-26: clear search button removes search filter")
  public void clearSearchButtonRemovesFilter() {
    storefront.searchFor("mango");
    assertEquals(storefront.productCards().size(), 1);
    storefront.clearSearch();
    assertEquals(storefront.productCards().size(), 30);
  }

  @Test(description = "TC-27: test all category filters work correctly", enabled = false)
  public void allCategoryFiltersWork() {
    String[] categories = {"Dairy & eggs", "Rice, atta & dals", "Snacks", "Pantry"};
    for (String category : categories) {
      storefront.selectCategory(category);
      assertTrue(storefront.productCards().size() > 0, "Category " + category + " should have products");
    }
  }

  @Test(description = "TC-28: switching between categories works correctly", enabled = false)
  public void switchingCategoriesWorks() {
    storefront.selectCategory("Dairy & eggs");
    int dairyCount = storefront.productCards().size();
    storefront.selectCategory("Rice, atta & dals");
    int riceCount = storefront.productCards().size();
    storefront.selectCategory("Dairy & eggs");
    assertEquals(storefront.productCards().size(), dairyCount);
    assertTrue(dairyCount != riceCount);
  }

  @Test(description = "TC-58: sort by price with same prices")
  public void sortWithSamePriceProducts() {
    storefront.sortBy("price-low");
    List<Integer> prices = visiblePrices();
    // Verify prices are non-decreasing
    for (int i = 1; i < prices.size(); i++) {
      assertTrue(prices.get(i) >= prices.get(i - 1));
    }
  }

  @Test(description = "TC-59: sort order persists across page reload", enabled = false)
  public void sortOrderPersistsAcrossReload() {
    storefront.sortBy("price-high");
    List<Integer> pricesBefore = visiblePrices();
    driver.navigate().refresh();
    storefront.waitUntilLoaded();
    storefront.sortBy("price-high");
    List<Integer> pricesAfter = visiblePrices();
    assertEquals(pricesBefore, pricesAfter);
  }

  @Test(description = "TC-60: search within selected category")
  public void searchWithinSelectedCategory() {
    storefront.selectCategory("Dairy & eggs");
    storefront.searchFor("milk");
    assertEquals(storefront.productCards().size(), 1);
    assertTrue(storefront.productNames().get(0).toLowerCase().contains("milk"));
  }

  @Test(description = "TC-61: category selection after search")
  public void categorySelectionAfterSearch() {
    storefront.searchFor("mango");
    storefront.selectCategory("Dairy & eggs");
    assertTrue(storefront.productCards().size() > 0);
    assertTrue(storefront.productNames().stream().allMatch(name -> name.contains("Milk") || name.contains("Eggs") || name.contains("Butter") || name.contains("Cheese") || name.contains("Curd") || name.contains("Paneer")));
  }

  private List<Integer> visiblePrices() {
    return driver.findElements(org.openqa.selenium.By.cssSelector(".product-price")).stream()
        .map(element -> Integer.parseInt(
            element.getText().split("/", 2)[0].replaceAll("\\D", "")))
        .toList();
  }
}
