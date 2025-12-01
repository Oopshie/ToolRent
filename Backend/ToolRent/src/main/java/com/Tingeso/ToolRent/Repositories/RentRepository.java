package com.Tingeso.ToolRent.Repositories;

import com.Tingeso.ToolRent.DTOs.ActiveRentReportDTO;
import com.Tingeso.ToolRent.DTOs.LateClientReportDTO;
import com.Tingeso.ToolRent.DTOs.ToolRankingReportDTO;
import com.Tingeso.ToolRent.Entities.RentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository

public interface RentRepository extends JpaRepository<RentEntity, Long> {

    List<RentEntity> findByClientIdAndActiveTrue(Long id);

    boolean existsByClientIdAndToolIdAndActiveTrue(Long id, Long toolId);

    // 1) Prestamos activos
    List<RentEntity> findByActiveTrue();

    // 2) Clientes con atrasos (finishDate < hoy && returnDate == null)
    @Query(value = """
SELECT
    r.id AS rentId,
    c.name AS clientName,
    r.finish_date AS finishDate
FROM rents r
JOIN client c ON r.client_id = c.id
WHERE r.return_date IS NULL
  AND TO_DATE(r.finish_date, 'YYYY-MM-DD') < TO_DATE(:today, 'YYYY-MM-DD')
""", nativeQuery = true)
    List<Map<String, Object>> findLateClients(@Param("today") String today);


    // 3) Ranking herramientas más usadas
    @Query(value = """
            SELECT 
                t.name AS toolName,
                COUNT(r.id) AS timesUsed
            FROM rents r
            JOIN tool t ON r.tool_id = t.id
            GROUP BY t.name
            ORDER BY timesUsed DESC
            """, nativeQuery = true)
    List<Map<String, Object>> getToolRanking();
}
