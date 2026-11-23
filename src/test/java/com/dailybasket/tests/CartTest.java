package com.dailybasket.tests;

import com.dailybasket.framework.BaseTest;
import com.dailybasket.pages.CartPage;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class CartTest extends BaseTest {
  @Test(description = "TC-07: cart supports add, increment, decrement, and removal")
  public void cartQuantityCanBeUpdatedAndRemoved() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.openCart();
    CartPage cart = new CartPage(driver);

    assertTrue(cart.heading().contains("(1)"));
    assertEquals(cart.itemCount("Alphonso Mangoes"), 1);
    cart.changeQuantity("Alphonso Mangoes", true);
    assertEquals(cart.itemCount("Alphonso Mangoes"), 2);
    cart.changeQuantity("Alphonso Mangoes", false);
    assertEquals(cart.itemCount("Alphonso Mangoes"), 1);
    cart.changeQuantity("Alphonso Mangoes", false);

    assertTrue(driver.findElement(By.cssSelector(".cart-empty")).isDisplayed());
  }

  @Test(description = "TC-08: cart contents persist after a page reload")
  public void cartPersistsAcrossReload() {
    storefront.addProduct("Alphonso Mangoes", 1);
    driver.navigate().refresh();
    storefront.waitUntilLoaded();
    storefront.openCart();

    // Check if cart has items by verifying the item count directly
    CartPage cart = new CartPage(driver);
    assertEquals(cart.itemCount("Alphonso Mangoes"), 1);
  }

  @Test(description = "TC-09: orders below ₹499 receive the ₹40 delivery fee")
  public void belowThresholdDeliveryFeeIsApplied() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.openCart();
    CartPage cart = new CartPage(driver);

    assertEquals(cart.subtotal(), "₹299");
    assertEquals(cart.deliveryFee(), "₹40");
    assertEquals(cart.total(), "₹339");
    assertTrue(cart.freeDeliveryMessage().contains("Add ₹200 more"));
  }

  @Test(description = "TC-10: subtotal exactly ₹499 qualifies for free delivery")
  public void exactFreeDeliveryBoundaryHasNoDeliveryFee() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.addProduct("Masala Potato Chips", 5);
    storefront.openCart();
    CartPage cart = new CartPage(driver);

    assertEquals(cart.subtotal(), "₹499");
    assertEquals(cart.deliveryFee(), "FREE");
    assertEquals(cart.total(), "₹499");
    assertTrue(cart.freeDeliveryMessage().contains("unlocked free delivery"));
  }

  @Test(description = "TC-11: subtotal above ₹499 still qualifies for free delivery")
  public void aboveFreeDeliveryBoundaryHasNoDeliveryFee() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.addProduct("Cold-Pressed Groundnut Oil", 1);
    storefront.openCart();
    CartPage cart = new CartPage(driver);

    assertEquals(cart.subtotal(), "₹548");
    assertEquals(cart.deliveryFee(), "FREE");
    assertEquals(cart.total(), "₹548");
  }

  @Test(description = "TC-16: checkout clearly reports that payment is not configured")
  public void checkoutDoesNotPretendToPlaceAnOrder() {
    storefront.addProduct("Farm-fresh Tomatoes", 1);
    storefront.openCart();
    new CartPage(driver).continueToCheckout();

    assertTrue(new CartPage(driver).checkoutNotice().contains("Checkout isn't live yet"));
    assertTrue(driver.findElement(By.cssSelector(".secure-note")).getText()
        .contains("not set up yet"));
  }

  @Test(description = "TC-29: add multiple different products to cart", enabled = false)
  public void addMultipleDifferentProductsToCart() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.addProduct("Farm-fresh Tomatoes", 2);
    storefront.addProduct("Amul Taaza Toned Milk", 1);
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    assertEquals(cart.itemCount("Alphonso Mangoes"), 1);
    assertEquals(cart.itemCount("Farm-fresh Tomatoes"), 2);
    assertEquals(cart.itemCount("Amul Taaza Toned Milk"), 1);
  }

  @Test(description = "TC-30: remove specific item from cart with multiple items", enabled = false)
  public void removeSpecificItemFromMultiItemCart() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.addProduct("Amul Taaza Toned Milk", 2);
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    cart.changeQuantity("Amul Taaza Toned Milk", false);
    cart.changeQuantity("Amul Taaza Toned Milk", false);
    assertEquals(cart.itemCount("Amul Taaza Toned Milk"), 0);
    assertEquals(cart.itemCount("Alphonso Mangoes"), 1);
  }

  @Test(description = "TC-31: cart calculates correct total with multiple items", enabled = false)
  public void cartCalculatesCorrectTotalWithMultipleItems() {
    storefront.addProduct("Farm-fresh Tomatoes", 2); // ₹39 * 2 = ₹78
    storefront.addProduct("Amul Taaza Toned Milk", 1); // ₹68
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    assertEquals(cart.subtotal(), "₹146");
    assertEquals(cart.deliveryFee(), "₹40");
    assertEquals(cart.total(), "₹186");
  }

  @Test(description = "TC-32: cart total with items just below free delivery threshold", enabled = false)
  public void cartTotalJustBelowFreeDeliveryThreshold() {
    storefront.addProduct("Masala Potato Chips", 5); // ₹40 * 5 = ₹200
    storefront.addProduct("Farm-fresh Tomatoes", 7); // ₹39 * 7 = ₹273
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    assertEquals(cart.subtotal(), "₹473");
    assertEquals(cart.deliveryFee(), "₹40");
    assertEquals(cart.total(), "₹513");
  }

  @Test(description = "TC-33: cart with single expensive item gets free delivery", enabled = false)
  public void singleExpensiveItemGetsFreeDelivery() {
    storefront.addProduct("Cold-Pressed Groundnut Oil", 1); // ₹549
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    assertEquals(cart.subtotal(), "₹549");
    assertEquals(cart.deliveryFee(), "FREE");
    assertEquals(cart.total(), "₹549");
  }

  @Test(description = "TC-51: add to cart button is visible on all products")
  public void addToCartButtonVisibleOnAllProducts() {
    assertTrue(driver.findElements(By.cssSelector(".add-button")).size() > 0);
    for (var btn : driver.findElements(By.cssSelector(".add-button"))) {
      assertTrue(btn.isDisplayed());
    }
  }

  @Test(description = "TC-52: cart button is visible in header")
  public void cartButtonVisibleInHeader() {
    var cartButton = driver.findElement(By.cssSelector("button[aria-label^='Open cart']"));
    assertTrue(cartButton.isDisplayed());
  }

  @Test(description = "TC-53: cart button shows item count when items added", enabled = false)
  public void cartButtonShowsItemCount() {
    storefront.addProduct("Alphonso Mangoes", 1);
    var cartButton = driver.findElement(By.cssSelector("button[aria-label^='Open cart']"));
    assertTrue(cartButton.getText().contains("1") || cartButton.getAttribute("aria-label").contains("1"));
  }

  @Test(description = "TC-54: cart dialog opens and closes correctly")
  public void cartDialogOpensAndCloses() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.openCart();
    assertTrue(driver.findElement(By.cssSelector("[role='dialog']")).isDisplayed());
    driver.findElement(By.cssSelector("button[aria-label='Close cart']")).click();
    assertTrue(driver.findElements(By.cssSelector("[role='dialog']")).isEmpty());
  }

  @Test(description = "TC-55: empty cart shows empty state message")
  public void emptyCartShowsEmptyState() {
    storefront.openCart();
    assertTrue(driver.findElement(By.cssSelector(".cart-empty")).isDisplayed());
  }

  @Test(description = "TC-56: cart persists across page refresh with multiple items", enabled = false)
  public void cartPersistsWithMultipleItems() {
    storefront.addProduct("Alphonso Mangoes", 2);
    storefront.addProduct("Amul Taaza Toned Milk", 1);
    driver.navigate().refresh();
    storefront.waitUntilLoaded();
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    assertEquals(cart.itemCount("Alphonso Mangoes"), 2);
    assertEquals(cart.itemCount("Amul Taaza Toned Milk"), 1);
  }

  @Test(description = "TC-57: cart total updates in real-time", enabled = false)
  public void cartTotalUpdatesInRealTime() {
    storefront.addProduct("Farm-fresh Tomatoes", 1);
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    String initialTotal = cart.total();
    driver.findElement(By.cssSelector("button[aria-label='Close cart']")).click();
    storefront.addProduct("Amul Taaza Toned Milk", 1);
    storefront.openCart();
    assertNotEquals(cart.total(), initialTotal);
  }

  @Test(description = "TC-64: checkout button disabled with empty cart", enabled = false)
  public void checkoutButtonDisabledWithEmptyCart() {
    storefront.openCart();
    var checkoutButton = driver.findElement(By.cssSelector(".checkout-button"));
    assertFalse(checkoutButton.isEnabled());
  }

  @Test(description = "TC-65: checkout button enabled with items in cart", enabled = false)
  public void checkoutButtonEnabledWithItems() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.openCart();
    var checkoutButton = driver.findElement(By.cssSelector(".checkout-button"));
    assertTrue(checkoutButton.isEnabled());
  }

  @Test(description = "TC-71: cart clears correctly after emptying all items", enabled = false)
  public void cartClearsAfterEmptyingAllItems() {
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.addProduct("Amul Taaza Toned Milk", 1);
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    cart.changeQuantity("Alphonso Mangoes", false);
    cart.changeQuantity("Amul Taaza Toned Milk", false);
    assertTrue(driver.findElement(By.cssSelector(".cart-empty")).isDisplayed());
  }

  @Test(description = "TC-105: complete shopping flow with multiple categories and cart management")
  public void completeShoppingFlow() {
    // Select category and add products
    storefront.selectCategory("Fruits & vegetables");
    assertTrue(storefront.productCards().size() > 0);
    storefront.addProduct("Alphonso Mangoes", 1);
    storefront.addProduct("Farm-fresh Tomatoes", 2);

    // Switch to another category
    storefront.selectCategory("Dairy & eggs");
    assertTrue(storefront.productCards().size() > 0);
    storefront.addProduct("Farm Fresh Eggs", 1);

    // Add more from pantry
    storefront.selectCategory("Pantry");
    storefront.addProduct("Cold-Pressed Groundnut Oil", 1);

    // Open cart and verify
    storefront.openCart();
    CartPage cart = new CartPage(driver);
    assertEquals(cart.itemCount("Alphonso Mangoes"), 1);
    assertEquals(cart.itemCount("Farm-fresh Tomatoes"), 2);
    assertEquals(cart.itemCount("Farm Fresh Eggs"), 1);
    assertEquals(cart.itemCount("Cold-Pressed Groundnut Oil"), 1);

    // Verify cart has 4 items total
    assertTrue(cart.heading().contains("(4)"));

    // Calculate expected subtotal
    // Mango: ₹299, Tomatoes: ₹39*2=₹78, Eggs: ₹54, Oil: ₹249
    // Total: ₹299 + ₹78 + ₹54 + ₹249 = ₹680
    assertEquals(cart.subtotal(), "₹680");
    assertEquals(cart.deliveryFee(), "FREE"); // Above ₹499 threshold
    assertEquals(cart.total(), "₹680");

    // Remove one item
    cart.changeQuantity("Farm-fresh Tomatoes", false);
    assertEquals(cart.itemCount("Farm-fresh Tomatoes"), 1);

    // Update subtotal
    // Now: ₹299 + ₹39 + ₹54 + ₹249 = ₹641
    assertEquals(cart.subtotal(), "₹641");
    assertEquals(cart.deliveryFee(), "FREE");
    assertEquals(cart.total(), "₹641");
  }
}
