/**
 * Shared API utility for all pages
 */
const API = {
  token: () => localStorage.getItem('token'),
  user: () => JSON.parse(localStorage.getItem('user') || '{}'),

  headers() {
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + this.token()
    };
  },

  async get(url) {
    const r = await fetch(url, { headers: this.headers() });
    if (r.status === 401) { localStorage.clear(); location.href = '/'; return null; }
    return r.json();
  },

  async post(url, body) {
    const r = await fetch(url, {
      method: 'POST', headers: this.headers(), body: JSON.stringify(body)
    });
    if (r.status === 401) { localStorage.clear(); location.href = '/'; return null; }
    return r.json();
  },

  async put(url, body) {
    const r = await fetch(url, {
      method: 'PUT', headers: this.headers(), body: JSON.stringify(body)
    });
    return r.json();
  },

  async del(url) {
    const r = await fetch(url, { method: 'DELETE', headers: this.headers() });
    return r.json();
  },

  async download(url, filename) {
    const r = await fetch(url, {
      headers: { 'Authorization': 'Bearer ' + this.token() }
    });
    const blob = await r.blob();
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = filename;
    a.click();
  },

  logout() { localStorage.clear(); location.href = '/'; }
};

// Toast notification helper
function toast(msg, type = 'success') {
  const div = document.createElement('div');
  div.className = `toast-msg toast-${type}`;
  div.textContent = msg;
  document.body.appendChild(div);
  setTimeout(() => div.classList.add('show'), 10);
  setTimeout(() => { div.classList.remove('show'); setTimeout(() => div.remove(), 400); }, 3000);
}

// Navigate sidebar
function showSection(id) {
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
  document.getElementById(id).classList.add('active');
  document.querySelector(`[data-section="${id}"]`)?.classList.add('active');
}
