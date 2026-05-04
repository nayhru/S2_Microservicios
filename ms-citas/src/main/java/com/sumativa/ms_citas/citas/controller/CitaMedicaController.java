package com.sumativa.ms_citas.citas.controller;

import com.sumativa.ms_citas.citas.model.CitaMedica;
import com.sumativa.ms_citas.citas.service.CitaMedicaService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Microservicio 2: Reserva de Citas Médicas Veterinarias
 *
 * Endpoints disponibles:
 *   GET    /api/citas                                                       - Consultar todas las citas
 *   GET    /api/citas/{id}                                                  - Consultar una cita por ID
 *   GET    /api/citas/disponibilidad?fecha=YYYY-MM-DD[&veterinario=...]     - Consultar disponibilidad
 *   POST   /api/citas                                                       - Programar una nueva cita
 *   PUT    /api/citas/{id}/cancelar                                         - Cancelar una cita existente
 *   DELETE /api/citas/{id}                                                  - Eliminar una cita
 *
 * Las respuestas incluyen vínculos HATEOAS bajo el bloque "_links".
 */
@RestController
@RequestMapping("/api/citas")
public class CitaMedicaController {

    private final CitaMedicaService service;

    public CitaMedicaController(CitaMedicaService service) {
        this.service = service;
    }

    @GetMapping
    public CollectionModel<EntityModel<CitaMedica>> obtenerTodas() {
        List<EntityModel<CitaMedica>> citas = service.obtenerTodas().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(citas,
                linkTo(methodOn(CitaMedicaController.class).obtenerTodas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<CitaMedica> cita = service.obtenerPorId(id);
        if (cita.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Cita con ID " + id + " no encontrada"));
        }
        return ResponseEntity.ok(toModel(cita.get()));
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> consultarDisponibilidad(
            @RequestParam String fecha,
            @RequestParam(required = false) String veterinario) {
        try {
            LocalDate localDate = LocalDate.parse(fecha);
            Map<String, Object> resultado = service.consultarDisponibilidad(localDate, veterinario);

            EntityModel<Map<String, Object>> respuesta = EntityModel.of(resultado,
                    linkTo(methodOn(CitaMedicaController.class)
                            .consultarDisponibilidad(fecha, veterinario)).withSelfRel(),
                    linkTo(methodOn(CitaMedicaController.class).obtenerTodas()).withRel("citas"));
            return ResponseEntity.ok(respuesta);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "Formato de fecha inválido. Use el formato YYYY-MM-DD. Ejemplo: 2026-04-05"));
        }
    }

    @PostMapping
    public ResponseEntity<?> programarCita(@RequestBody CitaMedica cita) {
        Map<String, Object> resultado = service.programarCita(cita);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
        CitaMedica creada = (CitaMedica) resultado.get("cita");
        Map<String, Object> respuesta = new HashMap<>(resultado);
        respuesta.put("cita", toModel(creada));
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarCita(@PathVariable Long id) {
        Map<String, Object> resultado = service.cancelarCita(id);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            String msg = (String) resultado.get("mensaje");
            HttpStatus status = (msg != null && msg.contains("no encontrada"))
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(resultado);
        }
        CitaMedica cancelada = (CitaMedica) resultado.get("cita");
        Map<String, Object> respuesta = new HashMap<>(resultado);
        respuesta.put("cita", toModel(cancelada));
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCita(@PathVariable Long id) {
        Map<String, Object> resultado = service.eliminarCita(id);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
        EntityModel<Map<String, Object>> respuesta = EntityModel.of(resultado,
                linkTo(methodOn(CitaMedicaController.class).obtenerTodas()).withRel("citas"));
        return ResponseEntity.ok(respuesta);
    }

    private EntityModel<CitaMedica> toModel(CitaMedica cita) {
        String fechaStr = cita.getFecha() != null ? cita.getFecha().toString() : null;
        return EntityModel.of(cita,
                linkTo(methodOn(CitaMedicaController.class).obtenerPorId(cita.getId())).withSelfRel(),
                linkTo(methodOn(CitaMedicaController.class).obtenerTodas()).withRel("citas"),
                linkTo(methodOn(CitaMedicaController.class)
                        .consultarDisponibilidad(fechaStr, cita.getVeterinario())).withRel("disponibilidad"),
                linkTo(methodOn(CitaMedicaController.class).cancelarCita(cita.getId())).withRel("cancelar"),
                linkTo(methodOn(CitaMedicaController.class).eliminarCita(cita.getId())).withRel("eliminar"));
    }
}
