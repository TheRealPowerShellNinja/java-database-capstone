import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);
        const data = await response.json();
        return data.doctors || data || [];
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: "DELETE"
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Doctor deleted."
        };
    } catch (error) {
        console.error("Error deleting doctor:", error);
        return {
            success: false,
            message: "Failed to delete doctor."
        };
    }
}

export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Doctor saved."
        };
    } catch (error) {
        console.error("Error saving doctor:", error);
        return {
            success: false,
            message: "Failed to save doctor."
        };
    }
}

export async function filterDoctors(name, time, specialty) {
    try {
        const safeName = name || "null";
        const safeTime = time || "null";
        const safeSpecialty = specialty || "null";

        const response = await fetch(`${DOCTOR_API}/filter/${safeName}/${safeTime}/${safeSpecialty}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            const data = await response.json();
            return data.doctors || data || [];
        } else {
            console.error("Failed to fetch filtered doctors:", response.statusText);
            return [];
        }
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Something went wrong!");
        return [];
    }
}