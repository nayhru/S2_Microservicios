package com.sumativa.s1_maria_ovalle.config;

import com.sumativa.s1_maria_ovalle.ordenes.model.OrdenCompra;
import com.sumativa.s1_maria_ovalle.ordenes.repository.OrdenCompraRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final OrdenCompraRepository ordenRepository;

    public DataInitializer(OrdenCompraRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @Override
    public void run(String... args) {
        if (ordenRepository.count() == 0) {
            LocalDateTime ahora = LocalDateTime.now();

            ordenRepository.save(crearOrden("Juan Pérez", "Firulais", "Perro",
                    Arrays.asList("Alimento premium canino 5kg", "Collar antipulgas"),
                    45000.0, "PENDIENTE", ahora.minusDays(4)));

            ordenRepository.save(crearOrden("María González", "Michi", "Gato",
                    Arrays.asList("Arena sanitaria 10kg", "Juguete rascador", "Vitaminas felinas"),
                    32000.0, "EN_PROCESO", ahora.minusDays(3)));

            ordenRepository.save(crearOrden("Carlos López", "Burbuja", "Pez",
                    Arrays.asList("Alimento para peces tropicales", "Filtro de acuario"),
                    18500.0, "COMPLETADA", ahora.minusDays(2)));

            ordenRepository.save(crearOrden("Sofía Herrera", "Manchas", "Gato",
                    Arrays.asList("Paté para gatos 12 unid", "Arenero cubierto", "Desparasitante oral"),
                    27300.0, "PENDIENTE", ahora.minusDays(1)));

            ordenRepository.save(crearOrden("Rodrigo Fuentes", "Thor", "Perro",
                    Arrays.asList("Arnés acolchado talla L", "Correa retráctil 5m"),
                    34900.0, "CANCELADA", ahora.minusHours(8)));
        }
    }

    private OrdenCompra crearOrden(String cliente, String mascota, String tipoMascota,
                                    java.util.List<String> productos, Double total,
                                    String estado, LocalDateTime fechaCreacion) {
        OrdenCompra o = new OrdenCompra();
        o.setCliente(cliente);
        o.setMascota(mascota);
        o.setTipoMascota(tipoMascota);
        o.setProductos(productos);
        o.setTotal(total);
        o.setEstado(estado);
        o.setFechaCreacion(fechaCreacion);
        return o;
    }
}
