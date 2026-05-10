package com.sumativa.ms_ordenes.ordenes.service;

import com.sumativa.ms_ordenes.ordenes.model.OrdenCompra;
import com.sumativa.ms_ordenes.ordenes.repository.OrdenCompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de Órdenes de Compra usando Mockito.
 * No requieren base de datos ni contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrdenCompraService — pruebas unitarias")
class OrdenCompraServiceTest {

    @Mock
    private OrdenCompraRepository repository;

    @InjectMocks
    private OrdenCompraService service;

    private OrdenCompra ordenValida;

    @BeforeEach
    void prepararOrdenValida() {
        ordenValida = new OrdenCompra();
        ordenValida.setCliente("Maria Ovalle");
        ordenValida.setMascota("Rocky");
        ordenValida.setTipoMascota("Perro");
        ordenValida.setProductos(List.of("Alimento premium 5kg"));
        ordenValida.setTotal(15990.0);
    }

    @Test
    @DisplayName("crearOrden con datos válidos retorna éxito y persiste estado PENDIENTE")
    void crearOrden_conDatosValidos_retornaExito() {
        when(repository.save(any(OrdenCompra.class))).thenAnswer(invocacion -> {
            OrdenCompra recibida = invocacion.getArgument(0);
            recibida.setId(99L);
            return recibida;
        });

        Map<String, Object> resultado = service.crearOrden(ordenValida);

        assertThat(resultado.get("exito")).isEqualTo(true);
        assertThat(resultado.get("mensaje")).isEqualTo("Orden creada exitosamente");

        OrdenCompra creada = (OrdenCompra) resultado.get("orden");
        assertThat(creada).isNotNull();
        assertThat(creada.getEstado()).isEqualTo("PENDIENTE");
        assertThat(creada.getFechaCreacion()).isNotNull();

        verify(repository).save(any(OrdenCompra.class));
    }

    @Test
    @DisplayName("actualizarEstado con un estado fuera de la lista permitida retorna error")
    void actualizarEstado_conEstadoInvalido_retornaError() {
        Map<String, Object> resultado = service.actualizarEstado(1L, "ENTREGADA");

        assertThat(resultado.get("exito")).isEqualTo(false);
        assertThat((String) resultado.get("mensaje")).contains("Estado inválido");

        verify(repository, never()).save(any(OrdenCompra.class));
    }

    @Test
    @DisplayName("actualizarEstado a una orden inexistente retorna error con mensaje 'no encontrada'")
    void actualizarEstado_aOrdenInexistente_retornaError() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Map<String, Object> resultado = service.actualizarEstado(99L, "EN_PROCESO");

        assertThat(resultado.get("exito")).isEqualTo(false);
        assertThat((String) resultado.get("mensaje")).contains("no encontrada");

        verify(repository, never()).save(any(OrdenCompra.class));
    }
}
