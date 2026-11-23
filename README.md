# Daily Basket

An Indian grocery storefront built with React, Vite, TypeScript, Tailwind CSS, and Supabase. Browse fresh produce, dairy, pantry staples, and snacks; search and filter the catalog; check the cart total and delivery fee; and enter a delivery PIN code.

## Run locally

1. Install Node.js 20 or newer.
2. Run `npm install`.
3. Run `npm run dev` and open the local URL printed by Vite.
4. Run `npm run build` to type-check and create a production build.
5. With a local test instance running and no production Supabase credentials configured, run `mvn test` to execute the Selenium/TestNG suite (Java 17+, Maven, and Chrome required). WebDriverManager resolves the matching ChromeDriver. Override the local URL with `mvn test -Dstorefront.url=http://localhost:5173` if needed.

The storefront works with sample grocery products by default. Prices are in Indian rupees and are shown per product unit (such as 1 kg, 1 L, or 1 dozen). Delivery is ₹40 below a ₹499 subtotal and free at or above ₹499. To load active products from Supabase, copy `.env.example` to `.env.local`, set `VITE_SUPABASE_URL` and `VITE_SUPABASE_ANON_KEY`, then run `supabase/schema.sql` in your Supabase project. It creates or updates the catalog schema, deactivates the earlier sample software products, and seeds sample groceries. If the connection is unavailable, the page displays the local sample catalog and reports the database error.

Email/password sign-in, account creation, and password reset use Supabase Auth. Configure email authentication and the allowed redirect URLs in your Supabase project in addition to the environment variables above. The sign-in page remains available without Supabase, but authentication actions report that the service is not configured. The PIN code field saves a six-digit Indian PIN code for the demo but does not check delivery coverage. Checkout is a UI handoff only; inventory, address collection, order processing, live delivery booking, and payment methods (UPI, cards, and cash on delivery) are not configured. Connect these services before accepting grocery orders.

## Browser automation

The Selenium suite uses the Page Object Model under `src/test/java/com/dailybasket`, with TestNG results written by Maven Surefire under `target/surefire-reports`. Tests use only the local sample catalog and browser storage; they do not write to Supabase. Do not configure production Supabase credentials when running UI tests. Supabase integration testing requires a separate disposable test project and has intentionally not been run as part of this suite.

See [TEST-REPORT.md](./TEST-REPORT.md) for the application audit, test-case inventory, execution limitations, findings, and recommendations.
# Daily_Basket
