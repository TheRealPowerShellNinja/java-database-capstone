import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found.</p>";
        return;
    }

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

async function filterDoctorsOnChange() {
    try {
        const name = document.getElementById("searchBar")?.value || null;
        const time = document.getElementById("filterTime")?.value || null;
        const specialty = document.getElementById("filterSpecialty")?.value || null;

        const doctors = await filterDoctors(name, time, specialty);
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Something went wrong while filtering doctors.");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
    document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
    document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);
});