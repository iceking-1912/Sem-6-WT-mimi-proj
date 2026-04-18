/**
 * auth.js – Shared authentication utilities.
 *
 * Runs on every page to:
 *   • Render the correct navbar state (login vs. logout).
 *   • Handle login / register form submissions on their respective pages.
 *   • Expose helper functions used by other scripts.
 */

const API_BASE = 'http://localhost:8080/bookstore/api';

/* ================================================================
   Session helpers
   ================================================================ */

/**
 * Returns the currently logged-in user object from localStorage,
 * or null if no session exists.
 * @returns {{ id: number, username: string, email: string }|null}
 */
function getUser() {
    try {
        return JSON.parse(localStorage.getItem('bookstore_user'));
    } catch {
        return null;
    }
}

/** Saves the user object to localStorage. */
function saveUser(user) {
    localStorage.setItem('bookstore_user', JSON.stringify(user));
}

/** Removes the user session from localStorage. */
function clearUser() {
    localStorage.removeItem('bookstore_user');
}

/* ================================================================
   Toast / notification helper
   ================================================================ */

/**
 * Displays a temporary toast notification.
 * @param {string} message  Text to show.
 * @param {'success'|'error'|'warning'|''} [type='']  Visual style.
 */
function showToast(message, type) {
    const toast = document.getElementById('toast');
    if (!toast) { alert(message); return; }

    toast.textContent = message;
    toast.className   = 'toast show' + (type ? ' ' + type : '');

    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => {
        toast.className = 'toast';
    }, 3500);
}

/* ================================================================
   Navbar: update auth link based on session
   ================================================================ */

function updateNavbar() {
    const navAuth = document.getElementById('navAuth');
    if (!navAuth) return;

    const user = getUser();
    if (user) {
        navAuth.innerHTML =
            `<span style="color:#ecf0f1;font-size:.9rem;">Hi, ${escapeHtml(user.username)}</span>
             <a href="#" class="btn-nav" onclick="logout(event)" style="margin-left:.6rem;">Logout</a>`;
    } else {
        navAuth.innerHTML = `<a href="login.html" class="btn-nav">Login</a>`;
    }
}

function logout(event) {
    if (event) event.preventDefault();
    clearUser();
    showToast('You have been logged out.', 'success');
    setTimeout(() => { window.location.href = 'index.html'; }, 800);
}

/* ================================================================
   Validation helpers
   ================================================================ */

