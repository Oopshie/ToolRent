package com.Tingeso.ToolRent.Repositories;

import com.Tingeso.ToolRent.Entities.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {

    public ToolEntity findByName(String name);

    List<ToolEntity> findByStatus(int status);

    List<ToolEntity> findByCategory(String category);

    List<ToolEntity> findByNameContainingIgnoreCase(String namePart);

    List<ToolEntity> findByNameAndCategory(String name, String category);

}
