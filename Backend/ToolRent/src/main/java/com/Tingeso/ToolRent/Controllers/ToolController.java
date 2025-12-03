package com.Tingeso.ToolRent.Controllers;

import com.Tingeso.ToolRent.Entities.ToolEntity;
import com.Tingeso.ToolRent.Services.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/tools")
@CrossOrigin(origins = "http://localhost:5173")
public class ToolController {
    @Autowired
    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    // Obtener nombre desde Keycloak
    private String extractEmployeeName(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();

        String given = jwt.getClaimAsString("given_name");
        String family = jwt.getClaimAsString("family_name");

        if (given != null && family != null)
            return given + " " + family;

        if (jwt.hasClaim("name"))
            return jwt.getClaimAsString("name");

        return jwt.getClaimAsString("preferred_username");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<ToolEntity> addTool(@RequestBody ToolEntity tool, Authentication auth) {
        String employeeName = extractEmployeeName(auth);

        return ResponseEntity.ok(toolService.addTool(tool, employeeName));
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/")
    public ResponseEntity<List<ToolEntity>> listTools() {
        List<ToolEntity> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/search")
    public ResponseEntity<List<ToolEntity>> getToolsByName(@RequestParam String name) {
        List<ToolEntity> tools = toolService.getToolsByName(name);
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<ToolEntity> getEntityById(@PathVariable Long id) {
        ToolEntity tool = toolService.getToolById(id);
        return ResponseEntity.ok(tool);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ToolEntity>> getToolByCategory(@PathVariable String category) {
        List<ToolEntity> tools = toolService.getToolsByCategory(category);
        return ResponseEntity.ok(tools);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ToolEntity> updateTool(
            @PathVariable Long id,
            @RequestBody ToolEntity tool,
            Authentication auth) {

        System.out.println("🔧 ENTRANDO A updateTool DEL CONTROLADOR");

        try {
            ToolEntity existing = toolService.getToolById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            tool.setId(id);

            boolean statusChanged = !Objects.equals(existing.getStatus(), tool.getStatus());

            System.out.println("existing status = " + existing.getStatus());
            System.out.println("incoming status = " + tool.getStatus());
            System.out.println("statusChanged = " + statusChanged);

            boolean replacementChanged = !Objects.equals(existing.getReplacementValue(),
                    tool.getReplacementValue());

            System.out.println("existing replacementValue = " + existing.getReplacementValue());
            System.out.println("incoming replacementValue = " + tool.getReplacementValue());
            System.out.println("replacementChanged = " + replacementChanged);


            // Si cambió replacementValue, Actualizar el grupo ANTES de actualizar esta herramienta
            if (replacementChanged) {
                toolService.updateToolGroupValues(
                        existing.getName(),        // Usar los valores ANTIGUOS (correctos)
                        existing.getCategory(),
                        tool.getReplacementValue() // nuevo valor
                );
            }

            // Cambió el estado actualizar estado + Kardex
            if (statusChanged) {
                if (auth == null) return ResponseEntity.badRequest().build();
                String employeeName = extractEmployeeName(auth);
                toolService.updateToolStatus(id, tool.getStatus(), employeeName);
            }

            //Ahora actualizar esta herramienta (nombre/categoría/valor)
            ToolEntity saved = toolService.updateToolFields(tool);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<ToolEntity> deactivateToolById(@PathVariable Long id) {
        return  ResponseEntity.ok(toolService.deactivateTool(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/check-duplicate")
    public ResponseEntity<Map<String, Object>> checkDuplicate(@RequestParam String name, @RequestParam String category) {
        Map<String, Object> response = toolService.checkDuplicateAndSuggestPrice(name, category);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToolById(@PathVariable Long id) {
         toolService.deleteToolById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping("/available")
    public List<ToolEntity> getAvailableTools() {
        return toolService.getToolsByStatus(1); // Estado 1 = disponible
    }

}
