# Smart Clinic Schema Design

## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- full_name: VARCHAR(100), Not Null
- email: VARCHAR(100), Not Null, Unique
- password: VARCHAR(255), Not Null
- phone: VARCHAR(20), Not Null, Unique
- date_of_birth: DATE, Not Null
- gender: VARCHAR(10), Not Null
- address: VARCHAR(255), Null

### Table: doctors
- id: INT, Primary Key, Auto Increment
- full_name: VARCHAR(100), Not Null
- email: VARCHAR(100), Not Null, Unique
- specialization: VARCHAR(100), Not Null
- phone: VARCHAR(20), Not Null, Unique
- availability: VARCHAR(255), Null

### Table: appointments
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id), Not Null
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- appointment_time: DATETIME, Not Null
- status: INT, Not Null  
  <!-- 0 = Scheduled, 1 = Completed, 2 = Cancelled -->

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Not Null, Unique
- password: VARCHAR(255), Not Null
- role: VARCHAR(20), Not Null

<!-- Design Notes:
- MySQL is used for structured and relational data such as patients, doctors, appointments, and admin users.
- The appointments table links patients and doctors through foreign keys.
- Unique constraints on email and phone help prevent duplicate records.
- Appointment status is stored as an integer for simple status tracking.
-->

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "duration": "5 days"
    }
  ],
  "doctorNotes": "Take 1 tablet every 6 hours after meals.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  },
  "tags": ["pain-relief", "fever"],
  "createdAt": "2026-04-18T10:30:00Z"
}
```

<!-- Design Notes:
- MongoDB is used for flexible prescription data that may vary from one appointment to another.
- The medications field uses an array to support multiple prescribed medicines in one document.
- The pharmacy field is embedded as a nested object for convenience.
- Patient and doctor are referenced by ID instead of embedding full objects, which keeps the document smaller and easier to maintain.
-->
