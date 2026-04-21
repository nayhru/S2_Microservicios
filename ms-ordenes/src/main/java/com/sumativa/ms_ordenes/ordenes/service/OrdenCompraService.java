package com.sumativa.ms_ordenes.ordenes.service;

import com.sumativa.ms_ordenes.ordenes.model.OrdenCompra;
import com.sumativa.ms_ordenes.ordenes.repository.OrdenCompraRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrdenCompraService {

    private final OrdenCompraRepository repository;

    private static final List<String> ESTADOS_VALIDOS =
            Arrays.asList("PENDIENTE", "EN_PROCESO", "COMPLETADA", "CANCELADA");

    public OrdenCompraService(OrdenCompraRepository repository) {
        this.repository = repository;
    }

    public List<OrdenCompra> obtenerTodas() {
        return repository.findAll();
    }

    public Optional<OrdenCompra> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    public Map<String, Object> crearOrden(OrdenCompra orden) {
        List<String> errores = new ArrayList<>();

        if (orden.getCliente() == null || orden.getCliente().isBlank()) {
            errores.add("El nombre del cliente es obligatorio");
        }
        if (orden.getMascota() == null || orden.getMascota().isBlank()) {
            errores.add("El nombre de la mascota es obligatorio");
        }
        if (orden.getTipoMascota() == null || orden.getTipoMascota().isBlank()) {
            errores.add("El tipo de mascota es obligatorio");
        }
        if (orden.getProductos() == null || orden.getProductos().isEmpty()) {
            errores.add("Debe incluir al menos un producto");
        }
        if (orden.getTotal() == null || orden.getTotal() <= 0) {
            errores.add("El total debe ser un valor mayor a 0");
        }

        if (!errores.isEmpty()) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", false);
            respuesta.put("errores", errores);
            return respuesta;
        }

        orden.setEstado("PENDIENTE");
        orden.setFechaCreacion(LocalDateTime.now());
        OrdenCompra guardada = repository.save(orden);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Orden creada exitosamente");
        respuesta.put("orden", guardada);
        return respuesta;
    }

    public Optional<Map<String, Object>> obtenerEstado(Long id) {
        return repository.findById(id).map(o -> {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", o.getId());
            respuesta.put("cliente", o.getCliente());
            respuesta.put("mascota", o.getMascota());
            respuesta.put("estado", o.getEstado());
            return respuesta;
        });
    }

    public Map<String, Object> actualizarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return Map.of("exito", false, "mensaje", "El estado no puede estar vacío");
        }

        String estadoUpper = nuevoEstado.toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estadoUpper)) {
            return Map.of("exito", false,
                    "mensaje", "Estado inválido. Los estados permitidos son: " + ESTADOS_VALIDOS);
        }

        Optional<OrdenCompra> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return Map.of("exito", false, "mensaje", "Orden con ID " + id + " no encontrada");
        }

        OrdenCompra orden = opt.get();
        orden.setEstado(estadoUpper);
        repository.save(orden);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Estado actualizado correctamente");
        respuesta.put("orden", orden);
        return respuesta;
    }

    public Map<String, Object> eliminarOrden(Long id) {
        if (!repository.existsById(id)) {
            return Map.of("exito", false, "mensaje", "Orden con ID " + id + " no encontrada");
        }
        repository.deleteById(id);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Orden con ID " + id + " eliminada correctamente");
        return respuesta;
    }
}
