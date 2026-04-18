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
