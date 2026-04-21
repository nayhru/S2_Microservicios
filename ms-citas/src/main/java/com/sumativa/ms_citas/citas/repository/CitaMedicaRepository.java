package com.sumativa.ms_citas.citas.repository;

import com.sumativa.ms_citas.citas.model.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    List<CitaMedica> findByEstado(String estado);

    List<CitaMedica> findByFechaAndEstado(LocalDate fecha, String estado);

    List<CitaMedica> findByFechaAndVeterinarioIgnoreCaseAndEstado(LocalDate fecha, String veterinario, String estado);

    boolean existsByFechaAndHoraAndVeterinarioIgnoreCaseAndEstado(
            LocalDate fecha, String hora, String veterinario, String estado);
}
