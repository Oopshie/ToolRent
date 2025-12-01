package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.DTOs.RentDTO;
import com.Tingeso.ToolRent.Entities.*;
import com.Tingeso.ToolRent.Repositories.ClientRepository;
import com.Tingeso.ToolRent.Repositories.KardexRepository;
import com.Tingeso.ToolRent.Repositories.RentRepository;
import com.Tingeso.ToolRent.Repositories.ToolRepository;
import com.Tingeso.ToolRent.Repositories.RateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class RentService {

    @Autowired private ClientRepository clientRepository;
    @Autowired private RentRepository rentRepository;
    @Autowired private ToolRepository toolRepository;
    @Autowired private KardexRepository kardexRepository;
    @Autowired private RateRepository rateRepository;

    public Object createRent(String clientRut, Long toolId, String finishDate, String employeeName) {

        // ===== CLIENTE =====
        ClientEntity client = clientRepository.findByRut(clientRut).orElse(null);
        if (client == null)
            return "CLIENT_NOT_FOUND";

        if (client.getStatus() != 1)
            return "CLIENT_RESTRICTED";

        // ===== PRÉSTAMOS ACTIVOS =====
        List<RentEntity> activeRents = rentRepository.findByClientIdAndActiveTrue(client.getId());
        if (activeRents.size() >= 5)
            return "CLIENT_MAX_RENTS";

        if (rentRepository.existsByClientIdAndToolIdAndActiveTrue(client.getId(), toolId))
            return "TOOL_ALREADY_RENTED_BY_CLIENT";

        // ===== HERRAMIENTA =====
        ToolEntity tool = toolRepository.findById(toolId).orElse(null);
        if (tool == null)
            return "TOOL_NOT_FOUND";

        if (tool.getStatus() != 1)
            return "TOOL_NOT_AVAILABLE";

        // ===== FECHA =====
        LocalDate end = LocalDate.parse(finishDate);
        if (end.isBefore(LocalDate.now()))
            return "Fecha de devolución inválida";

        // ===== CREAR PRÉSTAMO =====
        RentEntity rent = new RentEntity();
        rent.setClientId(client.getId());
        rent.setToolId(toolId);
        rent.setStartDate(LocalDate.now().toString());
        rent.setFinishDate(finishDate);

        rent.setActive(true);
        rent.setReturnDate(null);
        rent.setFineAmount(0);
        rent.setDamaged(false);
        rent.setIrreparable(false);

        rent.setEmployeeName(employeeName); // <--- EMPLEADO DE KEYCLOAK

        rentRepository.save(rent);

        // ===== ACTUALIZAR ESTADO =====
        tool.setStatus(2);
        toolRepository.save(tool);

        // ===== KARDEX =====
        KardexEntity mov = new KardexEntity();
        mov.setMovementType(1); // 1 = préstamo
        mov.setMovementDate(LocalDateTime.now().toString());
        mov.setQuantity(1);
        mov.setToolId(toolId);
        mov.setEmployeeName(employeeName);

        kardexRepository.save(mov);

        return rent;
    }

    public List<RentDTO> getAllRentsOrdered() {

        List<RentEntity> rents = rentRepository.findAll();
        LocalDate today = LocalDate.now();

        List<RentDTO> dtos = rents.stream()
                .map(r -> new RentDTO(
                        r.getId(),
                        r.getClient() != null ? r.getClient().getName() : null,
                        r.getTool() != null ? r.getTool().getName() : null,
                        r.getStartDate(),
                        r.getFinishDate(),
                        r.getReturnDate(),
                        r.isActive(),
                        r.isDamaged(),
                        r.isIrreparable(),
                        r.getFineAmount(),
                        r.getTotalAmount(),      // ← AGREGADO
                        r.getEmployeeName()      // ← ÚLTIMO
                ))
                .collect(Collectors.toList());


        return dtos.stream()
                .sorted((a, b) -> {
                    boolean aLate = a.getReturnDate() == null &&
                            LocalDate.parse(a.getFinishDate()).isBefore(today);

                    boolean bLate = b.getReturnDate() == null &&
                            LocalDate.parse(b.getFinishDate()).isBefore(today);

                    if (aLate && !bLate) return -1;
                    if (!aLate && bLate) return 1;

                    if (a.isActive() && !b.isActive()) return -1;
                    if (!a.isActive() && b.isActive()) return 1;

                    return 0;
                })
                .collect(Collectors.toList());
    }

    private int calculateTotal(RentEntity rent, ToolEntity tool, RateEntity rate) {

        LocalDate start = LocalDate.parse(rent.getStartDate());
        LocalDate finish = LocalDate.parse(rent.getFinishDate());
        LocalDate returned = LocalDate.parse(rent.getReturnDate());

        // Días de arriendo (incluyendo el día inicial)
        int rentalDays = (int) (finish.toEpochDay() - start.toEpochDay()) + 1;
        if (rentalDays < 1) rentalDays = 1;

        int rentalCost = rentalDays * rate.getDailyRentalRate();

        // Días de atraso
        int lateDays = 0;
        if (returned.isAfter(finish)) {
            lateDays = (int) (returned.toEpochDay() - finish.toEpochDay());
        }

        int lateFee = lateDays * rate.getDailyLateFeeRent();

        // Reposición
        int replacementCost = 0;
        if (rent.isIrreparable()) {
            replacementCost = tool.getReplacementValue();
        }

        return rentalCost + lateFee + replacementCost;
    }

    public RentDTO returnTool(Long rentId, boolean damaged, boolean irreparable) {

        RentEntity rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("Rent not found"));

        ToolEntity tool = toolRepository.findById(rent.getToolId())
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        RateEntity rate = rateRepository.findTopByOrderByIdDesc();
        if (rate == null) {
            throw new RuntimeException("Rate missing");
        }

        // Actualizar estados
        rent.setReturnDate(LocalDate.now().toString());
        rent.setActive(false);
        rent.setDamaged(damaged);
        rent.setIrreparable(irreparable);

        // Calcular total
        int total = calculateTotal(rent, tool, rate);
        rent.setTotalAmount(total);

        rentRepository.save(rent);

        // Actualizar herramienta
        // Actualizar estado de la herramienta según daño
        if (irreparable) {
            tool.setStatus(4); // Dada de baja
            KardexEntity mov= new KardexEntity();
            mov.setMovementType(3); // baja
            mov.setMovementDate(LocalDateTime.now().toString());
            mov.setQuantity(1);
            mov.setToolId(tool.getId());
            mov.setEmployeeName(rent.getEmployeeName());
            kardexRepository.save(mov);
        } else if (damaged) {
            tool.setStatus(3); // En reparación
            KardexEntity mov= new KardexEntity();
            mov.setMovementType(5); // en reparación
            mov.setMovementDate(LocalDateTime.now().toString());
            mov.setQuantity(1);
            mov.setToolId(tool.getId());
            mov.setEmployeeName(rent.getEmployeeName());
            kardexRepository.save(mov);
        } else {
            tool.setStatus(1); // Disponible

        }

        toolRepository.save(tool);

        // Kardex
        KardexEntity mov = new KardexEntity();
        mov.setMovementType(2); // devolución
        mov.setMovementDate(LocalDateTime.now().toString());
        mov.setQuantity(1);
        mov.setToolId(tool.getId());
        mov.setEmployeeName(rent.getEmployeeName());
        kardexRepository.save(mov);

        // Devolver DTO
        return new RentDTO(
                rent.getId(),
                rent.getClient().getName(),
                rent.getTool().getName(),
                rent.getStartDate(),
                rent.getFinishDate(),
                rent.getReturnDate(),
                rent.isActive(),
                rent.isDamaged(),
                rent.isIrreparable(),
                rent.getFineAmount(),
                rent.getTotalAmount(),
                rent.getEmployeeName()
        );
    }

    public List<RentEntity>getAll() {
        return rentRepository.findAll();
    }



}

