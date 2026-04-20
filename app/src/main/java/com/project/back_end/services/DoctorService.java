package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Doctor doctor = doctorOpt.get();
        List<String> availableTimes = doctor.getAvailableTimes() == null
                ? new ArrayList<>()
                : new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        Set<String> bookedSlots = appointments.stream()
                .map(a -> {
                    int hour = a.getAppointmentTime().getHour();
                    return String.format("%02d:00-%02d:00", hour, hour + 1);
                })
                .collect(Collectors.toSet());

        return availableTimes.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(Long doctorId) {
        try {
            if (!doctorRepository.existsById(doctorId)) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> validateDoctor(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            Doctor doctor = doctorRepository.findByEmail(email);

            if (doctor == null || !doctor.getPassword().equals(password)) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {
        return filterDoctorByTime(
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty),
                time
        );
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String time) {
        if (time == null || time.isBlank()) {
            return doctors;
        }

        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes() != null)
                .filter(doctor -> doctor.getAvailableTimes().stream().anyMatch(slot -> matchesTime(slot, time)))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
        return filterDoctorByTime(doctorRepository.findByNameContainingIgnoreCase(name), time);
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpecility(String specialty, String time) {
        return filterDoctorByTime(doctorRepository.findBySpecialtyIgnoreCase(specialty), time);
    }

    @Transactional
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorsByTime(String time) {
        return filterDoctorByTime(doctorRepository.findAll(), time);
    }

    private boolean matchesTime(String slot, String time) {
        if (slot == null || slot.length() < 2) {
            return false;
        }

        try {
            int hour = Integer.parseInt(slot.substring(0, 2));
            if ("am".equalsIgnoreCase(time)) {
                return hour < 12;
            }
            if ("pm".equalsIgnoreCase(time)) {
                return hour >= 12;
            }
        } catch (Exception ignored) {
        }

        return false;
    }
}