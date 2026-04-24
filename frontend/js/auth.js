const API_BASE = 'http://localhost:8080/bookstore/api';
const getUser = () => { try { return JSON.parse(localStorage.getItem('bookstore_user')); } catch { return null; } };
const saveUser = (u) => localStorage.setItem('bookstore_user', JSON.stringify(u));
const clearUser = () => localStorage.removeItem('bookstore_user');
const showToast = (m, t) => {
    const el = document.getElementById('toast'); if(!el) return;
    el.textContent = m; el.className = 'toast show ' + (t||'');
    setTimeout(() => el.className = 'toast', 3000);
};
const updateNavbar = () => {
    const el = document.getElementById('navAuth'), u = getUser();
    if (!el) return;
    el.innerHTML = u ? `<span style="color:#fff;font-size:.9rem;">Hi, ${escapeHtml(u.username)}</span> <a href="#" id="logoutBtn" class="btn-nav" style="margin-left:.5rem;">Logout</a>` : '<a href="login.html" class="btn-nav">Login</a>';
    if (u && document.getElementById('logoutBtn')) document.getElementById('logoutBtn').onclick = (e) => { e.preventDefault(); clearUser(); location.href='index.html'; };
};
const escapeHtml = (s) => String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const setFieldError = (i, m) => {
    const e = document.getElementById(i.id + 'Error');
    if (m) { i.classList.add('invalid'); if(e) { e.textContent = m; e.classList.add('visible'); } }
    else { i.classList.remove('invalid'); if(e) { e.textContent = ''; e.classList.remove('visible'); } }
};
document.addEventListener('DOMContentLoaded', updateNavbar);

const initForm = (id, url, cb) => {
    const f = document.getElementById(id); if(!f) return;
    f.onsubmit = async (e) => {
        e.preventDefault(); const d = Object.fromEntries(new FormData(f));
        try {
            const r = await fetch(API_BASE + url, { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(d) });
            const res = await r.json(); if(!r.ok) throw new Error(res.error || 'Failed');
            cb(res);
        } catch(err) { showToast(err.message, 'error'); }
    };
};
initForm('loginForm', '/users/login', (u) => { saveUser(u); location.href='index.html'; });
initForm('registerForm', '/users/register', () => { showToast('Registered!', 'success'); setTimeout(()=>location.href='login.html', 1000); });
