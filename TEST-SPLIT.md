# Test Suite Organization

The test suite has been split into 4 separate files for easier and faster execution. Each file focuses on a specific area of functionality.

## Test Files

### 1. BasicStorefrontTest.java
**Tests basic storefront functionality:**
- TC-01: Storefront loads with grocery products and India locale
- TC-02: Category filters to matching products
- TC-03: Search returns matching grocery case-insensitively
- TC-04: Unmatched search shows empty state and can be cleared
- TC-05: Low-to-high sorting orders grocery prices numerically
- TC-06: High-to-low sorting orders grocery prices numerically
- TC-22: Search with empty string shows all products
- TC-23: Search with spaces only shows all products
- TC-24: Search for partial product name returns matching products
- TC-25: Search is case insensitive for product categories
- TC-26: Clear search button removes search filter
- TC-27: Test all category filters work correctly (disabled)
- TC-28: Switching between categories works correctly (disabled)
- TC-58: Sort by price with same prices
- TC-59: Sort order persists across page reload (disabled)
- TC-60: Search within selected category
- TC-61: Category selection after search

**Run command:**
```bash
mvn test -Dtest=BasicStorefrontTest
```

### 2. CartTest.java
**Tests cart functionality and checkout:**
- TC-07: Cart supports add, increment, decrement, and removal
- TC-08: Cart contents persist after a page reload
- TC-09: Orders below ₹499 receive the ₹40 delivery fee
- TC-10: Subtotal exactly ₹499 qualifies for free delivery
- TC-11: Subtotal above ₹499 still qualifies for free delivery
- TC-16: Checkout clearly reports that payment is not configured
- TC-29: Add multiple different products to cart (disabled)
- TC-30: Remove specific item from cart with multiple items (disabled)
- TC-31: Cart calculates correct total with multiple items (disabled)
- TC-32: Cart total with items just below free delivery threshold (disabled)
- TC-33: Cart with single expensive item gets free delivery (disabled)
- TC-51: Add to cart button is visible on all products
- TC-52: Cart button is visible in header
- TC-53: Cart button shows item count when items added (disabled)
- TC-54: Cart dialog opens and closes correctly
- TC-55: Empty cart shows empty state message
- TC-56: Cart persists across page refresh with multiple items (disabled)
- TC-57: Cart total updates in real-time (disabled)
- TC-64: Checkout button disabled with empty cart (disabled)
- TC-65: Checkout button enabled with items in cart (disabled)
- TC-71: Cart clears correctly after emptying all items (disabled)
- TC-105: Complete shopping flow with multiple categories and cart management

**Run command:**
```bash
mvn test -Dtest=CartTest
```

### 3. UIInteractionTest.java
**Tests UI interactions and form elements:**
- TC-12: Valid six-digit Indian PIN code is accepted in the UI
- TC-13: Malformed and incomplete PIN codes fail browser validation
- TC-14: Empty PIN code submission shows a validation message
- TC-15: Valid newsletter input shows the UI confirmation
- TC-17: Favourite action provides its current confirmation
- TC-18: Internal navigation targets resolve to page sections
- TC-19: Mobile viewport does not introduce horizontal overflow
- TC-34: Test PIN code with different valid Indian PINs (disabled)
- TC-35: PIN code with 5 digits is invalid
- TC-36: PIN code with 7 digits is invalid (disabled)
- TC-37: PIN code with only zeros is invalid
- TC-38: PIN code input accepts only digits
- TC-39: Newsletter with invalid email format (disabled)
- TC-40: Newsletter with empty email (disabled)
- TC-41: Favorite button can be clicked multiple times
- TC-42: Different product favorite buttons work (disabled)
- TC-43: All internal navigation links work (disabled)
- TC-44: Navigation link scrolls to correct section
- TC-45: Mobile viewport at 320px width
- TC-46: Tablet viewport at 768px width
- TC-47: Desktop viewport at 1440px width
- TC-48: Large desktop viewport at 1920px width
- TC-62: PIN code persists across page reload (disabled)
- TC-63: Update saved PIN code (disabled)

**Run command:**
```bash
mvn test -Dtest=UIInteractionTest
```

### 4. VisualAndPerformanceTest.java
**Tests visual elements and performance:**
- TC-20: Browser has no uncaught application JavaScript errors
- TC-21: Malformed persisted cart data does not break the catalog (disabled)
- TC-49: Product cards are visible in viewport
- TC-50: Product images load successfully
- TC-66: All product cards have required elements
- TC-67: Product prices are displayed in INR
- TC-68: Page title is correct (disabled)
- TC-69: Page language attribute is set
- TC-70: No console errors after multiple interactions (disabled)

**Run command:**
```bash
mvn test -Dtest=VisualAndPerformanceTest
```

## Running All Tests

To run all tests together (same as before):
```bash
mvn test
```

## Running Specific Test

To run a specific test case (e.g., TC-01):
```bash
mvn test -Dtest=BasicStorefrontTest#storefrontLoadsCatalogInIndianLocale
```

## Quick Test Runs

For quick feedback during development, run the BasicStorefrontTest (17 tests):
```bash
mvn test -Dtest=BasicStorefrontTest
```

## Prerequisites

Make sure the development server is running before running tests:
```bash
npm run dev
```

The server should be running on http://localhost:5173 (or the port shown in the terminal).

## Notes

- Tests marked as `enabled = false` are disabled due to known issues or pending features
- The original `GroceryStorefrontTest.java` file is kept for reference but all tests have been moved to the new files
- Each test file extends `BaseTest` which handles browser setup and teardown
