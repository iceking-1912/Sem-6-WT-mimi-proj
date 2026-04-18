/**
 * cart.js – Shopping cart page logic.
 *
 * Responsibilities:
 *   • Redirect to login if user is not authenticated.
 *   • Fetch and display cart items for the current user.
 *   • Update item quantity inline.
 *   • Remove individual items.
 *   • Clear the entire cart.
 *   • Show the order total.
 *   • Handle a mock checkout flow.
 */

/* ================================================================
   Page initialisation
   ================================================================ */

document.addEventListener('DOMContentLoaded', () => {
    const user = getUser();

    if (!user) {
        showToast('Please log in to view your cart.', 'warning');
        setTimeout(() => { window.location.href = 'login.html'; }, 1000);
        return;
    }

    // Wire static buttons
    const clearCartBtn = document.getElementById('clearCartBtn');
    if (clearCartBtn) clearCartBtn.addEventListener('click', clearCart);

    const checkoutBtn = document.getElementById('checkoutBtn');
    if (checkoutBtn) checkoutBtn.addEventListener('click', checkout);

    // Delegated listener for dynamically rendered cart rows
    const tbody = document.getElementById('cartBody');
    if (tbody) {
        tbody.addEventListener('change', (e) => {
            if (e.target.matches('.qty-input')) {
                const cartId = parseInt(e.target.closest('tr').id.replace('row-', ''), 10);
                updateQuantity(cartId, e.target);
            }
        });
        tbody.addEventListener('click', (e) => {
            const btn = e.target.closest('.remove-item-btn');
            if (!btn) return;
            const cartId = parseInt(btn.dataset.cartId, 10);
            removeItem(cartId);
        });
    }

    loadCart();
});

/* ================================================================
   Load / render cart
   ================================================================ */

async function loadCart() {
    const user = getUser();
    showSpinner(true);

    try {
        const res = await fetch(`${API_BASE}/cart?userId=${user.id}`);

        if (!res.ok) {
            const data = await res.json();
            throw new Error(data.error || `HTTP ${res.status}`);
        }

        const items = await res.json();
        renderCart(items);
    } catch (err) {
        showSpinner(false);
        showToast('Could not load cart: ' + err.message, 'error');
        console.error('loadCart error:', err);
    }
}

/** Renders the cart table or the empty-state message. */
function renderCart(items) {
    showSpinner(false);

    const content    = document.getElementById('cartContent');
    const emptyState = document.getElementById('emptyState');

    if (!items || items.length === 0) {
        content.style.display    = 'none';
        emptyState.style.display = 'block';
        return;
    }

    emptyState.style.display = 'none';
    content.style.display    = 'block';

    const tbody = document.getElementById('cartBody');
    tbody.innerHTML = items.map(cartRow).join('');
    updateTotal(items);
}

/** Returns the HTML string for one cart row. */
function cartRow(item) {
    const book    = item.book || {};
    const imgSrc  = book.imageUrl
        ? escapeHtml(book.imageUrl)
        : 'https://via.placeholder.com/50x65?text=?';
    const title   = escapeHtml(book.title  || 'Unknown');
    const author  = escapeHtml(book.author || '—');
    const price   = Number(book.price  || 0).toFixed(2);
    const sub     = (Number(book.price || 0) * item.quantity).toFixed(2);

    return `
        <tr id="row-${item.id}">
            <td><img src="${imgSrc}" alt="${title}"
                     onerror="this.src='https://via.placeholder.com/50x65?text=?'"></td>
            <td>${title}</td>
            <td>${author}</td>
            <td>$${price}</td>
            <td>
                <input class="qty-input"
                       type="number"
                       min="1"
                       value="${item.quantity}"
                       aria-label="Quantity for ${title}">
            </td>
            <td id="sub-${item.id}">$${sub}</td>
            <td>
                <button class="btn btn-danger remove-item-btn"
                        data-cart-id="${item.id}">Remove</button>
            </td>
        </tr>`;
}

