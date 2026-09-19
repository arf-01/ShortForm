const API_BASE = "/api";
const recentUrls = [];

const shortenForm = document.querySelector("#shorten-form");
const lookupForm = document.querySelector("#lookup-form");
const editForm = document.querySelector("#edit-form");
const resultPanel = document.querySelector("#result-panel");
const formMessage = document.querySelector("#form-message");
const lookupMessage = document.querySelector("#lookup-message");
const recentLinks = document.querySelector("#recent-links");
let activeUrl = null;

function setMessage(element, message = "") {
  element.textContent = message;
}

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json", ...options.headers },
    ...options,
  });

  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body = await response.json();
      message = body.message || body.error || message;
    } catch (_) {
      // The API may return an empty body for errors.
    }
    throw new Error(message);
  }

  return response.status === 204 ? null : response.json();
}

function shortUrl(code) {
  return `${API_BASE}/shorten/${code}`;
}

function saveRecent(url) {
  const next = [url, ...recentUrls.filter((item) => item.shortCode !== url.shortCode)].slice(0, 5);
  recentUrls.splice(0, recentUrls.length, ...next);
  renderRecent();
}

function renderRecent() {
  const recent = recentUrls;
  if (!recent.length) {
    recentLinks.innerHTML = '<p class="empty-state">Your newly shortened links will appear here.</p>';
    return;
  }
  recentLinks.innerHTML = recent.map((item) => `
    <article class="recent-item">
      <a href="#" data-code="${item.shortCode}">${shortUrl(item.shortCode)}</a>
      <p title="${item.url}">${item.url}</p>
      <small>${item.stats || 0} CLICKS</small>
    </article>
  `).join("");
}

function showResult(url) {
  activeUrl = url;
  resultPanel.hidden = false;
  document.querySelector("#result-code").textContent = `CODE ${url.shortCode}`;
  const link = document.querySelector("#short-link");
  link.textContent = shortUrl(url.shortCode);
  link.href = shortUrl(url.shortCode);
  document.querySelector("#destination").textContent = url.url;
  document.querySelector("#destination").title = url.url;
  document.querySelector("#stats").textContent = url.stats || 0;
  document.querySelector("#edit-url-input").value = url.url;
  editForm.hidden = true;
  resultPanel.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

shortenForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  setMessage(formMessage, "Creating...");
  const url = new FormData(shortenForm).get("url");
  try {
    const created = await request("/shorten", { method: "POST", body: JSON.stringify({ url }) });
    saveRecent(created);
    showResult(created);
    shortenForm.reset();
    setMessage(formMessage, "Created.");
  } catch (error) {
    setMessage(formMessage, error.message);
  }
});

lookupForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  setMessage(lookupMessage, "Looking up...");
  const code = new FormData(lookupForm).get("code").trim();
  try {
    const found = await request(`/search/${encodeURIComponent(code)}`);
    showResult(found);
    setMessage(lookupMessage, "Found.");
  } catch (error) {
    setMessage(lookupMessage, error.message);
  }
});

document.querySelector("#edit-button").addEventListener("click", () => {
  editForm.hidden = !editForm.hidden;
  if (!editForm.hidden) document.querySelector("#edit-url-input").focus();
});

editForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const url = document.querySelector("#edit-url-input").value;
  try {
    const updated = await request(`/shorten/${activeUrl.shortCode}`, { method: "PUT", body: JSON.stringify({ url }) });
    saveRecent(updated);
    showResult(updated);
  } catch (error) {
    setMessage(lookupMessage, error.message);
  }
});

document.querySelector("#delete-button").addEventListener("click", async () => {
  if (!activeUrl || !window.confirm(`Delete ${activeUrl.shortCode}?`)) return;
  try {
    await request(`/shorten/${activeUrl.shortCode}`, { method: "DELETE" });
    const recent = recentUrls.filter((item) => item.shortCode !== activeUrl.shortCode);
    recentUrls.splice(0, recentUrls.length, ...recent);
    renderRecent();
    resultPanel.hidden = true;
    setMessage(lookupMessage, "Link deleted.");
  } catch (error) {
    setMessage(lookupMessage, error.message);
  }
});

document.querySelector("#copy-button").addEventListener("click", async (event) => {
  await navigator.clipboard.writeText(shortUrl(activeUrl.shortCode));
  event.currentTarget.textContent = "Copied";
  setTimeout(() => { event.currentTarget.textContent = "Copy link"; }, 1400);
});

recentLinks.addEventListener("click", async (event) => {
  const link = event.target.closest("[data-code]");
  if (!link) return;
  event.preventDefault();
  document.querySelector("#code-input").value = link.dataset.code;
  lookupForm.requestSubmit();
});

renderRecent();
