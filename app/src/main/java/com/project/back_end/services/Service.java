package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public boolean validateToken(String token, String role) {
        return tokenService.validateToken(token, role);
    }

    public ResponseEntity<Map<String, Object>> validateAdmin(Admin admin) {
        Map<String, Object> response = new HashMap<>();

        try {
            Admin existing = adminRepository.findByUsername(admin.getUsername());

            if (existing == null || !existing.getPassword().equals(admin.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(existing.getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> validatePatientLogin(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null || !patient.getPassword().equals(password)) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    public List<Doctor> filterDoctor(String name, String specialty, String time) {

        List<Doctor> doctors;

        if (name != null && specialty != null) {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (name != null) {
            doctors = doctorRepository.findByNameContainingIgnoreCase(name);
        } else if (specialty != null) {
            doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        } else {
            doctors = doctorRepository.findAll();
        }

        if (time == null) return doctors;

        List<Doctor> filtered = new ArrayList<>();

        for (Doctor doctor : doctors) {
            if (doctor.getAvailableTimes() == null) continue;

            for (String slot : doctor.getAvailableTimes()) {
                if (time.equalsIgnoreCase("am") && slot.compareTo("12:00") < 0) {
                    filtered.add(doctor);
                    break;
                }
                if (time.equalsIgnoreCase("pm") && slot.compareTo("12:00") >= 0) {
                    filtered.add(doctor);
                    break;
                }
            }
        }

        return filtered;
    }

    public boolean validateAppointment(Long doctorId, String timeSlot) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return false;

        Doctor doctor = doctorOpt.get();
        return doctor.getAvailableTimes() != null && doctor.getAvailableTimes().contains(timeSlot);
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String token, String condition, String doctorName) {
        return patientService.filter(token, condition, doctorName);
    }
}