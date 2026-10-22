function renderAlerts(alerts) {
    alertsTable.innerHTML = alerts.length
        ? alerts.map(alert => `
            <tr>
                <td>${escapeHtml(alert.accountId)}</td>
                <td class="type">
                    ${escapeHtml(alert.alertType)}
                </td>
                <td>${escapeHtml(alert.description)}</td>
                <td>${formatAmount(alert.outgoingAmount)}</td>
                <td>${riskBadge(alert.riskLevel)}</td>
                <td>${formatDate(alert.detectedAt)}</td>
            </tr>
        `).join("")
        : `
            <tr>
                <td colspan="6" class="message">
                    No alerts detected.
                </td>
            </tr>
        `;
}