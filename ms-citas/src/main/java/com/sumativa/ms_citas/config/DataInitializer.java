package com.sumativa.ms_citas.config;

import com.sumativa.ms_citas.citas.model.CitaMedica;
import com.sumativa.ms_citas.citas.repository.CitaMedicaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CitaMedicaRepository citaRepository;

    public DataInitializer(CitaMedicaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public void run(String... args) {
        if (citaRepository.count() == 0) {
            LocalDate hoy = LocalDate.now();
            LocalDateTime ahora = LocalDateTime.now();

            citaRepository.save(crearCita("Ana Torres", "Bobby", "Perro",
                    "Dr. Rodríguez", hoy.plusDays(1), "09:00",
                    "Vacunación anual", "PROGRAMADA", ahora.minusDays(1)));

            citaRepository.save(crearCita("Pedro Soto", "Luna", "Gato",
                    "Dra. Martínez", hoy.plusDays(2), "11:00",
                    "Control de peso y revisión general", "PROGRAMADA", ahora.minusHours(3)));

            citaRepository.save(crearCita("Lucía Ramírez", "Coco", "Conejo",
                    "Dr. Rodríguez", hoy.plusDays(3), "15:00",
                    "Desparasitación", "PROGRAMADA", ahora.minusHours(1)));

            citaRepository.save(crearCita("Camila Rojas", "Nala", "Perro",
                    "Dra. Martínez", hoy.plusDays(4), "12:00",
                    "Revisión post-operatoria", "PROGRAMADA", ahora.minusHours(5)));

            citaRepository.save(crearCita("Tomás Vargas", "Simba", "Gato",
                    "Dr. Rodríguez", hoy.plusDays(5), "14:00",
                    "Limpieza dental", "CANCELADA", ahora.minusHours(2)));
        }
    }

    private CitaMedica crearCita(String paciente, String mascota, String tipoMascota,
                                  String veterinario, LocalDate fecha, String hora,
                                  String motivo, String estado, LocalDateTime fechaRegistro) {
        CitaMedica c = new CitaMedica();
        c.setPaciente(paciente);
        c.setMascota(mascota);
        c.setTipoMascota(tipoMascota);
        c.setVeterinario(veterinario);
        c.setFecha(fecha);
        c.setHora(hora);
        c.setMotivo(motivo);
        c.setEstado(estado);
        c.setFechaRegistro(fechaRegistro);
        return c;
    }
}
