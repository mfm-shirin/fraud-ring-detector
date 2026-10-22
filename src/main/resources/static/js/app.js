// Shared dashboard elements
const transactionsTable = document.getElementById("transactionsTable");
const alertsTable = document.getElementById("alertsTable");

const transactionForm = document.getElementById("transactionForm");
const submitTransactionButton = document.getElementById("submitTransactionButton");
const formMessage = document.getElementById("formMessage");


// Safely display data in the dashboard
function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, function (char) {
        return {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        }[char];
    });
}


// Format amounts as Indian Rupees
function formatAmount(amount) {
    const value = Number(amount);

    if (!Number.isFinite(value)) {
        return "—";
    }

    return value.toLocaleString("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2
    });
}


// Format timestamps
function formatDate(value) {
    if (!value) {
        return "—";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return escapeHtml(value);
    }

    return date.toLocaleString("en-IN");
}


// Display risk levels with coloured badges
function riskBadge(risk) {
    const value = String(risk ?? "UNKNOWN").toUpperCase();

    let cssClass = "badge-medium";

    if (value === "HIGH") {
        cssClass = "badge-high";
    }

    if (value === "LOW") {
        cssClass = "badge-low";
    }

    return `<span class="badge ${cssClass}">
                ${escapeHtml(value)}
            </span>`;
}


// Fetch JSON data from the backend
async function fetchJson(url) {
    const response = await fetch(url, {
        cache: "no-store"
    });

    if (!response.ok) {
        throw new Error(`${url} returned HTTP ${response.status}`);
    }

    return response.json();
}

async function loadDashboard() {
    const connectionStatus = document.getElementById("connectionStatus");

    try {
        // Check the current user's role using the authenticated session.
        const userResponse = await fetch("/api/auth/me", {
            credentials: "same-origin"
        });

        if (!userResponse.ok) {
            throw new Error(
                `Could not identify the current user: HTTP ${userResponse.status}`
            );
        }

        const currentUser = await userResponse.json();
        const roles = currentUser.roles || [];
        
        const username = currentUser.username || "User";

        const usernameElement = document.getElementById("currentUsername");
        const avatarElement = document.getElementById("profileAvatar");
        const dropdownUsername = document.getElementById("dropdownUsername");
        const dropdownRole = document.getElementById("dropdownRole");

        if (usernameElement) {
            usernameElement.textContent = username;
        }

        if (avatarElement) {
            avatarElement.textContent = username.charAt(0).toUpperCase();
        }

        if (dropdownUsername) {
            dropdownUsername.textContent = username;
        }

        if (dropdownRole) {
            dropdownRole.textContent = roles
                .map(role => role.replace("ROLE_", ""))
                .join(" / ");
        }

        // Fetch the CSRF token required for secure logout.
        const csrfResponse = await fetch("/api/csrf", {
            credentials: "same-origin",
            cache: "no-store"
        });

        if (csrfResponse.ok) {
            const csrfData = await csrfResponse.json();

            const csrfInput = document.getElementById("logoutCsrfToken");

            if (csrfInput) {
                csrfInput.value = csrfData.token;
            }
        }


        const isAdmin = roles.includes("ROLE_ADMIN");
        const isAnalyst = roles.includes("ROLE_ANALYST");
        const canViewTransactions = isAdmin || isAnalyst;
        const canViewAlerts = isAdmin || isAnalyst;

        let transactions = [];
        let alerts = [];

        // Fetch only the data this role is allowed to access.
        const requests = [];

        if (canViewTransactions) {
            requests.push(
                fetchJson("/api/transactions").then(data => {
                    transactions = data;
                })
            );
        }

        if (canViewAlerts) {
            requests.push(
                fetchJson("/api/fraud-alerts").then(data => {
                    alerts = data;
                })
            );
        }

        await Promise.all(requests);

        connectionStatus.textContent = "Backend Connected";

        document.getElementById("totalTransactions").textContent =
            canViewTransactions ? transactions.length : "Restricted";

        document.getElementById("totalAlerts").textContent =
            canViewAlerts ? alerts.length : "Restricted";

        document.getElementById("highRiskAlerts").textContent =
            canViewAlerts
                ? alerts.filter(alert =>
                    String(alert.riskLevel).toUpperCase() === "HIGH"
                ).length
                : "Restricted";

        if (canViewAlerts) {
            renderAlerts(alerts);
        } else {
            const alertsTable = document.getElementById("alertsTable");

            if (alertsTable) {
                alertsTable.innerHTML = `
                    <tr>
                        <td colspan="6" class="message">
                            Fraud alerts are not available for your role.
                        </td>
                    </tr>
                `;
            }
        }

        if (canViewTransactions) {
            renderTransactions(transactions);
        } else {
            const transactionsTable =
                document.getElementById("transactionsTable");

            if (transactionsTable) {
                transactionsTable.innerHTML = `
                    <tr>
                        <td colspan="5" class="message">
                            Transaction records are not available for your role.
                        </td>
                    </tr>
                `;
            }
        }

        const now = new Date().toLocaleTimeString("en-IN");

        const alertUpdate = document.getElementById("alertUpdate");
        if (alertUpdate) {
            alertUpdate.textContent = canViewAlerts
                ? `${alerts.length} alert(s)`
                : "Restricted";
        }

        const transactionUpdate =
            document.getElementById("transactionUpdate");

        if (transactionUpdate) {
            transactionUpdate.textContent = canViewTransactions
                ? `${transactions.length} transaction(s)`
                : "Restricted";
        }

        const lastUpdated = document.getElementById("lastUpdated");
        if (lastUpdated) {
            lastUpdated.textContent = `Last updated ${now}`;
        }

    } catch (error) {
        console.error("Dashboard loading error:", error);

        connectionStatus.textContent = "Connection Error";
    }
}
