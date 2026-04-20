package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                               @PathVariable String patientName,
                                                               @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String normalizedPatientName = "null".equalsIgnoreCase(patientName) ? null : patientName;
        return appointmentService.getAppointments(token, date, normalizedPatientName);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(@Valid @RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(token, "patient")) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            response.put("message", "Doctor is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (appointment.getAppointmentTime() == null) {
            response.put("message", "Appointment time is required");
            return ResponseEntity.badRequest().body(response);
        }

        String requestedSlot = String.format("%02d:00-%02d:00",
                appointment.getAppointmentTime().getHour(),
                appointment.getAppointmentTime().getHour() + 1);

        if (!service.validateAppointment(appointment.getDoctor().getId(), requestedSlot)) {
            response.put("message", "Selected slot is unavailable");
            return ResponseEntity.badRequest().body(response);
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            response.put("message", "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Error booking appointment");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(@Valid @RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return appointmentService.updateAppointment(appointment, token);
    }

    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable Long appointmentId,
                                                                 @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return appointmentService.cancelAppointment(appointmentId, token);
    }
}