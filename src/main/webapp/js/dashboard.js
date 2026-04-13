// Wait until the HTML is fully loaded
document.addEventListener("DOMContentLoaded", () => {
    console.log("Dashboard JS loaded ✅");

    // Select all sidebar links
    const navLinks = document.querySelectorAll(".nav-links li");

    // Add active state switching
    navLinks.forEach(link => {
        link.addEventListener("click", () => {
            navLinks.forEach(l => l.classList.remove("active"));
            link.classList.add("active");
        });
    });

    // Display greeting message dynamically (can later be fetched from session)
    const userInfo = document.querySelector(".user-info");
    if (userInfo) {
        const username = "Admin"; // Placeholder (replace with dynamic session user)
        userInfo.innerHTML = `<span>Hello, ${username} 👋</span>`;
    }

    // Optional: Handle logout
    const logoutBtn = document.querySelector("#logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            alert("Logging out...");
            window.location.href = "login.html";
        });
    }
    
    
});
