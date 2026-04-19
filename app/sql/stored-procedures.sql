DELIMITER $$

CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN report_date DATE)
BEGIN
    SELECT 
        d.id AS doctor_id,
        d.name AS doctor_name,
        COUNT(a.id) AS total_appointments
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    WHERE DATE(a.appointment_time) = report_date
    GROUP BY d.id, d.name;
END $$

CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(IN input_month INT, IN input_year INT)
BEGIN
    SELECT 
        d.id AS doctor_id,
        d.name AS doctor_name,
        COUNT(DISTINCT a.patient_id) AS patients_seen
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    WHERE MONTH(a.appointment_time) = input_month
      AND YEAR(a.appointment_time) = input_year
    GROUP BY d.id, d.name
    ORDER BY patients_seen DESC
    LIMIT 1;
END $$

CREATE PROCEDURE GetDoctorWithMostPatientsByYear(IN input_year INT)
BEGIN
    SELECT 
        d.id AS doctor_id,
        d.name AS doctor_name,
        COUNT(DISTINCT a.patient_id) AS patients_seen
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    WHERE YEAR(a.appointment_time) = input_year
    GROUP BY d.id, d.name
    ORDER BY patients_seen DESC
    LIMIT 1;
END $$

DELIMITER ;