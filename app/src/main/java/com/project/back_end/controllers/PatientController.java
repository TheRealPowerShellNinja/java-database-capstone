package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @RequestBody Patient patient) {
        Map<String, Object> response = new HashMap<>();

        if (!service.validatePatient(patient)) {
            response.put("message", "Patient already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put("message", "Patient created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Error creating patient");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login.getEmail(), login.getPassword());
    }

    @GetMapping("/appointments/{patientId}/{user}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long patientId,
                                                                     @PathVariable String user,
                                                                     @PathVariable String token) {
        if (!service.validateToken(token, user)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return patientService.getPatientAppointment(patientId);
    }

    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                        @PathVariable String name,
                                                                        @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String normalizedCondition = "null".equalsIgnoreCase(condition) ? null : condition;
        String normalizedName = "null".equalsIgnoreCase(name) ? null : name;

        return service.filterPatient(token, normalizedCondition, normalizedName);
    }
}