/** Recalculates and displays the order total from the current DOM state. */
function updateTotal(items) {
    const total = items.reduce((sum, i) => {
        return sum + (Number(i.book ? i.book.price : 0) * i.quantity);
    }, 0);
    const el = document.getElementById('cartTotal');
    if (el) el.textContent = `$${total.toFixed(2)}`;
}

/* ================================================================
   Quantity update
   ================================================================ */

/**
 * Validates the quantity input and sends a PUT request to the API.
 * @param {number} cartId
 * @param {HTMLInputElement} input
 */
async function updateQuantity(cartId, input) {
    const qty = parseInt(input.value, 10);

    if (isNaN(qty) || qty < 1) {
        input.value = 1;
        showToast('Quantity must be at least 1.', 'warning');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/cart`, {
            method:  'PUT',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ cartId, quantity: qty })
        });

        if (!res.ok) {
            const data = await res.json();
            showToast(data.error || 'Could not update quantity.', 'error');
            return;
        }

        showToast('Quantity updated.', 'success');
        // Update the subtotal cell for this row without a full reload
        refreshRowSubtotal(cartId, qty);
        recalcTotalFromDom();
    } catch {
        showToast('Could not reach the server.', 'error');
    }
}

/** Updates the subtotal cell in a cart row. */
function refreshRowSubtotal(cartId, newQty) {
    const row    = document.getElementById(`row-${cartId}`);
    if (!row) return;

    const cells  = row.querySelectorAll('td');
    // Price is in column index 3 (0-based)
    const priceText = cells[3] ? cells[3].textContent.replace('$', '') : '0';
    const price     = parseFloat(priceText) || 0;
    const subCell   = document.getElementById(`sub-${cartId}`);
    if (subCell) subCell.textContent = `$${(price * newQty).toFixed(2)}`;
}

/** Sums all visible subtotal cells to update the total. */
function recalcTotalFromDom() {
    let total = 0;
    document.querySelectorAll('[id^="sub-"]').forEach(el => {
        total += parseFloat(el.textContent.replace('$', '')) || 0;
    });
    const el = document.getElementById('cartTotal');
    if (el) el.textContent = `$${total.toFixed(2)}`;
}

/* ================================================================
   Remove item
   ================================================================ */

async function removeItem(cartId) {
    if (!confirm('Remove this item from your cart?')) return;

    try {
        const res = await fetch(`${API_BASE}/cart?cartId=${cartId}`, { method: 'DELETE' });

        if (!res.ok) {
            const data = await res.json();
            showToast(data.error || 'Could not remove item.', 'error');
            return;
        }

        const row = document.getElementById(`row-${cartId}`);
        if (row) row.remove();

        showToast('Item removed from cart.', 'success');
        recalcTotalFromDom();

        // If the table is now empty, show the empty state
        if (!document.querySelector('#cartBody tr')) {
            document.getElementById('cartContent').style.display = 'none';
            document.getElementById('emptyState').style.display  = 'block';
        }
    } catch {
        showToast('Could not reach the server.', 'error');
    }
}

/* ================================================================
   Clear entire cart
   ================================================================ */

async function clearCart() {
    if (!confirm('Remove all items from your cart?')) return;

    const rows = document.querySelectorAll('#cartBody tr');
    if (rows.length === 0) return;

    // Extract cart IDs from row ids (row-{id})
    const ids = Array.from(rows).map(r => parseInt(r.id.replace('row-', ''), 10));

    let anyError = false;
    for (const id of ids) {
        try {
            await fetch(`${API_BASE}/cart?cartId=${id}`, { method: 'DELETE' });
        } catch {
            anyError = true;
        }
    }

    if (anyError) {
        showToast('Some items could not be removed.', 'warning');
    } else {
        showToast('Cart cleared.', 'success');
    }

    // Reload to get fresh state
    loadCart();
}

/* ================================================================
   Checkout (mock)
   ================================================================ */

function checkout() {
    showToast('Thank you for your order! (Payment gateway coming soon.)', 'success');
}

/* ================================================================
   Spinner helper
   ================================================================ */

function showSpinner(show) {
    const spinner = document.getElementById('spinner');
    if (spinner) spinner.style.display = show ? 'block' : 'none';
}
