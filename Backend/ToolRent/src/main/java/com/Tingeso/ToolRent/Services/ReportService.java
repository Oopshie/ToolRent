package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.DTOs.ActiveRentReportDTO;
import com.Tingeso.ToolRent.Entities.RentEntity;
import com.Tingeso.ToolRent.Repositories.RentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class ReportService {

    @Autowired
    private RentRepository rentRepository;

    // 1) PRESTAMOS ACTIVOS
    public List<ActiveRentReportDTO> getActiveRents() {

        List<RentEntity> rents = rentRepository.findByActiveTrue();

        return rents.stream().map(r -> {

            boolean isLate = false;

            // --- CALCULAR ATRASO (trabajando con Strings) ---
            try {
                if (r.getReturnDate() == null && r.getFinishDate() != null) {
                    String today = java.time.LocalDate.now().toString();
                    // comparación de fechas en formato yyyy-MM-dd funciona como string
                    isLate = r.getFinishDate().compareTo(today) < 0;
                }
            } catch (Exception ignored) {}

            return new ActiveRentReportDTO(
                    r.getId(),
                    r.getClient().getName(),
                    r.getTool().getName(),
                    r.getStartDate(),
                    r.getFinishDate(),
                    isLate
            );

        }).toList();
    }

    // 2) CLIENTES CON ATRASOS
    public List<Map<String, Object>> getLateClients() {

        // HOY como string yyyy-MM-dd (porque tu entidad usa String)
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        System.out.println("📌 Buscando clientes con atraso para la fecha: " + today);

        List<Map<String, Object>> result = rentRepository.findLateClients(today);

        System.out.println("📌 Resultado obtenido desde la BD (clientes con atraso): ");
        result.forEach(r -> System.out.println(" - " + r));

        return result;
    }

    // 3) RANKING DE HERRAMIENTAS
    public List<Map<String, Object>> getToolRanking() {
        List<Map<String, Object>> raw = rentRepository.getToolRanking();

        // Normalizar llaves a camelCase
        return raw.stream().map(row -> Map.of(
                "toolName", row.get("toolname"),
                "timesUsed", row.get("timesused")
        )).toList();
    }

}
