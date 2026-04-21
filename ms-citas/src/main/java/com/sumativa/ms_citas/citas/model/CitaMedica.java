package com.sumativa.ms_citas.citas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CITAS_MEDICAS")
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cita_seq")
    @SequenceGenerator(name = "cita_seq", sequenceName = "CITA_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String paciente;       // Nombre del dueño de la mascota

    @Column(nullable = false)
    private String mascota;

    @Column(name = "tipo_mascota", nullable = false)
    private String tipoMascota;

    @Column(nullable = false)
    private String veterinario;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String hora;           // Formato HH:mm  p.ej. "09:00"

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private String estado;         // PROGRAMADA, CANCELADA, COMPLETADA

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public CitaMedica() {}

    public CitaMedica(Long id, String paciente, String mascota, String tipoMascota,
                      String veterinario, LocalDate fecha, String hora,
                      String motivo, String estado, LocalDateTime fechaRegistro) {
        this.id = id;
        this.paciente = paciente;
        this.mascota = mascota;
        this.tipoMascota = tipoMascota;
        this.veterinario = veterinario;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }

    public String getMascota() { return mascota; }
    public void setMascota(String mascota) { this.mascota = mascota; }

    public String getTipoMascota() { return tipoMascota; }
    public void setTipoMascota(String tipoMascota) { this.tipoMascota = tipoMascota; }

    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
