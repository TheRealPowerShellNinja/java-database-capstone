package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> dtoList = appointments.stream().map(this::toDto).toList();
            response.put("appointments", dtoList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error fetching appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(Long patientId, String condition) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;
            if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);

            List<AppointmentDTO> dtoList = appointments.stream().map(this::toDto).toList();
            response.put("appointments", dtoList);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(Long patientId, String doctorName) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);

            List<AppointmentDTO> dtoList = appointments.stream().map(this::toDto).toList();
            response.put("appointments", dtoList);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(Long patientId,
                                                                          String doctorName,
                                                                          String condition) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;
            if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else {
                response.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);

            List<AppointmentDTO> dtoList = appointments.stream().map(this::toDto).toList();
            response.put("appointments", dtoList);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error fetching patient details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filter(String token, String condition, String doctorName) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if ((condition == null || condition.isBlank()) && (doctorName == null || doctorName.isBlank())) {
                return getPatientAppointment(patient.getId());
            }

            if (condition != null && !condition.isBlank() && doctorName != null && !doctorName.isBlank()) {
                return filterByDoctorAndCondition(patient.getId(), doctorName, condition);
            }

            if (condition != null && !condition.isBlank()) {
                return filterByCondition(patient.getId(), condition);
            }

            return filterByDoctor(patient.getId(), doctorName);

        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private AppointmentDTO toDto(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : null,
                appointment.getPatient() != null ? appointment.getPatient().getId() : null,
                appointment.getPatient() != null ? appointment.getPatient().getName() : null,
                appointment.getPatient() != null ? appointment.getPatient().getEmail() : null,
                appointment.getPatient() != null ? appointment.getPatient().getPhone() : null,
                appointment.getPatient() != null ? appointment.getPatient().getAddress() : null,
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}