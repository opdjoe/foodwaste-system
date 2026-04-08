/**
 * Food Waste & Inventory Analytics System
 * Shared API client + utility helpers
 */

const API_BASE = 'http://localhost:8080/api';

// ── Token helpers ─────────────────────────────────────────────────────────────
const Auth = {
    getToken: () => localStorage.getItem('fw_token'),
    getUser:  () => JSON.parse(localStorage.getItem('fw_user') || '{}'),
    isLoggedIn: () => !!localStorage.getItem('fw_token'),
    save(token, user) {
        localStorage.setItem('fw_token', token);
        localStorage.setItem('fw_user', JSON.stringify(user));
    },
    clear() {
        localStorage.removeItem('fw_token');
        localStorage.removeItem('fw_user');
    },
    requireAuth() {
        if (!this.isLoggedIn()) window.location.href = 'index.html';
    },
    redirectIfLoggedIn() {
        if (this.isLoggedIn()) window.location.href = 'dashboard.html';
    }
};

// ── API wrapper ───────────────────────────────────────────────────────────────
const API = {
    _headers() {
        const h = { 'Content-Type': 'application/json' };
        const token = Auth.getToken();
        if (token) h['Authorization'] = `Bearer ${token}`;
        return h;
    },

    async _request(method, path, body) {
        const opts = { method, headers: this._headers() };
        if (body) opts.body = JSON.stringify(body);
        try {
            const res = await fetch(`${API_BASE}${path}`, opts);
            const json = await res.json();
            if (res.status === 401) { Auth.clear(); window.location.href = 'index.html'; }
            return { ok: res.ok, status: res.status, data: json };
        } catch (err) {
            console.error('API error:', err);
            return { ok: false, status: 0, data: { success: false, message: 'Network error' } };
        }
    },

    get:    (path)       => API._request('GET',    path),
    post:   (path, body) => API._request('POST',   path, body),
    put:    (path, body) => API._request('PUT',    path, body),
    delete: (path)       => API._request('DELETE', path),

    // Domain methods
    login:           (dto)   => API.post('/auth/login', dto),
    register:        (dto)   => API.post('/auth/register', dto),
    getInventory:    ()      => API.get('/inventory'),
    getInventoryById:(id)    => API.get(`/inventory/${id}`),
    createInventory: (dto)   => API.post('/inventory', dto),
    updateInventory: (id, d) => API.put(`/inventory/${id}`, d),
    deleteInventory: (id)    => API.delete(`/inventory/${id}`),
    getLowStock:     ()      => API.get('/inventory/low-stock'),
    getExpired:      ()      => API.get('/inventory/expired'),
    getWasteLogs:    ()      => API.get('/waste-logs'),
    createWasteLog:  (dto)   => API.post('/waste-logs', dto),
    getLogsByItem:   (id)    => API.get(`/waste-logs/by-item/${id}`),
    getAlerts:       ()      => API.get('/alerts'),
    createAlert:     (dto)   => API.post('/alerts', dto),
    updateAlert:     (id, d) => API.put(`/alerts/${id}`, d),
    deleteAlert:     (id)    => API.delete(`/alerts/${id}`),
    getAnalytics:    ()      => API.get('/analytics/summary'),
    getUsers:        ()      => API.get('/users'),
    deleteUser:      (id)    => API.delete(`/users/${id}`),
    updateUserRole:  (id, r) => API.put(`/users/${id}/role?role=${r}`),
};

// ── Toast notifications ───────────────────────────────────────────────────────
const Toast = {
    show(message, type = 'success') {
        const container = document.getElementById('toast-container') ||
            (() => {
                const el = document.createElement('div');
                el.id = 'toast-container';
                el.className = 'fixed top-5 right-5 z-50 flex flex-col gap-2';
                document.body.appendChild(el);
                return el;
            })();

        const icons = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };
        const colors = {
            success: 'bg-emerald-500',
            error:   'bg-rose-500',
            warning: 'bg-amber-500',
            info:    'bg-sky-500',
        };

        const toast = document.createElement('div');
        toast.className = `flex items-center gap-3 px-4 py-3 rounded-lg text-white shadow-lg
            text-sm font-medium transform translate-x-full transition-transform duration-300
            ${colors[type] || colors.info}`;
        toast.innerHTML = `<span class="font-bold text-lg">${icons[type]}</span><span>${message}</span>`;
        container.appendChild(toast);

        // Slide in
        requestAnimationFrame(() => {
            requestAnimationFrame(() => toast.classList.remove('translate-x-full'));
        });

        // Slide out and remove
        setTimeout(() => {
            toast.classList.add('translate-x-full');
            toast.addEventListener('transitionend', () => toast.remove());
        }, 3500);
    },

    success: (msg) => Toast.show(msg, 'success'),
    error:   (msg) => Toast.show(msg, 'error'),
    warning: (msg) => Toast.show(msg, 'warning'),
    info:    (msg) => Toast.show(msg, 'info'),
};

// ── Modal helpers ─────────────────────────────────────────────────────────────
const Modal = {
    open(id)  { document.getElementById(id)?.classList.remove('hidden'); },
    close(id) { document.getElementById(id)?.classList.add('hidden'); },
};

// ── Misc utilities ────────────────────────────────────────────────────────────
const Utils = {
    formatDate: (d) => d ? new Date(d).toLocaleDateString() : '—',
    formatDateTime: (d) => d ? new Date(d).toLocaleString() : '—',
    formatWeight: (w) => w != null ? `${Number(w).toFixed(2)} kg` : '—',
    badge(text, color = 'slate') {
        return `<span class="inline-block px-2 py-0.5 rounded-full text-xs font-semibold
            bg-${color}-100 text-${color}-800">${text}</span>`;
    },
    roleBadge(role) {
        const map = { ADMIN: 'purple', MANAGER: 'blue', STAFF: 'slate' };
        return Utils.badge(role, map[role] || 'slate');
    },
    renderNav() {
        const user = Auth.getUser();
        const el = document.getElementById('nav-username');
        const roleEl = document.getElementById('nav-role');
        if (el) el.textContent = user.username || 'User';
        if (roleEl) roleEl.textContent = user.role || '';
    },
};

// Expose to all pages
window.Auth  = Auth;
window.API   = API;
window.Toast = Toast;
window.Modal = Modal;
window.Utils = Utils;
