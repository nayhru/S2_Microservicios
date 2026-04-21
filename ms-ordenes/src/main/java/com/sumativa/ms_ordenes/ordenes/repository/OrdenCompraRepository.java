package com.sumativa.ms_ordenes.ordenes.repository;

import com.sumativa.ms_ordenes.ordenes.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByEstado(String estado);

    List<OrdenCompra> findByClienteContainingIgnoreCase(String cliente);
}
