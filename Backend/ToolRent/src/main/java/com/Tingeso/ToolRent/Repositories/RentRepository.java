package com.Tingeso.ToolRent.Repositories;

import com.Tingeso.ToolRent.Entities.RentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface RentRepository extends JpaRepository<RentEntity, Long> {

    //prestamos activos del cliente
    List<RentEntity> findByClientIdAndActiveTrue(Long clientId);

    // prestamo activo de una herramienta para un cliente
    boolean existsByClientIdAndToolIdAndActiveTrue(Long clientId, Long toolId);

    List<RentEntity> findByReturnDateIsNull();

}