const USERNAME_REGEX   = /^[A-Za-z0-9_]{3,50}$/;
const EMAIL_REGEX      = /^[A-Za-z0-9+_.\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/;

/**
 * Shows or hides a field-level error message and toggles the 'invalid' class.
 * @param {HTMLInputElement} input
 * @param {string|null} message  Pass null to clear the error.
 */
function setFieldError(input, message) {
    const errorEl = document.getElementById(input.id + 'Error');
    if (message) {
        input.classList.add('invalid');
        if (errorEl) { errorEl.textContent = message; errorEl.classList.add('visible'); }
    } else {
        input.classList.remove('invalid');
        if (errorEl) { errorEl.textContent = ''; errorEl.classList.remove('visible'); }
    }
}

/** Returns true when all arguments have no error (they are null/undefined). */
function allValid(...errors) {
    return errors.every(e => !e);
}

/* ================================================================
   Register page
   ================================================================ */

(function initRegister() {
    const form = document.getElementById('registerForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username        = document.getElementById('username');
        const email           = document.getElementById('email');
        const password        = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');

        // ---- client-side validation ----
        let usernameErr = null, emailErr = null, passwordErr = null, confirmErr = null;

        if (!username.value.trim()) {
            usernameErr = 'Username is required.';
        } else if (!USERNAME_REGEX.test(username.value.trim())) {
            usernameErr = 'Username must be 3–50 characters (letters, digits, underscore).';
        }

        if (!email.value.trim()) {
            emailErr = 'Email address is required.';
        } else if (!EMAIL_REGEX.test(email.value.trim())) {
            emailErr = 'Please enter a valid email address.';
        }

        if (!password.value) {
            passwordErr = 'Password is required.';
        } else if (password.value.length < 6) {
            passwordErr = 'Password must be at least 6 characters.';
        }

        if (!confirmPassword.value) {
            confirmErr = 'Please confirm your password.';
        } else if (confirmPassword.value !== password.value) {
            confirmErr = 'Passwords do not match.';
        }

        setFieldError(username,        usernameErr);
        setFieldError(email,           emailErr);
        setFieldError(password,        passwordErr);
        setFieldError(confirmPassword, confirmErr);

        if (!allValid(usernameErr, emailErr, passwordErr, confirmErr)) return;

        // ---- submit to API ----
        try {
            const res = await fetch(`${API_BASE}/users/register`, {
                method:  'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify({
                    username: username.value.trim(),
                    email:    email.value.trim(),
                    password: password.value
                })
            });

            const data = await res.json();

            if (!res.ok) {
                showToast(data.error || 'Registration failed.', 'error');

                // Highlight field if duplicate
                if (data.error && data.error.toLowerCase().includes('username')) {
                    setFieldError(username, data.error);
                } else if (data.error && data.error.toLowerCase().includes('email')) {
                    setFieldError(email, data.error);
                }
                return;
            }

            showToast('Account created successfully! Redirecting to login…', 'success');
            setTimeout(() => { window.location.href = 'login.html'; }, 1500);

        } catch {
            showToast('Cannot reach the server. Is the backend running?', 'error');
        }
    });

    // Live validation feedback
    document.getElementById('username').addEventListener('input', function () {
        if (!this.value.trim()) {
            setFieldError(this, 'Username is required.');
        } else if (!USERNAME_REGEX.test(this.value.trim())) {
            setFieldError(this, 'Username must be 3–50 characters (letters, digits, underscore).');
        } else {
            setFieldError(this, null);
        }
    });

    document.getElementById('email').addEventListener('input', function () {
        if (!this.value.trim()) {
            setFieldError(this, 'Email is required.');
        } else if (!EMAIL_REGEX.test(this.value.trim())) {
            setFieldError(this, 'Enter a valid email address.');
        } else {
            setFieldError(this, null);
        }
    });

    document.getElementById('password').addEventListener('input', function () {
        if (!this.value) {
            setFieldError(this, 'Password is required.');
        } else if (this.value.length < 6) {
            setFieldError(this, 'Password must be at least 6 characters.');
        } else {
            setFieldError(this, null);
        }
        const confirm = document.getElementById('confirmPassword');
        if (confirm.value && confirm.value !== this.value) {
            setFieldError(confirm, 'Passwords do not match.');
        } else if (confirm.value) {
            setFieldError(confirm, null);
        }
    });

    document.getElementById('confirmPassword').addEventListener('input', function () {
        const pwd = document.getElementById('password').value;
        if (!this.value) {
            setFieldError(this, 'Please confirm your password.');
        } else if (this.value !== pwd) {
            setFieldError(this, 'Passwords do not match.');
        } else {
            setFieldError(this, null);
        }
    });
})();

/* ================================================================
   Login page
   ================================================================ */

(function initLogin() {
    const form = document.getElementById('loginForm');
    if (!form) return;

    // Already logged in? Redirect
    if (getUser()) {
        window.location.href = 'index.html';
        return;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = document.getElementById('username');
        const password = document.getElementById('password');

        let usernameErr = null, passwordErr = null;

        if (!username.value.trim()) usernameErr = 'Username is required.';
        if (!password.value)        passwordErr = 'Password is required.';

        setFieldError(username, usernameErr);
        setFieldError(password, passwordErr);

        if (!allValid(usernameErr, passwordErr)) return;

        try {
            const res = await fetch(`${API_BASE}/users/login`, {
                method:  'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify({
                    username: username.value.trim(),
                    password: password.value
                })
            });

            const data = await res.json();

            if (!res.ok) {
                showToast(data.error || 'Login failed.', 'error');
                setFieldError(password, data.error || 'Invalid credentials.');
                return;
            }

            saveUser(data);
            showToast(`Welcome back, ${data.username}!`, 'success');
            setTimeout(() => { window.location.href = 'index.html'; }, 800);

        } catch {
            showToast('Cannot reach the server. Is the backend running?', 'error');
        }
    });
})();

/* ================================================================
   XSS helper
   ================================================================ */
function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

/* ================================================================
   Run on every page: update navbar
   ================================================================ */
document.addEventListener('DOMContentLoaded', updateNavbar);
