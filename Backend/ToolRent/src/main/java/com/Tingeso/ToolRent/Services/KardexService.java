package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.DTOs.KardexDTO;
import com.Tingeso.ToolRent.Entities.KardexEntity;
import com.Tingeso.ToolRent.Repositories.KardexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KardexService {

    @Autowired
    private KardexRepository kardexRepository;

    private KardexDTO toDTO(KardexEntity k) {
        return new KardexDTO(
                k.getId(),
                k.getMovementDate(),
                k.getMovementType(),
                k.getQuantity(),
                k.getToolId(),
                k.getTool() != null ? k.getTool().getName() : null,
                k.getTool() != null ? k.getTool().getCategory() : null,
                k.getEmployeeName()
        );
    }

    public void registerMovement(int type, Long toolId, String employeeName) {
        KardexEntity mov = new KardexEntity();
        mov.setMovementType(type);
        mov.setMovementDate(LocalDateTime.now().toString());
        mov.setQuantity(1);
        mov.setToolId(toolId);
        mov.setEmployeeName(employeeName);

        kardexRepository.save(mov);
    }


    public List<KardexDTO> getByTool(Long toolId) {
        return kardexRepository.findByToolIdOrderByMovementDateDesc(toolId)
                .stream().map(this::toDTO).toList();
    }

    public List<KardexDTO> getByDateRange(String from, String to) {
        return kardexRepository.findByMovementDateBetweenOrderByMovementDateDesc(from, to)
                .stream().map(this::toDTO).toList();
    }

    public List<KardexDTO> getAllMovements() {
        return kardexRepository.findAll()
                .stream().map(this::toDTO).toList();
    }
}
