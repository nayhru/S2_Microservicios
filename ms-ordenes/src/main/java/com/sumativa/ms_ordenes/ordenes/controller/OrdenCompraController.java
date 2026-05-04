package com.sumativa.ms_ordenes.ordenes.controller;

import com.sumativa.ms_ordenes.ordenes.model.OrdenCompra;
import com.sumativa.ms_ordenes.ordenes.service.OrdenCompraService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Microservicio 1: Gestión de Órdenes de Compra (Cuidado de Mascotitas)
 *
 * Endpoints disponibles:
 *   GET    /api/ordenes              - Consultar todas las órdenes
 *   GET    /api/ordenes/{id}         - Consultar una orden por ID
 *   GET    /api/ordenes/{id}/estado  - Consultar el estado de una orden
 *   POST   /api/ordenes              - Crear una nueva orden
 *   PUT    /api/ordenes/{id}/estado  - Actualizar el estado de una orden
 *   DELETE /api/ordenes/{id}         - Eliminar una orden
 *
 * Las respuestas incluyen vínculos HATEOAS bajo el bloque "_links".
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenCompraController {

    private final OrdenCompraService service;

    public OrdenCompraController(OrdenCompraService service) {
        this.service = service;
    }

    @GetMapping
    public CollectionModel<EntityModel<OrdenCompra>> obtenerTodas() {
        List<EntityModel<OrdenCompra>> ordenes = service.obtenerTodas().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(ordenes,
                linkTo(methodOn(OrdenCompraController.class).obtenerTodas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<OrdenCompra> orden = service.obtenerPorId(id);
        if (orden.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Orden con ID " + id + " no encontrada"));
        }
        return ResponseEntity.ok(toModel(orden.get()));
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<?> obtenerEstado(@PathVariable Long id) {
        Optional<Map<String, Object>> estado = service.obtenerEstado(id);
        if (estado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Orden con ID " + id + " no encontrada"));
        }
        EntityModel<Map<String, Object>> modelo = EntityModel.of(estado.get(),
                linkTo(methodOn(OrdenCompraController.class).obtenerEstado(id)).withSelfRel(),
                linkTo(methodOn(OrdenCompraController.class).obtenerPorId(id)).withRel("orden"),
                linkTo(methodOn(OrdenCompraController.class).obtenerTodas()).withRel("ordenes"));
        return ResponseEntity.ok(modelo);
    }

    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody OrdenCompra orden) {
        Map<String, Object> resultado = service.crearOrden(orden);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
        OrdenCompra creada = (OrdenCompra) resultado.get("orden");
        Map<String, Object> respuesta = new HashMap<>(resultado);
        respuesta.put("orden", toModel(creada));
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id,
                                              @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "El campo 'estado' es obligatorio en el cuerpo de la petición"));
        }

        Map<String, Object> resultado = service.actualizarEstado(id, nuevoEstado);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            String msg = (String) resultado.get("mensaje");
            HttpStatus status = (msg != null && msg.contains("no encontrada"))
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(resultado);
        }
        OrdenCompra actualizada = (OrdenCompra) resultado.get("orden");
        Map<String, Object> respuesta = new HashMap<>(resultado);
        respuesta.put("orden", toModel(actualizada));
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOrden(@PathVariable Long id) {
        Map<String, Object> resultado = service.eliminarOrden(id);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
        EntityModel<Map<String, Object>> respuesta = EntityModel.of(resultado,
                linkTo(methodOn(OrdenCompraController.class).obtenerTodas()).withRel("ordenes"));
        return ResponseEntity.ok(respuesta);
    }

    private EntityModel<OrdenCompra> toModel(OrdenCompra orden) {
        return EntityModel.of(orden,
                linkTo(methodOn(OrdenCompraController.class).obtenerPorId(orden.getId())).withSelfRel(),
                linkTo(methodOn(OrdenCompraController.class).obtenerTodas()).withRel("ordenes"),
                linkTo(methodOn(OrdenCompraController.class).obtenerEstado(orden.getId())).withRel("estado"),
                linkTo(methodOn(OrdenCompraController.class).actualizarEstado(orden.getId(), null)).withRel("actualizar-estado"),
                linkTo(methodOn(OrdenCompraController.class).eliminarOrden(orden.getId())).withRel("eliminar"));
    }
}
