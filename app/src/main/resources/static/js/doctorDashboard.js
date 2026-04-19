import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0];
const token = localStorage.getItem("token");
let patientName = "null";

async function loadAppointments() {
    if (!tableBody) return;

    try {
        const response = await getAllAppointments(selectedDate, patientName, token);
        const appointments = response.appointments || response || [];

        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">No Appointments found for today.</td>
                </tr>
            `;
            return;
        }

        appointments.forEach((appointment) => {
            const patient = {
                id: appointment.patient?.id,
                name: appointment.patient?.name,
                phone: appointment.patient?.phone,
                email: appointment.patient?.email
            };

            const row = createPatientRow(
                patient,
                appointment.id,
                appointment.doctor?.id
            );

            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error("Error loading appointments:", error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="5">Error loading appointments. Try again later.</td>
            </tr>
        `;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const searchBar = document.getElementById("searchBar");
    const todayButton = document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");

    if (datePicker) {
        datePicker.value = selectedDate;
    }

    searchBar?.addEventListener("input", (e) => {
        const value = e.target.value.trim();
        patientName = value !== "" ? value : "null";
        loadAppointments();
    });

    todayButton?.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split("T")[0];
        if (datePicker) {
            datePicker.value = selectedDate;
        }
        loadAppointments();
    });

    datePicker?.addEventListener("change", (e) => {
        selectedDate = e.target.value;
        loadAppointments();
    });

    loadAppointments();
});