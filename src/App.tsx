import { useEffect, useMemo, useState } from "react";
import {
  ArrowDownUp,
  ArrowLeft,
  ArrowRight,
  ArrowUpRight,
  Check,
  ChevronDown,
  Heart,
  Leaf,
  LockKeyhole,
  LogOut,
  Mail,
  MapPin,
  Menu,
  Minus,
  Plus,
  Search,
  ShieldCheck,
  ShoppingBag,
  ShoppingBasket,
  Star,
  Truck,
  UserRound,
  X,
} from "lucide-react";
import { categories, featuredProducts, type Product } from "./data/products";
import { supabase } from "./lib/supabase";

type Cart = Record<string, number>;

const money = (amount: number) =>
  new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(amount);

function AuthPage({ onBack, onAuthenticated }: { onBack: () => void; onAuthenticated: (email: string) => void }) {
  const [mode, setMode] = useState<"login" | "signup">("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setBusy(true);
    setErrorMessage("");
    setSuccessMessage("");

    try {
      if (!supabase) {
        // Demo mode: use localStorage for authentication when Supabase is not configured
        const demoUsers = JSON.parse(localStorage.getItem("daily-basket-users") || "{}");
        
        if (mode === "login") {
          if (demoUsers[email] && demoUsers[email] === password) {
            localStorage.setItem("daily-basket-current-user", email);
            onAuthenticated(email);
          } else {
            setErrorMessage("Invalid email or password. In demo mode, you need to create an account first.");
          }
        } else {
          // Sign up
          if (demoUsers[email]) {
            setErrorMessage("An account with this email already exists in demo mode.");
          } else {
            demoUsers[email] = password;
            localStorage.setItem("daily-basket-users", JSON.stringify(demoUsers));
            localStorage.setItem("daily-basket-current-user", email);
            onAuthenticated(email);
          }
        }
        return;
      }

      if (mode === "login") {
        const { error } = await supabase.auth.signInWithPassword({ email, password });
        if (error) {
          setErrorMessage(error.message);
          return;
        }
        onAuthenticated(email);
      } else {
        const { data, error } = await supabase.auth.signUp({ email, password });
        if (error) {
          setErrorMessage(error.message);
          return;
        }
        if (data.session) {
          onAuthenticated(email);
        } else {
          setSuccessMessage("Account created. Check your email to confirm your account, then sign in.");
        }
      }
    } catch (error: unknown) {
      setErrorMessage(error instanceof Error ? error.message : String(error));
    } finally {
      setBusy(false);
    }
  }

  async function resetPassword() {
    setErrorMessage("");
    setSuccessMessage("");
    if (!email.trim()) {
      setErrorMessage("Enter your email address first so we can send a reset link.");
      return;
    }
    if (!supabase) {
      setErrorMessage("Password reset is not available in demo mode. Authentication is not configured. Add your Supabase URL and anon key to enable password reset.");
      return;
    }

    setBusy(true);
    try {
      const { error } = await supabase.auth.resetPasswordForEmail(email, {
        redirectTo: window.location.origin,
      });
      if (error) setErrorMessage(error.message);
      else setSuccessMessage("If an account exists for that email, a password reset link is on its way.");
    } catch (error: unknown) {
      setErrorMessage(error instanceof Error ? error.message : String(error));
    } finally {
      setBusy(false);
    }
  }

  function changeMode(nextMode: "login" | "signup") {
    setMode(nextMode);
    setErrorMessage("");
    setSuccessMessage("");
  }

  return (
    <main className="auth-page">
      <a className="brand auth-brand" href="#" onClick={(event) => { event.preventDefault(); onBack(); }}>
        <span className="brand-mark basket-mark"><ShoppingBasket size={24} strokeWidth={2} /></span>
        <span>daily basket</span>
      </a>
      <section className="auth-layout">
        <div className="auth-story">
          <span className="eyebrow"><span className="eyebrow-dot" /> GOOD FOOD, RIGHT AT HOME</span>
          <h1>Your daily picks,<br /><span>all in one place.</span></h1>
          <p>Sign in to make your next grocery shop a little simpler.</p>
          <div className="auth-story-card"><span><Leaf size={18} /></span><div><strong>Freshness, delivered.</strong><small>Your neighbourhood favourites are waiting.</small></div><Check size={17} /></div>
        </div>
        <div className="auth-card">
          <button className="auth-back" onClick={onBack}><ArrowLeft size={15} /> Back to shopping</button>
          <span className="eyebrow section-eyebrow">{mode === "login" ? "WELCOME BACK" : "JOIN DAILY BASKET"}</span>
          <h2>{mode === "login" ? "Sign in to your account" : "Create your account"}</h2>
          <p className="auth-description">{mode === "login" ? "Your fresh favourites are just a few clicks away." : "Create an account to make shopping easier next time."}</p>
          <form className="auth-form" onSubmit={submit}>
            <label htmlFor="auth-email">Email address</label>
            <div className="auth-input"><Mail size={17} /><input id="auth-email" type="email" autoComplete="email" placeholder="you@example.com" value={email} onChange={(event) => setEmail(event.target.value)} required /></div>
            <label htmlFor="auth-password">Password</label>
            <div className="auth-input"><LockKeyhole size={17} /><input id="auth-password" type="password" autoComplete={mode === "login" ? "current-password" : "new-password"} placeholder="At least 6 characters" value={password} onChange={(event) => setPassword(event.target.value)} minLength={6} required /></div>
            {mode === "login" && <button className="auth-forgot" type="button" onClick={resetPassword} disabled={busy}>Forgot password?</button>}
            {errorMessage && <p className="auth-message auth-error" role="alert">{errorMessage}</p>}
            {successMessage && <p className="auth-message auth-success" role="status">{successMessage}</p>}
            <button className="button button-dark auth-submit" type="submit" disabled={busy}>
              {busy ? "Please wait..." : mode === "login" ? "Sign in" : "Create account"} {!busy && <ArrowRight size={16} />}
            </button>
          </form>
          <p className="auth-switch">
            {mode === "login" ? "New to Daily Basket?" : "Already have an account?"}{" "}
            <button type="button" onClick={() => changeMode(mode === "login" ? "signup" : "login")}>
              {mode === "login" ? "Create an account" : "Sign in"}
            </button>
          </p>
          <div className="auth-secure"><ShieldCheck size={14} /> {supabase ? "Your account is securely managed by Supabase." : "Demo mode: Account stored locally in your browser."}</div>
        </div>
      </section>
    </main>
  );
}

