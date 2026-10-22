document.addEventListener("DOMContentLoaded", function () {
    const navigationButtons = document.querySelectorAll(".nav-item");
    const pageSections = document.querySelectorAll("[data-page]");

    function showTab(tabName) {
        // Hide sections that do not belong to the selected tab
        pageSections.forEach(section => {
            section.hidden = section.dataset.page !== tabName;
        });

        // Highlight the active navigation button
        navigationButtons.forEach(button => {
            const isActive = button.dataset.tab === tabName;

            button.classList.toggle("active", isActive);
            button.setAttribute("aria-current", isActive ? "page" : "false");
        });
    }

    navigationButtons.forEach(button => {
        button.addEventListener("click", function () {
            showTab(button.dataset.tab);
        });
    });

    // Profile dropdown functionality
    const profileButton = document.getElementById("profileButton");
    const profileDropdown = document.getElementById("profileDropdown");

    if (profileButton && profileDropdown) {
        profileButton.addEventListener("click", function (event) {
            event.stopPropagation();

            const isOpen = !profileDropdown.hidden;

            profileDropdown.hidden = isOpen;

            profileButton.setAttribute(
                "aria-expanded",
                String(!isOpen)
            );
        });

        // Close when clicking outside the dropdown
        document.addEventListener("click", function (event) {
            if (!event.target.closest(".profile-container")) {
                profileDropdown.hidden = true;

                profileButton.setAttribute(
                    "aria-expanded",
                    "false"
                );
            }
        });

        // Close when pressing Escape
        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape") {
                profileDropdown.hidden = true;

                profileButton.setAttribute(
                    "aria-expanded",
                    "false"
                );

                profileButton.focus();
            }
        });
    }

    // Start on the Overview tab
    showTab("overview");

    // Load dashboard data and refresh every five seconds
    loadDashboard();
    setInterval(loadDashboard, 5000);
});