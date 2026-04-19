import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

window.onload = function () {
    const adminBtn = document.getElementById("adminLogin");
    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            localStorage.setItem("userRole", "admin");
        });
    }

    const doctorBtn = document.getElementById("doctorLogin");
    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            localStorage.setItem("userRole", "doctor");
        });
    }

    const patientBtn = document.getElementById("patientLogin");
    if (patientBtn) {
        patientBtn.addEventListener("click", () => {
            localStorage.setItem("userRole", "patient");
        });
    }
};

window.adminLoginHandler = async function () {
    try {
        const username = document.getElementById("username")?.value;
        const password = document.getElementById("password")?.value;

        const admin = { username, password };

        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token || "");
            localStorage.setItem("userRole", "admin");
            window.location.href = `/adminDashboard/${data.token}`;
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Admin login error:", error);
        alert("Something went wrong during admin login.");
    }
};

window.doctorLoginHandler = async function () {
    try {
        const email = document.getElementById("email")?.value;
        const password = document.getElementById("password")?.value;

        const doctor = { email, password };

        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token || "");
            localStorage.setItem("userRole", "doctor");
            window.location.href = `/doctorDashboard/${data.token}`;
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Doctor login error:", error);
        alert("Something went wrong during doctor login.");
    }
};