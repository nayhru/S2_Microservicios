package com.sumativa.ms_ordenes.ordenes.controller;

import com.sumativa.ms_ordenes.ordenes.model.OrdenCompra;
import com.sumativa.ms_ordenes.ordenes.service.OrdenCompraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Microservicio 1: Gestión de Órdenes de Compra (Cuidado de Mascotitas)
 *
 * Endpoints disponibles:
 *   GET    /api/ordenes            - Consultar todas las órdenes
 *   GET    /api/ordenes/{id}       - Consultar una orden por ID
 *   GET    /api/ordenes/{id}/estado - Consultar el estado de una orden
 *   POST   /api/ordenes            - Crear una nueva orden
 *   PUT    /api/ordenes/{id}/estado - Actualizar el estado de una orden
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenCompraController {

    private final OrdenCompraService service;

    public OrdenCompraController(OrdenCompraService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<OrdenCompra> orden = service.obtenerPorId(id);
        if (orden.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Orden con ID " + id + " no encontrada"));
        }
        return ResponseEntity.ok(orden.get());
    }

 
    @GetMapping("/{id}/estado")
    public ResponseEntity<?> obtenerEstado(@PathVariable Long id) {
        Optional<Map<String, Object>> estado = service.obtenerEstado(id);
        if (estado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Orden con ID " + id + " no encontrada"));
        }
        return ResponseEntity.ok(estado.get());
    }

    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody OrdenCompra orden) {
        Map<String, Object> resultado = service.crearOrden(orden);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
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
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOrden(@PathVariable Long id) {
        Map<String, Object> resultado = service.eliminarOrden(id);
        if (Boolean.FALSE.equals(resultado.get("exito"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
        return ResponseEntity.ok(resultado);
    }
}
