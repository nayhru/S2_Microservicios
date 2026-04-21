package com.sumativa.ms_citas.citas.service;

import com.sumativa.ms_citas.citas.model.CitaMedica;
import com.sumativa.ms_citas.citas.repository.CitaMedicaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CitaMedicaService {

    private final CitaMedicaRepository repository;

    private static final List<String> HORAS_DISPONIBLES =
            Arrays.asList("09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00");

    public CitaMedicaService(CitaMedicaRepository repository) {
        this.repository = repository;
    }

    public List<CitaMedica> obtenerTodas() {
        return repository.findAll();
    }

    public Optional<CitaMedica> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    public Map<String, Object> programarCita(CitaMedica cita) {
        List<String> errores = new ArrayList<>();

        if (cita.getPaciente() == null || cita.getPaciente().isBlank()) {
            errores.add("El nombre del paciente (dueño) es obligatorio");
        }
        if (cita.getMascota() == null || cita.getMascota().isBlank()) {
            errores.add("El nombre de la mascota es obligatorio");
        }
        if (cita.getTipoMascota() == null || cita.getTipoMascota().isBlank()) {
            errores.add("El tipo de mascota es obligatorio");
        }
        if (cita.getVeterinario() == null || cita.getVeterinario().isBlank()) {
            errores.add("El nombre del veterinario es obligatorio");
        }
        if (cita.getFecha() == null) {
            errores.add("La fecha de la cita es obligatoria");
        } else if (cita.getFecha().isBefore(LocalDate.now())) {
            errores.add("La fecha de la cita debe ser hoy o en el futuro");
        }
        if (cita.getHora() == null || cita.getHora().isBlank()) {
            errores.add("La hora de la cita es obligatoria");
        } else if (!HORAS_DISPONIBLES.contains(cita.getHora())) {
            errores.add("Hora inválida. Los horarios disponibles son: " + HORAS_DISPONIBLES);
        }
        if (cita.getMotivo() == null || cita.getMotivo().isBlank()) {
            errores.add("El motivo de la cita es obligatorio");
        }

        if (!errores.isEmpty()) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", false);
            respuesta.put("errores", errores);
            return respuesta;
        }

        boolean ocupado = repository.existsByFechaAndHoraAndVeterinarioIgnoreCaseAndEstado(
                cita.getFecha(), cita.getHora(), cita.getVeterinario(), "PROGRAMADA");

        if (ocupado) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", false);
            respuesta.put("errores", List.of(
                    "El horario " + cita.getHora() + " del " + cita.getFecha()
                    + " ya está reservado para " + cita.getVeterinario()
                    + ". Consulte /api/citas/disponibilidad para ver horarios libres."));
            return respuesta;
        }

        cita.setEstado("PROGRAMADA");
        cita.setFechaRegistro(LocalDateTime.now());
        CitaMedica guardada = repository.save(cita);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Cita programada exitosamente");
        respuesta.put("cita", guardada);
        return respuesta;
    }

    public Map<String, Object> cancelarCita(Long id) {
        Optional<CitaMedica> opt = repository.findById(id);

        if (opt.isEmpty()) {
            return Map.of("exito", false, "mensaje", "Cita con ID " + id + " no encontrada");
        }

        CitaMedica cita = opt.get();

        if ("CANCELADA".equals(cita.getEstado())) {
            return Map.of("exito", false, "mensaje", "La cita ya se encuentra cancelada");
        }
        if ("COMPLETADA".equals(cita.getEstado())) {
            return Map.of("exito", false, "mensaje", "No se puede cancelar una cita que ya fue completada");
        }

        cita.setEstado("CANCELADA");
        repository.save(cita);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Cita cancelada exitosamente");
        respuesta.put("cita", cita);
        return respuesta;
    }

    public Map<String, Object> eliminarCita(Long id) {
        if (!repository.existsById(id)) {
            return Map.of("exito", false, "mensaje", "Cita con ID " + id + " no encontrada");
        }
        repository.deleteById(id);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("mensaje", "Cita con ID " + id + " eliminada correctamente");
        return respuesta;
    }

    public Map<String, Object> consultarDisponibilidad(LocalDate fecha, String veterinario) {
        List<CitaMedica> citasDelDia = (veterinario == null)
                ? repository.findByFechaAndEstado(fecha, "PROGRAMADA")
                : repository.findByFechaAndVeterinarioIgnoreCaseAndEstado(fecha, veterinario, "PROGRAMADA");

        List<String> horasOcupadas = citasDelDia.stream()
                .map(CitaMedica::getHora)
                .sorted()
                .collect(Collectors.toList());

        List<String> horasLibres = HORAS_DISPONIBLES.stream()
                .filter(h -> !horasOcupadas.contains(h))
                .collect(Collectors.toList());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", true);
        respuesta.put("fecha", fecha.toString());
        respuesta.put("veterinario", veterinario != null ? veterinario : "Todos");
        respuesta.put("horariosDisponibles", horasLibres);
        respuesta.put("horariosOcupados", horasOcupadas);
        respuesta.put("totalDisponibles", horasLibres.size());
        return respuesta;
    }
}
