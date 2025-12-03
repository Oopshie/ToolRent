package com.Tingeso.ToolRent.Controllers;

import com.Tingeso.ToolRent.DTOs.RentDTO;
import com.Tingeso.ToolRent.Services.RentService;
import com.Tingeso.ToolRent.Entities.RentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rent")
public class RentController {

    @Autowired
    private RentService rentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public Object createRent(
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // Obtener nombre seguro del empleado
        String given = jwt.getClaimAsString("given_name");
        String family = jwt.getClaimAsString("family_name");
        String employeeName;

        if (given != null && family != null) {
            employeeName = given + " " + family;
        } else if (jwt.hasClaim("name")) {
            employeeName = jwt.getClaimAsString("name"); // fallback
        } else {
            employeeName = jwt.getClaimAsString("preferred_username"); // último recurso
        }

        // Datos enviados del front
        String rut = body.get("rut");
        Long toolId = Long.valueOf(body.get("toolId"));
        String finishDate = body.get("finishDate");

        // Guardar arriendo
        return rentService.createRent(rut, toolId, finishDate, employeeName);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<List<RentDTO>> getAllRents() {
        return ResponseEntity.ok(rentService.getAllRentsOrdered());
    }

    @PostMapping("/return/{rentId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<RentDTO> returnTool(@PathVariable Long rentId, @RequestBody Map<String, Boolean> body) {
        boolean damaged = body.getOrDefault("damaged", false);
        boolean irreparable = body.getOrDefault("irreparable", false);

        return ResponseEntity.ok(rentService.returnTool(rentId, damaged, irreparable));
    }

    @GetMapping("/report/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<RentEntity> getAllRentsForReport() {
        return rentService.getAll();
    }

    @GetMapping("/report/delayed")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<RentEntity> getAllRentsForDelayedReport() {
        return rentService.getAll();
    }

    @GetMapping("/report/tools")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<RentEntity> getAllRentsForToolsReport() {
        return rentService.getAll();
    }



}

