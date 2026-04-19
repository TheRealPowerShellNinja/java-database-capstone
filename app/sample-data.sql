-- Insert Admin
INSERT INTO admin (username, password)
VALUES ('admin1', 'admin123');

-- Insert Doctors
INSERT INTO doctor (name, specialty, email, password, phone)
VALUES 
('Dr. John Doe', 'Cardiology', 'john@example.com', 'password123', '1234567890'),
('Dr. Jane Smith', 'Dermatology', 'jane@example.com', 'password123', '0987654321');

-- Insert Doctor Availability
INSERT INTO doctor_available_times (doctor_id, available_times)
VALUES 
(1, '09:00-10:00'),
(1, '10:00-11:00'),
(2, '13:00-14:00'),
(2, '14:00-15:00');

-- Insert Patients
INSERT INTO patient (name, email, password, phone, address)
VALUES 
('Alice Brown', 'alice@example.com', 'password123', '1112223333', '123 Main St'),
('Bob White', 'bob@example.com', 'password123', '4445556666', '456 Elm St');

-- Insert Appointments
INSERT INTO appointment (doctor_id, patient_id, appointment_time, status)
VALUES 
(1, 1, NOW() + INTERVAL 1 DAY, 0),
(2, 2, NOW() + INTERVAL 2 DAY, 0);
