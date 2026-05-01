let currentItems = [];
const loadCart = async () => {
    const u = getUser(); if(!u) return location.href='login.html';
    try {
        const r = await fetch(`${API_BASE}/cart?userId=${u.id}`);
        currentItems = await r.json(); renderCart(currentItems);
    } catch(e) { showToast('Error loading cart', 'error'); }
};
const renderCart = (items) => {
    const b = document.getElementById('cartBody'), c = document.getElementById('cartContent'), e = document.getElementById('emptyState');
    if(!b) return;
    if(!items.length) { c.style.display='none'; e.style.display='block'; return; }
    e.style.display='none'; c.style.display='block';
    b.innerHTML = items.map(i => `
        <tr id="row-${i.id}">
            <td><img src="${i.book.imageUrl || ''}" style="width:50px"></td>
            <td>${escapeHtml(i.book.title)}</td>
            <td>${escapeHtml(i.book.author)}</td>
            <td>$${i.book.price.toFixed(2)}</td>
            <td><input type="number" value="${i.quantity}" min="1" onchange="updateQty(${i.id}, this.value)" style="width:50px"></td>
            <td>$${(i.book.price * i.quantity).toFixed(2)}</td>
            <td><button class="btn btn-danger" onclick="remove(${i.id})">Remove</button></td>
        </tr>
    `).join('');
    document.getElementById('cartTotal').textContent = '$' + items.reduce((s,i)=>s+(i.book.price*i.quantity), 0).toFixed(2);
};
const updateQty = async (id, q) => {
    await fetch(`${API_BASE}/cart`, { method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify({cartId:id, quantity:q}) });
    loadCart();
    if (typeof updateCartCount === 'function') updateCartCount();
};
const remove = async (id, silent) => {
    if(silent || confirm('Remove?')) { 
        await fetch(`${API_BASE}/cart?cartId=${id}`, { method:'DELETE' }); 
        loadCart(); 
        if (typeof updateCartCount === 'function') updateCartCount();
    }
};
const generateBill = () => {
    const u = getUser(); if(!u || !currentItems.length) return;
    const total = currentItems.reduce((s,i)=>s+(i.book.price*i.quantity), 0);
    const win = window.open('', '_blank');
    win.document.write(`
        <html><head><title>Bill - Bookstore</title><style>body{font-family:sans-serif;padding:20px}table{width:100%;border-collapse:collapse}th,td{border:1px solid #ddd;padding:8px;text-align:left}th{background:#f2f2f2}.total{text-align:right;font-size:1.2rem;margin-top:20px}</style></head>
        <body><h1>Bookstore - Order Invoice</h1><p>Customer: ${u.username} (${u.email})</p><p>Date: ${new Date().toLocaleString()}</p><p>Payment Mode: Cash on Delivery</p>
        <table><thead><tr><th>Book</th><th>Qty</th><th>Price</th><th>Subtotal</th></tr></thead>
        <tbody>${currentItems.map(i=>`<tr><td>${i.book.title}</td><td>${i.quantity}</td><td>$${i.book.price.toFixed(2)}</td><td>$${(i.book.price*i.quantity).toFixed(2)}</td></tr>`).join('')}</tbody></table>
        <div class="total"><strong>Total: $${total.toFixed(2)}</strong></div>
        <script>window.print();</script></body></html>
    `);
    win.document.close();
    if(confirm('Order placed! Clear cart?')) currentItems.forEach(i => remove(i.id, true));
};
document.addEventListener('DOMContentLoaded', () => {
    loadCart();
    const c = document.getElementById('checkoutBtn'); if(c) c.onclick = generateBill;
    const cl = document.getElementById('clearCartBtn'); if(cl) cl.onclick = () => confirm('Clear all?') && currentItems.forEach(i => remove(i.id, true));
});
