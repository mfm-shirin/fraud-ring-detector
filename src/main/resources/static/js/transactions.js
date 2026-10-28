function renderTransactions(transactions) {
    transactionsTable.innerHTML = transactions.length
        ? transactions.map(transaction => `
            <tr>
                <td>${escapeHtml(transaction.transactionId)}</td>
                <td>${escapeHtml(transaction.fromAccount)}</td>
                <td>${escapeHtml(transaction.toAccount)}</td>
                <td>${formatAmount(transaction.amount)}</td>
                <td>${formatDate(transaction.timestamp)}</td>
            </tr>
        `).join("")
        : `
            <tr>
                <td colspan="5" class="message">
                    No transactions available.
                </td>
            </tr>
        `;
}


// Submit a transaction through the frontend
transactionForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const transaction = {
        transactionId: document
            .getElementById("transactionId")
            .value.trim(),

        fromAccount: document
            .getElementById("fromAccount")
            .value.trim(),

        toAccount: document
            .getElementById("toAccount")
            .value.trim(),

        amount: Number(
            document.getElementById("amount").value
        )
    };

    formMessage.textContent = "";
    formMessage.className = "form-message";

    submitTransactionButton.disabled = true;
    submitTransactionButton.textContent = "Adding...";

    try {
        // Spring Security needs a CSRF token on POST requests.
        const csrfResponse = await fetch("/api/csrf", {
            credentials: "same-origin",
            cache: "no-store"
        });

        if (csrfResponse.status === 401) {
            window.location.replace("/login.html");
            return;
        }

        if (!csrfResponse.ok) {
            throw new Error("Could not get a security token. Please refresh.");
        }

        const csrf = await csrfResponse.json();

        const response = await fetch("/api/transactions", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token
            },
            credentials: "same-origin",
            body: JSON.stringify(transaction)
        });

        if (!response.ok) {
            const errorBody = await response.text();

            throw new Error(
                errorBody || `Request failed (HTTP ${response.status})`
            );
        }

        const savedTransaction = await response.json();

        formMessage.textContent =
            `Transaction ${savedTransaction.transactionId} added successfully.`;

        formMessage.className = "form-message success";

        transactionForm.reset();

        await loadDashboard();

    } catch (error) {
        console.error("Transaction submission error:", error);

        formMessage.textContent =
            error.message ||
            "Could not add transaction. Please try again.";

        formMessage.className = "form-message error";

    } finally {
        submitTransactionButton.disabled = false;
        submitTransactionButton.textContent = "Add Transaction";
    }
});