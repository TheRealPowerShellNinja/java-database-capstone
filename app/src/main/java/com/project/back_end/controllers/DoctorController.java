package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user,
                                                                     @PathVariable Long doctorId,
                                                                     @PathVariable String date,
                                                                     @PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<String> availability = doctorService.getDoctorAvailability(doctorId, LocalDate.parse(date));
        response.put("availableTimes", availability);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(@Valid @RequestBody Doctor doctor,
                                                          @PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == -1) {
            response.put("message", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        if (result == 1) {
            response.put("message", "Doctor added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Error saving doctor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login.getEmail(), login.getPassword());
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(@Valid @RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (result == 1) {
            response.put("message", "Doctor updated successfully");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Error updating doctor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @DeleteMapping("/{doctorId}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable Long doctorId,
                                                            @PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        int result = doctorService.deleteDoctor(doctorId);
        if (result == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Error deleting doctor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(@PathVariable String name,
                                                      @PathVariable String time,
                                                      @PathVariable String speciality) {
        Map<String, Object> response = new HashMap<>();

        String normalizedName = "null".equalsIgnoreCase(name) ? null : name;
        String normalizedTime = "null".equalsIgnoreCase(time) ? null : time;
        String normalizedSpeciality = "null".equalsIgnoreCase(speciality) ? null : speciality;

        response.put("doctors", service.filterDoctor(normalizedName, normalizedSpeciality, normalizedTime));
        return ResponseEntity.ok(response);
    }
}