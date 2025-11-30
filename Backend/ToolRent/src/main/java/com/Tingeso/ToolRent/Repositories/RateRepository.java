package com.Tingeso.ToolRent.Repositories;

import com.Tingeso.ToolRent.Entities.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RateRepository extends JpaRepository<RateEntity, Long> {

    RateEntity findTopByOrderByIdDesc();
}
