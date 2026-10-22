document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const togglePassword = document.getElementById("togglePassword");
    const loginButton = document.getElementById("loginButton");
    const buttonText = document.getElementById("buttonText");
    const loginMessage = document.getElementById("loginMessage");

    // Show or hide the password.
    togglePassword.addEventListener("click", () => {
        const showPassword = passwordInput.type === "password";

        passwordInput.type = showPassword ? "text" : "password";
        togglePassword.textContent = showPassword ? "HIDE" : "SHOW";

        togglePassword.setAttribute(
            "aria-label",
            showPassword ? "Hide password" : "Show password"
        );
    });

    function showMessage(message, type) {
        loginMessage.textContent = message;
        loginMessage.className = `login-message ${type}`;
    }

    // Handle Spring Security login.
    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const username = usernameInput.value.trim();
        const password = passwordInput.value;

        if (!username || !password) {
            showMessage(
                "Please enter your username and password.",
                "error"
            );
            return;
        }

        loginButton.disabled = true;
        buttonText.textContent = "Authenticating...";
        loginMessage.textContent = "";
        loginMessage.className = "login-message";

        try {
            const formData = new URLSearchParams();
            formData.set("username", username);
            formData.set("password", password);

            const response = await fetch("/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: formData.toString(),
                credentials: "same-origin",
                redirect: "follow"
            });

            const finalUrl = new URL(response.url, window.location.origin);

            // Spring Security redirects failed logins to ?error.
            if (
                finalUrl.pathname.endsWith("/login.html") &&
                finalUrl.searchParams.has("error")
            ) {
                showMessage(
                    "Invalid username or password. Please try again.",
                    "error"
                );
                passwordInput.value = "";
                passwordInput.focus();
                return;
            }

            // Navigate only after a successful authentication redirect.
            if (
                response.redirected &&
                response.ok &&
                finalUrl.pathname.endsWith("/index.html")
            ) {
                window.location.replace("/index.html");
                return;
            }

            // Handle unexpected responses without assuming login succeeded.
            if (response.status === 401 || response.status === 403) {
                showMessage(
                    "Login was rejected. Check your credentials and try again.",
                    "error"
                );
                return;
            }

            showMessage(
                `Login could not be completed (HTTP ${response.status}). Please try again.`,
                "error"
            );

        } catch (error) {
            console.error("Login request failed:", error);

            showMessage(
                "Unable to contact the server. Check that Spring Boot is running.",
                "error"
            );

        } finally {
            loginButton.disabled = false;
            buttonText.textContent = "Sign In";
        }
    });

    // Display an error if Spring Security redirects back with ?error.
    const queryParams = new URLSearchParams(window.location.search);

    if (queryParams.has("error")) {
        showMessage(
            "Invalid username or password. Please try again.",
            "error"
        );
    }

    // Display a message after logout.
    if (queryParams.has("logout")) {
        showMessage(
            "You have been logged out successfully.",
            "success"
        );
    }
});