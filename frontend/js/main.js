/**
 * main.js – Home page logic.
 *
 * Responsibilities:
 *   • Load and display all books on page load.
 *   • Search books by title or author.
 *   • Add a book to the cart (requires login).
 */

/* ================================================================
   State
   ================================================================ */

let allBooks = []; // cached full list for quick client-side filtering

/* ================================================================
   Book loading
   ================================================================ */

/** Fetches all books from the API and renders them. */
async function loadBooks() {
    showSpinner(true);

    try {
        const res   = await fetch(`${API_BASE}/books`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        allBooks    = await res.json();
        renderBooks(allBooks);
        updateSectionTitle('All Books');
    } catch (err) {
        showSpinner(false);
        document.getElementById('emptyState').style.display = 'block';
        document.getElementById('emptyState').innerHTML =
            '<div class="icon">⚠️</div>' +
            '<p>Could not load books. Make sure the backend is running.</p>';
        console.error('loadBooks error:', err);
    }
}

/** Sends a search request to the API. */
async function searchBooks() {
    const keyword = document.getElementById('searchInput').value.trim();

    if (!keyword) {
        renderBooks(allBooks);
        updateSectionTitle('All Books');
        return;
    }

    showSpinner(true);

    try {
        const res  = await fetch(`${API_BASE}/books?search=${encodeURIComponent(keyword)}`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        renderBooks(data);
        updateSectionTitle(`Results for "${escapeHtml(keyword)}"`);
    } catch (err) {
        showSpinner(false);
        showToast('Search failed. Please try again.', 'error');
        console.error('searchBooks error:', err);
    }
}

/** Clears the search box and shows all books. */
function clearSearch() {
    document.getElementById('searchInput').value = '';
    renderBooks(allBooks);
    updateSectionTitle('All Books');
}

/* ================================================================
   Rendering
   ================================================================ */

/** Renders an array of book objects into the grid. */
function renderBooks(books) {
    showSpinner(false);

    const grid       = document.getElementById('booksGrid');
    const emptyState = document.getElementById('emptyState');

    if (!books || books.length === 0) {
        grid.style.display       = 'none';
        emptyState.style.display = 'block';
        emptyState.innerHTML =
            '<div class="icon">🔍</div><p>No books found. Try a different search term.</p>';
        return;
    }

    emptyState.style.display = 'none';
    grid.style.display       = 'grid';
    grid.innerHTML = books.map(bookCard).join('');
}

/** Returns the HTML string for a single book card. */
function bookCard(book) {
    const imgSrc = book.imageUrl
        ? escapeHtml(book.imageUrl)
        : 'https://via.placeholder.com/210x200?text=No+Cover';

    const category = book.category
        ? `<span class="book-category">${escapeHtml(book.category)}</span>`
        : '';

    return `
        <div class="book-card">
            <img src="${imgSrc}"
                 alt="Cover of ${escapeHtml(book.title)}"
                 onerror="this.src='https://via.placeholder.com/210x200?text=No+Cover'">
            <div class="card-body">
                <div class="book-title">${escapeHtml(book.title)}</div>
                <div class="book-author">by ${escapeHtml(book.author)}</div>
                ${category}
                <div class="book-price">$${Number(book.price).toFixed(2)}</div>
            </div>
            <div class="card-footer">
                <button class="btn btn-accent add-to-cart-btn" style="width:100%;"
                        data-book-id="${book.id}"
                        data-book-title="${escapeHtml(book.title)}">
                    🛒 Add to Cart
                </button>
            </div>
        </div>`;
}
}

/* ================================================================
   Cart interaction
   ================================================================ */

/** Adds a book to the logged-in user's cart. */
async function addToCart(bookId, bookTitle) {
    const user = getUser();

    if (!user) {
        showToast('Please log in to add books to your cart.', 'warning');
        setTimeout(() => { window.location.href = 'login.html'; }, 1200);
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/cart`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ userId: user.id, bookId, quantity: 1 })
        });

        const data = await res.json();

        if (!res.ok) {
            showToast(data.error || 'Could not add to cart.', 'error');
            return;
        }

        showToast(`"${bookTitle}" added to cart! 🛒`, 'success');
        updateCartBadge();
    } catch {
        showToast('Could not reach the server.', 'error');
    }
}

/* ================================================================
   Cart badge (shows item count in navbar)
   ================================================================ */

async function updateCartBadge() {
    const user    = getUser();
    const badge   = document.getElementById('cartCount');
    if (!badge || !user) return;

    try {
        const res  = await fetch(`${API_BASE}/cart?userId=${user.id}`);
        if (!res.ok) return;
        const items = await res.json();
        const total = items.reduce((sum, i) => sum + i.quantity, 0);
        badge.textContent = total > 0 ? `(${total})` : '';
    } catch {
        // silently ignore badge update failures
    }
}

/* ================================================================
   Spinner helper
   ================================================================ */

function showSpinner(show) {
    const spinner = document.getElementById('spinner');
    const grid    = document.getElementById('booksGrid');
    if (spinner) spinner.style.display = show ? 'block' : 'none';
    if (grid && show) grid.style.display = 'none';
}

function updateSectionTitle(text) {
    const el = document.getElementById('sectionTitle');
    if (el) el.textContent = text;
}

/* ================================================================
   Allow pressing Enter in the search box
   ================================================================ */

document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('searchInput');
    if (input) {
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') searchBooks();
        });
    }

    const searchBtn = document.getElementById('searchBtn');
    if (searchBtn) searchBtn.addEventListener('click', searchBooks);

    const clearBtn = document.getElementById('clearBtn');
    if (clearBtn) clearBtn.addEventListener('click', clearSearch);

    // Delegated event listener for all "Add to Cart" buttons in the grid.
    // Using delegation avoids re-attaching listeners every time the grid re-renders
    // and removes the need for inline onclick handlers (which risk injection).
    const grid = document.getElementById('booksGrid');
    if (grid) {
        grid.addEventListener('click', (e) => {
            const btn = e.target.closest('.add-to-cart-btn');
            if (!btn) return;
            const bookId    = parseInt(btn.dataset.bookId, 10);
            const bookTitle = btn.dataset.bookTitle || '';
            addToCart(bookId, bookTitle);
        });
    }

    loadBooks();
    updateCartBadge();
});
