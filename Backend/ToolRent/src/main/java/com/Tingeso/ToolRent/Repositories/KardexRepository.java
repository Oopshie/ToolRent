package com.Tingeso.ToolRent.Repositories;

import com.Tingeso.ToolRent.Entities.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface KardexRepository extends JpaRepository<KardexEntity, Long> {

    List<KardexEntity> findByMovementType(int type);

    List<KardexEntity> findByToolIdOrderByMovementDateDesc(Long toolId);

    List<KardexEntity> findByMovementDateBetweenOrderByMovementDateDesc(
            String startDate,
            String endDate
        );
}

