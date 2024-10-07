package com.medizine.backend.repositoryservices;

import com.medizine.backend.dto.Doctor;
import com.medizine.backend.dto.Status;
import com.medizine.backend.exchanges.DoctorPatchRequest;
import com.medizine.backend.repositories.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.inject.Provider;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


class DoctorRepositoryServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private Provider<ModelMapper> modelMapperProvider;

    @InjectMocks
    private DoctorRepositoryServiceImpl doctorRepositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDoctor_Success() {
        Doctor doctor = new Doctor();
        doctor.setPhoneNumber("123456789");
        doctor.setCountryCode("+1");

        when(doctorRepository.findAll()).thenReturn(emptyList());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        ResponseEntity<?> response = doctorRepositoryService.createDoctor(doctor);

        assertEquals(ResponseEntity.ok(doctor), response);
        verify(doctorRepository, times(1)).save(doctor);
    }



    @Test
    void testUpdateDoctorById_Success() {
        String doctorId = "1";
        Doctor existingDoctor = new Doctor();
        existingDoctor.id ="1";
        existingDoctor.setPhoneNumber("123456789");
        existingDoctor.setCountryCode("+1");
        existingDoctor.setName("John Doe");

        // Simula que existe un doctor con ese ID
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));

        // Doctor que queremos usar para la actualización (solo con el email)
        Doctor updatedDoctor = Doctor.builder()
                .emailAddress("updated@example.com")
                .build();

        // Llama al método para actualizar el doctor
        ResponseEntity<?> response = doctorRepositoryService.updateDoctorById(doctorId, updatedDoctor);

        // Verifica que el estado sea 200 y que solo el email haya sido actualizado
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("updated@example.com", ((Doctor) response.getBody()).getEmailAddress());

        // También puedes verificar que los otros campos se mantuvieron
        assertEquals("John Doe", ((Doctor) response.getBody()).getName());
        assertEquals("123456789", ((Doctor) response.getBody()).getPhoneNumber());
        assertEquals("+1", ((Doctor) response.getBody()).getCountryCode());
    }

    @Test
    void testUpdateDoctorById_NotFound() {
        Doctor updatedDoctor = new Doctor();

        when(doctorRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorRepositoryService.updateDoctorById("1", updatedDoctor);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testDeleteDoctorById_Success() {
        Doctor doctor = new Doctor();
        doctor.id = "1";
        doctor.setStatus(Status.ACTIVE);

        when(doctorRepository.findById("1")).thenReturn(Optional.of(doctor));

        ResponseEntity<?> response = doctorRepositoryService.deleteDoctorById("1");

        assertEquals(ResponseEntity.ok().build(), response);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void testDeleteDoctorById_NotFound() {
        when(doctorRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorRepositoryService.deleteDoctorById("1");

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testGetDoctorById_Success() {
        Doctor doctor = new Doctor();
        doctor.id = "1";
        doctor.setStatus(Status.ACTIVE);

        when(doctorRepository.findById("1")).thenReturn(Optional.of(doctor));

        ResponseEntity<?> response = doctorRepositoryService.getDoctorById("1");

        assertEquals(ResponseEntity.ok(doctor), response);
    }

    @Test
    void testGetDoctorById_NotFound() {
        when(doctorRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorRepositoryService.getDoctorById("1");

        assertEquals(ResponseEntity.noContent().build(), response);
    }
}
