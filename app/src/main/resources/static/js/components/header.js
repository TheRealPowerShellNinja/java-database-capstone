function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    let headerContent = `
        <header class="header">
            <div class="logo-section">
                <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>
            <nav>
    `;

    if (role === "admin" && token) {
        headerContent += `
            <button onclick="alert('Add Doctor clicked')">Add Doctor</button>
            <button onclick="logout()">Logout</button>
        `;
    } else if (role === "doctor" && token) {
        headerContent += `
            <button onclick="logout()">Logout</button>
        `;
    } else {
        headerContent += `
            <button onclick="alert('Login required')">Login</button>
        `;
    }

    headerContent += `
            </nav>
        </header>
    `;

    headerDiv.innerHTML = headerContent;
}

function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    window.location.href = "/";
}

renderHeader();