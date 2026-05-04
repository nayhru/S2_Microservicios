package com.sumativa.ms_citas.citas.controller;

import com.sumativa.ms_citas.citas.model.CitaMedica;
import com.sumativa.ms_citas.citas.service.CitaMedicaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del controlador de Citas con MockMvc.
 * Validan el contrato HTTP y la presencia de enlaces HATEOAS.
 */
@WebMvcTest(CitaMedicaController.class)
@DisplayName("CitaMedicaController — pruebas con MockMvc")
class CitaMedicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CitaMedicaService service;

    private CitaMedica cita;

    @BeforeEach
    void prepararCita() {
        cita = new CitaMedica(
                1L, "Ana Torres", "Bobby", "Perro",
                "Dr. Rodríguez",
                LocalDate.now().plusDays(1),
                "09:00",
                "Vacunación anual",
                "PROGRAMADA",
                LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/citas devuelve la colección con bloques _links HATEOAS")
    void getCitas_devuelveColeccionConLinks() throws Exception {
        when(service.obtenerTodas()).thenReturn(List.of(cita));

        mockMvc.perform(get("/api/citas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.citaMedicaList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.citaMedicaList[0].paciente").value("Ana Torres"))
                .andExpect(jsonPath("$._embedded.citaMedicaList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.citaMedicaList[0]._links.cancelar.href").exists())
                .andExpect(jsonPath("$._embedded.citaMedicaList[0]._links.disponibilidad.href").exists());
    }

    @Test
    @DisplayName("GET /api/citas/{id} con id inexistente devuelve 404 y mensaje claro")
    void getCitaPorId_inexistente_devuelve404() throws Exception {
        when(service.obtenerPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/citas/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Cita con ID 999 no encontrada"));
    }
}
