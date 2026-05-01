let allBooks = [];
const loadBooks = async () => {
    try {
        const r = await fetch(`${API_BASE}/books`);
        allBooks = await r.json(); render(allBooks);
    } catch(e) { console.error(e); }
};
const render = (books) => {
    const g = document.getElementById('booksGrid'), s = document.getElementById('spinner'), e = document.getElementById('emptyState');
    if(s) s.style.display = 'none'; if(!g) return;
    g.style.display = books.length ? 'grid' : 'none';
    if (e) e.style.display = books.length ? 'none' : 'block';
    g.innerHTML = books.map(b => `
        <div class="book-card">
            <img src="${b.imageUrl || 'https://via.placeholder.com/150'}" onerror="this.src='https://via.placeholder.com/150'">
            <div class="card-body">
                <div class="book-title">${escapeHtml(b.title)}</div>
                <div class="book-author">by ${escapeHtml(b.author)}</div>
                <div class="book-price">$${b.price.toFixed(2)}</div>
            </div>
            <div class="card-footer"><button class="btn btn-accent" style="width:100%" onclick="addToCart(${b.id}, '${escapeHtml(b.title)}')">Add to Cart</button></div>
        </div>
    `).join('');
};
const addToCart = async (id, title) => {
    const u = getUser(); if(!u) return location.href='login.html';
    try {
        const r = await fetch(`${API_BASE}/cart`, { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({userId:u.id, bookId:id}) });
        if (r.ok) {
            showToast(title + ' added!', 'success');
            if (typeof updateCartCount === 'function') updateCartCount();
        }
    } catch(e) { showToast('Error', 'error'); }
};
const search = () => {
    const k = document.getElementById('searchInput').value.toLowerCase();
    render(allBooks.filter(b => b.title.toLowerCase().includes(k) || b.author.toLowerCase().includes(k)));
};
document.addEventListener('DOMContentLoaded', () => {
    loadBooks();
    const btn = document.getElementById('searchBtn'); if(btn) btn.onclick = search;
    const inp = document.getElementById('searchInput'); if (inp) inp.onkeydown = (e) => {
        if (e.key === 'Enter') search();
    };
    const clearBtn = document.getElementById('clearBtn');
    if (clearBtn) clearBtn.onclick = () => {
        if (inp) inp.value = '';
        render(allBooks);
    };
});
