package com.Tingeso.ToolRent.Controllers;

import com.Tingeso.ToolRent.DTOs.ActiveRentReportDTO;
import com.Tingeso.ToolRent.Entities.RentEntity;
import com.Tingeso.ToolRent.Services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/active")
    public List<ActiveRentReportDTO> getActiveRents() {
        return reportService.getActiveRents();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/late")
    public List<Map<String, Object>> getLateClients() {
        return reportService.getLateClients();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/ranking")
    public List<Map<String, Object>> getToolRanking() {
        return reportService.getToolRanking();
    }
}
