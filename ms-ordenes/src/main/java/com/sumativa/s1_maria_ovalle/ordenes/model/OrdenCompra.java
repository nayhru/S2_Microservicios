package com.sumativa.s1_maria_ovalle.ordenes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORDENES_COMPRA")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_seq")
    @SequenceGenerator(name = "orden_seq", sequenceName = "ORDEN_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false)
    private String mascota;

    @Column(name = "tipo_mascota", nullable = false)
    private String tipoMascota;

    @ElementCollection
    @CollectionTable(name = "ORDEN_PRODUCTOS", joinColumns = @JoinColumn(name = "orden_id"))
    @Column(name = "producto")
    private List<String> productos;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String estado; // PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public OrdenCompra() {}

    public OrdenCompra(Long id, String cliente, String mascota, String tipoMascota,
                       List<String> productos, Double total, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.cliente = cliente;
        this.mascota = mascota;
        this.tipoMascota = tipoMascota;
        this.productos = productos;
        this.total = total;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getMascota() { return mascota; }
    public void setMascota(String mascota) { this.mascota = mascota; }

    public String getTipoMascota() { return tipoMascota; }
    public void setTipoMascota(String tipoMascota) { this.tipoMascota = tipoMascota; }

    public List<String> getProductos() { return productos; }
    public void setProductos(List<String> productos) { this.productos = productos; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
