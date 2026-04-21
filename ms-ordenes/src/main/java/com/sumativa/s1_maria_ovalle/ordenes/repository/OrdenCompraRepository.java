package com.sumativa.s1_maria_ovalle.ordenes.repository;

import com.sumativa.s1_maria_ovalle.ordenes.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByEstado(String estado);

    List<OrdenCompra> findByClienteContainingIgnoreCase(String cliente);
}
