package com.sumativa.ms_ordenes.ordenes.controller;

import com.sumativa.ms_ordenes.ordenes.model.OrdenCompra;
import com.sumativa.ms_ordenes.ordenes.service.OrdenCompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del controlador de Órdenes con MockMvc.
 * Validan el contrato HTTP y la presencia de enlaces HATEOAS en las respuestas.
 */
@WebMvcTest(OrdenCompraController.class)
@DisplayName("OrdenCompraController — pruebas con MockMvc")
class OrdenCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrdenCompraService service;

    private OrdenCompra orden;

    @BeforeEach
    void prepararOrden() {
        orden = new OrdenCompra(
                1L, "Juan Pérez", "Firulais", "Perro",
                List.of("Alimento premium canino 5kg"),
                25000.0, "PENDIENTE", LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/ordenes devuelve la colección con bloques _links HATEOAS")
    void getOrdenes_devuelveColeccionConLinks() throws Exception {
        when(service.obtenerTodas()).thenReturn(List.of(orden));

        mockMvc.perform(get("/api/ordenes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.ordenCompraList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.ordenCompraList[0].cliente").value("Juan Pérez"))
                .andExpect(jsonPath("$._embedded.ordenCompraList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.ordenCompraList[0]._links.estado.href").exists())
                .andExpect(jsonPath("$._embedded.ordenCompraList[0]._links.eliminar.href").exists());
    }

    @Test
    @DisplayName("GET /api/ordenes/{id} con id inexistente devuelve 404 y mensaje claro")
    void getOrdenPorId_inexistente_devuelve404() throws Exception {
        when(service.obtenerPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ordenes/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Orden con ID 999 no encontrada"));
    }
}
