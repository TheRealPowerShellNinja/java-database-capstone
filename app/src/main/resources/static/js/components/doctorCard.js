export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name || "Unknown Doctor";

    const specialty = document.createElement("p");
    specialty.textContent = `Specialty: ${doctor.specialty || "N/A"}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email || "N/A"}`;

    const availability = document.createElement("p");
    const times = Array.isArray(doctor.availableTimes) ? doctor.availableTimes.join(", ") : "N/A";
    availability.textContent = `Availability: ${times}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialty);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.addEventListener("click", () => {
            alert("Delete requires backend integration.");
        });
        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient" || role === "loggedPatient") {
        const bookBtn = document.createElement("button");
        bookBtn.textContent = "Book Now";
        bookBtn.addEventListener("click", () => {
            alert("Please log in as a patient to book an appointment.");
        });
        actionsDiv.appendChild(bookBtn);
    }

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}