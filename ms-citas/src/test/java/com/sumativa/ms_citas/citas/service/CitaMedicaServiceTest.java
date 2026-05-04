package com.sumativa.ms_citas.citas.service;

import com.sumativa.ms_citas.citas.model.CitaMedica;
import com.sumativa.ms_citas.citas.repository.CitaMedicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de Citas Médicas usando Mockito.
 * No requieren base de datos ni contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CitaMedicaService — pruebas unitarias")
class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository repository;

    @InjectMocks
    private CitaMedicaService service;

    private CitaMedica citaValida;

    @BeforeEach
    void prepararCitaValida() {
        citaValida = new CitaMedica();
        citaValida.setPaciente("Maria Ovalle");
        citaValida.setMascota("Luna");
        citaValida.setTipoMascota("Gato");
        citaValida.setVeterinario("Dr. Rodríguez");
        citaValida.setFecha(LocalDate.now().plusDays(2));
        citaValida.setHora("10:00");
        citaValida.setMotivo("Vacunación anual");
    }

    @Test
    @DisplayName("programarCita falla cuando el horario del veterinario ya está reservado")
    void programarCita_conHorarioOcupado_falla() {
        when(repository.existsByFechaAndHoraAndVeterinarioIgnoreCaseAndEstado(
                any(LocalDate.class), anyString(), anyString(), eq("PROGRAMADA")))
                .thenReturn(true);

        Map<String, Object> resultado = service.programarCita(citaValida);

        assertThat(resultado.get("exito")).isEqualTo(false);
        @SuppressWarnings("unchecked")
        List<String> errores = (List<String>) resultado.get("errores");
        assertThat(errores).isNotEmpty();
        assertThat(errores.get(0)).contains("ya está reservado");

        verify(repository, never()).save(any(CitaMedica.class));
    }

    @Test
    @DisplayName("cancelarCita falla cuando la cita ya fue completada")
    void cancelarCita_yaCompletada_falla() {
        CitaMedica completada = new CitaMedica();
        completada.setId(7L);
        completada.setEstado("COMPLETADA");

        when(repository.findById(7L)).thenReturn(Optional.of(completada));

        Map<String, Object> resultado = service.cancelarCita(7L);

        assertThat(resultado.get("exito")).isEqualTo(false);
        assertThat((String) resultado.get("mensaje")).contains("completada");

        verify(repository, never()).save(any(CitaMedica.class));
    }
}