function readCart(): Cart {
  try {
    const saved = localStorage.getItem("daily-basket-cart");
    return saved ? (JSON.parse(saved) as Cart) : {};
  } catch {
    return {};
  }
}

function App() {
  const [products, setProducts] = useState<Product[]>(featuredProducts);
  const [cart, setCart] = useState<Cart>(readCart);
  const [activeCategory, setActiveCategory] = useState("All groceries");
  const [search, setSearch] = useState("");
  const [sortOrder, setSortOrder] = useState<"featured" | "price-low" | "price-high">("featured");
  const [cartOpen, setCartOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [notice, setNotice] = useState("");
  const [databaseMessage, setDatabaseMessage] = useState("");
  const [pincode, setPincode] = useState("");
  const [pincodeInput, setPincodeInput] = useState("");
  const [authVisible, setAuthVisible] = useState(false);
  const [userEmail, setUserEmail] = useState<string | null>(null);

  useEffect(() => {
    if (!supabase) {
      // Demo mode: check localStorage for current user
      const currentUser = localStorage.getItem("daily-basket-current-user");
      setUserEmail(currentUser);
      return;
    }
    let active = true;
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setUserEmail(session?.user.email ?? null);
    });

    supabase.auth.getSession().then(({ data, error }) => {
      if (!active) return;
      if (error) {
        setNotice(`Could not check your sign-in status: ${error.message}`);
        return;
      }
      setUserEmail(data.session?.user.email ?? null);
    }).catch((error: unknown) => {
      if (!active) return;
      const message = error instanceof Error ? error.message : String(error);
      setNotice(`Could not check your sign-in status: ${message}`);
    });

    return () => {
      active = false;
      subscription.unsubscribe();
    };
  }, []);

  useEffect(() => {
    if (!supabase) return;
    let active = true;

    Promise.resolve(
      supabase
        .from("products")
        .select("id,name,maker,category,description,price,unit,rating,reviews,image_url,badge,accent")
        .eq("active", true)
        .order("featured_order", { ascending: true }),
    )
      .then(({ data, error }) => {
        if (!active) return;
        if (error) {
          setDatabaseMessage(`Showing sample groceries. Supabase could not load products: ${error.message}`);
          return;
        }
        if (data?.length) setProducts(data as Product[]);
      })
      .catch((error: unknown) => {
        if (!active) return;
        const message = error instanceof Error ? error.message : String(error);
        setDatabaseMessage(`Showing sample groceries. Supabase could not load products: ${message}`);
      });

    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    localStorage.setItem("daily-basket-cart", JSON.stringify(cart));
  }, [cart]);

  useEffect(() => {
    if (!notice) return;
    const timeout = window.setTimeout(() => setNotice(""), 2600);
    return () => window.clearTimeout(timeout);
  }, [notice]);

  const filteredProducts = useMemo(() => {
    const query = search.trim().toLowerCase();
    const matches = products.filter((product) => {
      const matchesCategory = activeCategory === "All groceries" || product.category === activeCategory;
      const matchesSearch =
        !query ||
        [product.name, product.maker, product.category, product.description]
          .some((value) => value.toLowerCase().includes(query));
      return matchesCategory && matchesSearch;
    });

    if (sortOrder === "price-low") return [...matches].sort((a, b) => a.price - b.price);
    if (sortOrder === "price-high") return [...matches].sort((a, b) => b.price - a.price);
    return matches;
  }, [activeCategory, products, search, sortOrder, activeCategory, search]);

  const cartItems = products.filter((product) => cart[product.id]);
  const cartCount = Object.values(cart).reduce((total, quantity) => total + quantity, 0);
  const subtotal = cartItems.reduce((total, product) => total + product.price * cart[product.id], 0);
  const deliveryFee = subtotal === 0 || subtotal >= 499 ? 0 : 40;
  const freeDeliveryProgress = Math.max(0, 499 - subtotal);

  function addToCart(product: Product) {
    setCart((current) => ({ ...current, [product.id]: (current[product.id] ?? 0) + 1 }));
    setNotice(`${product.name} added to your cart`);
  }

  function savePincode(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!/^[1-9]\d{5}$/.test(pincodeInput)) {
      setNotice("Enter a valid 6-digit Indian PIN code.");
      return;
    }
    setPincode(pincodeInput);
    setNotice("PIN code saved for this demo. Live delivery coverage is not connected yet.");
  }

  function changeQuantity(productId: string, change: number) {
    setCart((current) => {
      const quantity = (current[productId] ?? 0) + change;
      if (quantity <= 0) {
        const next = { ...current };
        delete next[productId];
        return next;
      }
      return { ...current, [productId]: quantity };
    });
  }

  async function signOut() {
    if (!supabase) {
      // Demo mode: clear the current user from localStorage
      localStorage.removeItem("daily-basket-current-user");
      setUserEmail(null);
      setNotice("You have been signed out.");
      return;
    }
    try {
      const { error } = await supabase.auth.signOut();
      if (error) setNotice(`Could not sign out: ${error.message}`);
      else setNotice("You have been signed out.");
    } catch (error: unknown) {
      setNotice(`Could not sign out: ${error instanceof Error ? error.message : String(error)}`);
    }
  }

  if (authVisible) {
    return <AuthPage onBack={() => setAuthVisible(false)} onAuthenticated={(email) => { setUserEmail(email); setAuthVisible(false); }} />;
  }

  return (
    <div className="storefront">
      <div className="announcement">
        <Leaf size={14} strokeWidth={1.8} />
        <span>Fresh groceries for your everyday — free delivery over ₹499</span>
        <a href="#products">Shop now <ArrowRight size={13} /></a>
      </div>

      <header className="site-header">
        <a href="#" className="brand" aria-label="Daily Basket home">
          <span className="brand-mark basket-mark"><ShoppingBasket size={24} strokeWidth={2} /></span>
          <span>daily basket</span>
        </a>
        <nav className={`main-nav ${mobileMenuOpen ? "is-open" : ""}`} aria-label="Main navigation">
          <a className="nav-active" href="#products" onClick={() => setMobileMenuOpen(false)}>Shop</a>
          <a href="#categories" onClick={() => setMobileMenuOpen(false)}>Categories</a>
          <a href="#why-softly" onClick={() => setMobileMenuOpen(false)}>Our promise</a>
        </nav>
        <form className="delivery-check" onSubmit={savePincode}>
          <MapPin size={16} />
          <label htmlFor="delivery-pincode">
            <span>Deliver to</span>
            <input id="delivery-pincode" inputMode="numeric" pattern="[1-9][0-9]{5}" maxLength={6} placeholder="Enter PIN code" value={pincodeInput} onChange={(event) => setPincodeInput(event.target.value.replace(/\D/g, "").slice(0, 6))} />
          </label>
          <button type="submit" aria-label="Save delivery PIN code"><ArrowRight size={14} /></button>
          {pincode && <span className="pincode-saved">{pincode}</span>}
        </form>
        <div className="header-actions">
          <button className="icon-button mobile-search" aria-label="Search groceries" onClick={() => document.getElementById("product-search")?.focus()}><Search size={19} /></button>
          {userEmail
            ? <button className="account-button" onClick={signOut} title={`Signed in as ${userEmail}`} aria-label={`Sign out ${userEmail}`}><LogOut size={16} /><span>Sign out</span></button>
            : <button className="account-button" onClick={() => setAuthVisible(true)} aria-label="Sign in"><UserRound size={16} /><span>Sign in</span></button>}
          <button className="bag-button" onClick={() => setCartOpen(true)} aria-label={`Open cart, ${cartCount} items`}>
            <ShoppingBag size={18} strokeWidth={1.8} /><span>Cart</span><span className="bag-count">{cartCount}</span>
          </button>
          <button className="icon-button menu-button" aria-label="Toggle navigation" onClick={() => setMobileMenuOpen((open) => !open)}>
            {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </header>

      <main>
        <section className="hero">
          <div className="hero-copy">
            <div className="eyebrow"><span className="eyebrow-dot" /> YOUR NEIGHBOURHOOD GROCERY, ONLINE</div>
            <h1>Fresh picks.<br /><span>Happy meals</span> every day.</h1>
            <p className="hero-description">Farm-fresh fruits, everyday pantry staples, and all your favourites — delivered to your doorstep.</p>
            <div className="hero-actions">
              <a className="button button-dark" href="#products">Fill your basket <ArrowRight size={16} /></a>
              <div className="hero-proof"><div className="avatar-stack"><span>J</span><span>M</span><span>A</span><span>S</span></div><span>Freshness loved by <strong>2,400+</strong> homes</span></div>
            </div>
            <div className="hero-note"><Truck size={16} /> Free delivery on orders over ₹499</div>
          </div>
          <div className="hero-art grocery-art" aria-label="Illustration of fresh grocery picks">
            <div className="orbit orbit-one" /><div className="orbit orbit-two" />
            <div className="hero-sun" />
            <div className="hero-shape shape-lilac" />
            <div className="hero-shape shape-peach" />
            <div className="hero-shape shape-green" />
            <div className="hero-center-card">
              <span className="center-spark"><Leaf size={19} /></span>
              <span className="center-label">FRESH PICKS<br />EVERY DAY</span>
              <span className="center-sub">good food, right to your door.</span>
            </div>
            <div className="float-card float-top"><span className="float-icon float-purple">✳</span><span><b>Farm fresh</b><small>Picked with care every day</small></span><Check size={16} className="float-check" /></div>
            <div className="float-card float-bottom"><span className="float-icon float-orange">✦</span><span><b>Good food, fair prices</b><small>Your local favourites</small></span><Heart size={16} className="float-heart" /></div>
            <span className="art-spark art-spark-one">✳</span><span className="art-spark art-spark-two">✦</span>
          </div>
        </section>

        <section className="trust-row" aria-label="Grocery delivery benefits">
          <div><span className="trust-icon trust-mint"><Leaf size={19} /></span><span><strong>Freshness first</strong><small>Carefully picked produce</small></span></div>
          <div><span className="trust-icon trust-pink"><Truck size={19} /></span><span><strong>Free delivery over ₹499</strong><small>₹40 delivery below ₹499</small></span></div>
          <div><span className="trust-icon trust-lilac"><ShieldCheck size={19} /></span><span><strong>Secure, simple shopping</strong><small>Clear prices, no surprises</small></span></div>
        </section>

        <section className="products-section" id="products">
          <div className="section-heading">
            <div><span className="eyebrow section-eyebrow">FRESH FROM OUR SHELVES</span><h2>Good food, <span>good mood</span></h2><p>Everyday groceries and little treats for your home.</p></div>
            <a href="#products" className="text-link">Shop all groceries <ArrowUpRight size={16} /></a>
          </div>

          <div className="product-controls" id="categories">
            <div className="category-list" role="tablist" aria-label="Filter by grocery category">
              {categories.map((category) => (
                <button
                  className={`category-pill ${activeCategory === category ? "selected" : ""}`}
                  key={category}
                  role="tab"
                  aria-selected={activeCategory === category}
                  onClick={() => { setActiveCategory(category); setSearch(""); }}
                >{category}</button>
              ))}
            </div>
            <div className="product-tools">
              <label className="search-field">
                <Search size={16} />
                <input id="product-search" aria-label="Search groceries" placeholder="Search groceries..." value={search} onChange={(event) => setSearch(event.target.value)} />
                {search && <button aria-label="Clear search" onClick={() => setSearch("")}><X size={14} /></button>}
              </label>
              <label className="sort-field"><ArrowDownUp size={15} /><select aria-label="Sort groceries" value={sortOrder} onChange={(event) => setSortOrder(event.target.value as typeof sortOrder)}><option value="featured">Featured</option><option value="price-low">Price: low to high</option><option value="price-high">Price: high to low</option></select><ChevronDown size={14} /></label>
            </div>
          </div>

          {databaseMessage && <p className="database-message" role="status">{databaseMessage}</p>}

          {filteredProducts.length ? (
            <div className="product-grid">
              {filteredProducts.map((product) => (
                <article className="product-card" key={product.id}>
                  <div className="product-image" style={{ backgroundColor: product.accent }}>
                    <img src={product.image_url} alt="" loading="lazy" />
                    {product.badge && <span className="product-badge">{product.badge}</span>}
                    <button className="favorite-button" aria-label={`Save ${product.name} to favorites`} onClick={() => setNotice(`${product.name} saved for later`)}><Heart size={17} /></button>
                  </div>
                  <div className="product-content">
                    <div className="product-title-row"><h3>{product.name}</h3><span className="product-price">{money(product.price)}<small> / {product.unit}</small></span></div>
                    <p className="product-maker">Brand: {product.maker}</p>
                    <p className="product-description">{product.description}</p>
                    <div className="product-card-footer">
                      <span className="product-rating"><Star size={14} fill="currentColor" /> {product.rating} <small>({product.reviews})</small></span>
                      <button className={`add-button ${cart[product.id] ? "added" : ""}`} onClick={() => addToCart(product)} aria-label={`Add ${product.name} to cart`}>
                        {cart[product.id] ? <><Check size={15} /> Added</> : <><Plus size={15} /> Add</>}
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          ) : (
            <div className="empty-results"><span className="empty-icon"><Search size={22} /></span><h3>No groceries found</h3><p>Try another search or choose a different category.</p><button className="text-link" onClick={() => { setSearch(""); setActiveCategory("All groceries"); }}>Clear filters <ArrowRight size={15} /></button></div>
          )}
          <div className="catalog-footer"><span>Showing <strong>{filteredProducts.length}</strong> of {products.length} grocery picks</span><button className="catalog-link" onClick={() => { setActiveCategory("All groceries"); setSearch(""); }}>Shop everything <ArrowRight size={15} /></button></div>
        </section>

        <section className="why-section" id="why-softly">
          <div className="why-art"><div className="why-blob"><span>✳</span></div><div className="why-note"><span className="note-star">✦</span><p>Fresh food.<br /><strong>Happy homes.</strong></p><span className="note-caption">That's the Daily Basket way.</span></div></div>
          <div className="why-copy"><span className="eyebrow section-eyebrow">A LITTLE MORE FRESH, EVERY DAY</span><h2>Everyday essentials, picked with <span>care.</span></h2><p>From crisp greens and seasonal fruit to pantry staples and chai-time snacks, Daily Basket brings the groceries you love together in one easy-to-shop place. Add your PIN code to tell us where you need your order.</p><a className="button button-outline" href="#products">Explore today's picks <ArrowRight size={16} /></a></div>
        </section>

        <section className="newsletter">
          <div className="newsletter-mark"><span aria-hidden="true">✦</span></div><div><h2>A little freshness in your inbox.</h2><p>Seasonal picks, pantry favourites, and occasional offers.</p></div>
          <form className="newsletter-form" onSubmit={(event) => { event.preventDefault(); setNotice("Thanks for subscribing! Newsletter delivery is not connected yet."); }}>
            <label className="sr-only" htmlFor="newsletter-email">Your email address</label><input id="newsletter-email" type="email" placeholder="Your email address" required /><button className="button button-dark" type="submit">Keep me posted <ArrowRight size={15} /></button>
          </form>
        </section>
      </main>

      <footer className="site-footer"><a href="#" className="brand footer-brand"><span className="brand-mark basket-mark"><ShoppingBasket size={20} /></span><span>daily basket</span></a><span>Fresh groceries for your everyday • Daily Basket</span><div><a href="#why-softly">Our promise</a><a href="mailto:hello@dailybasket.example">Say hello</a><a href="#products">Back to top ↑</a></div></footer>

      {notice && <div className="toast" role="status"><span><Check size={16} /></span>{notice}<button aria-label="Dismiss notification" onClick={() => setNotice("")}><X size={15} /></button></div>}

      {cartOpen && <div className="drawer-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setCartOpen(false); }}>
        <aside className="cart-drawer" role="dialog" aria-modal="true" aria-labelledby="cart-heading">
          <div className="drawer-header"><div><span className="eyebrow section-eyebrow">YOUR DAILY PICKS</span><h2 id="cart-heading">Your cart <span>({cartCount})</span></h2></div><button className="icon-button" aria-label="Close cart" onClick={() => setCartOpen(false)}><X size={21} /></button></div>
          {cartItems.length ? <>
            <div className="free-delivery-message">{freeDeliveryProgress === 0 ? <><Check size={15} /> You unlocked free delivery!</> : <>Add {money(freeDeliveryProgress)} more for free delivery.</>}<div className="delivery-progress"><span style={{ width: `${Math.min(100, (subtotal / 499) * 100)}%` }} /></div></div>
            <div className="cart-items">{cartItems.map((product) => <div className="cart-item" key={product.id}><img src={product.image_url} alt="" /><div className="cart-item-info"><strong>{product.name}</strong><span>{money(product.price)} / {product.unit}</span><div className="quantity-control"><button aria-label={`Remove one ${product.name}`} onClick={() => changeQuantity(product.id, -1)}><Minus size={13} /></button><span>{cart[product.id]}</span><button aria-label={`Add one ${product.name}`} onClick={() => changeQuantity(product.id, 1)}><Plus size={13} /></button></div></div><strong className="cart-item-total">{money(product.price * cart[product.id])}</strong></div>)}</div>
            <div className="cart-summary"><div><span>Subtotal</span><strong>{money(subtotal)}</strong></div><div><span>Delivery</span><strong>{deliveryFee === 0 ? "FREE" : money(deliveryFee)}</strong></div><div className="cart-total"><span>Total</span><strong>{money(subtotal + deliveryFee)}</strong></div><p>Prices are in INR. Payment and live delivery booking are not connected in this demo.</p><button className="button button-dark checkout-button" onClick={() => setNotice("Checkout isn't live yet. Connect a payment provider and delivery service to accept grocery orders.")}>Proceed to checkout <ArrowRight size={16} /></button><span className="secure-note"><ShieldCheck size={14} /> UPI, cards, and cash on delivery are not set up yet</span></div>
          </> : <div className="cart-empty"><span className="empty-icon"><ShoppingBasket size={22} /></span><h3>Your basket is empty</h3><p>Add fresh picks and pantry favourites to get started.</p><button className="button button-dark" onClick={() => { setCartOpen(false); document.getElementById("products")?.scrollIntoView({ behavior: "smooth" }); }}>Start shopping <ArrowRight size={15} /></button></div>}
        </aside>
      </div>}
    </div>
  );
}

export default App;
