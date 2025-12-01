package com.Tingeso.ToolRent.Controllers;

import com.Tingeso.ToolRent.Entities.ToolEntity;
import com.Tingeso.ToolRent.Services.ToolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ✅ Hamcrest: importa solo lo que usas, NO uses *
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

// ✅ Mockito matchers: aquí está el any() bueno
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToolController.class)
public class ToolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToolService toolService;

    // ==== Helpers para JWT con roles ====

    private RequestPostProcessor adminJwt() {
        return jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .jwt(j -> {
                    j.claim("given_name", "Alex");
                    j.claim("family_name", "Garcia");
                });
    }

    private RequestPostProcessor employeeJwt() {
        return jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
                .jwt(j -> j.claim("preferred_username", "empleado1"));
    }

    // ==== TESTS ====

    @Test
    public void listTools_ShouldReturnTools() throws Exception {
        ToolEntity tool1 = new ToolEntity(1L, "martillo", "manual", 10000, 1);
        ToolEntity tool2 = new ToolEntity(2L, "taladro", "electrico", 50000, 1);

        List<ToolEntity> tools = new ArrayList<>(Arrays.asList(tool1, tool2));

        given(toolService.getAllTools()).willReturn((ArrayList<ToolEntity>) tools);

        mockMvc.perform(get("/api/tools/").with(employeeJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("martillo")))
                .andExpect(jsonPath("$[1].name", is("taladro")));
    }

    @Test
    public void getToolById_ShouldReturnTool() throws Exception {
        ToolEntity tool = new ToolEntity(1L, "martillo", "manual", 10000, 1);

        given(toolService.getToolById(1L)).willReturn(tool);

        mockMvc.perform(get("/api/tools/{id}", 1L).with(employeeJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("martillo")))
                .andExpect(jsonPath("$.category", is("manual")));
    }

    @Test
    public void addTool_ShouldReturnSavedTool() throws Exception {
        // El servicio convierte nombre/categoría a minúsculas
        ToolEntity savedTool = new ToolEntity(1L, "martillo", "manual", 10000, 1);

        given(toolService.addTool(any(ToolEntity.class), anyString())).willReturn(savedTool);

        String toolJson = """
            {
                "name": "Martillo",
                "category": "Manual",
                "replacementValue": 10000
            }
            """;

        mockMvc.perform(post("/api/tools/")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toolJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("martillo")))
                .andExpect(jsonPath("$.category", is("manual")));
    }

    @Test
    public void updateTool_ShouldReturnUpdatedTool() throws Exception {
        // existing con mismo status y replacementValue para que NO se dispare updateToolStatus ni updateToolGroupValues
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 1);
        ToolEntity updated = new ToolEntity(1L, "martillo pro", "manual", 10000, 1);

        given(toolService.getToolById(1L)).willReturn(existing);
        given(toolService.updateToolFields(any(ToolEntity.class))).willReturn(updated);

        String toolJson = """
            {
                "name": "martillo pro",
                "category": "manual",
                "replacementValue": 10000,
                "status": 1
            }
            """;

        mockMvc.perform(put("/api/tools/{id}", 1L)
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toolJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("martillo pro")));
    }

    @Test
    public void deactivateToolById_ShouldReturnDeactivatedTool() throws Exception {
        ToolEntity deactivated = new ToolEntity(1L, "martillo", "manual", 10000, 4);

        given(toolService.deactivateTool(1L)).willReturn(deactivated);

        mockMvc.perform(delete("/api/tools/{id}/deactivate", 1L).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(4)));
    }

    @Test
    public void deleteToolById_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/tools/{id}", 1L).with(adminJwt()))
                .andExpect(status().isNoContent());

        verify(toolService).deleteToolById(1L);
    }

    @Test
    public void getToolsByName_ShouldReturnFilteredTools() throws Exception {
        ToolEntity tool = new ToolEntity(1L, "martillo", "manual", 10000, 1);
        List<ToolEntity> tools = List.of(tool);

        given(toolService.getToolsByName("martillo")).willReturn(tools);

        mockMvc.perform(get("/api/tools/search")
                        .with(employeeJwt())
                        .param("name", "martillo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("martillo")));
    }

    @Test
    public void getToolsByCategory_ShouldReturnFilteredTools() throws Exception {
        ToolEntity tool = new ToolEntity(1L, "martillo", "manual", 10000, 1);
        List<ToolEntity> tools = new ArrayList<>(List.of(tool));

        given(toolService.getToolsByCategory("manual")).willReturn((ArrayList<ToolEntity>) tools);

        mockMvc.perform(get("/api/tools/category/{category}", "manual")
                        .with(employeeJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("manual")));
    }

    @Test
    public void checkDuplicate_ShouldReturnSuggestion() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("exists", true);
        response.put("suggestedPrice", 15000);
        response.put("message", "Se encontró una herramienta similar. Precio sugerido basado en datos existentes.");

        given(toolService.checkDuplicateAndSuggestPrice("martillo", "manual"))
                .willReturn(response);

        mockMvc.perform(get("/api/tools/check-duplicate")
                        .with(adminJwt())
                        .param("name", "martillo")
                        .param("category", "manual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(true)))
                .andExpect(jsonPath("$.suggestedPrice", is(15000)))
                .andExpect(jsonPath("$.message", containsString("Precio sugerido")));
    }

    @Test
    public void getAvailableTools_ShouldReturnOnlyAvailable() throws Exception {
        ToolEntity tool1 = new ToolEntity(1L, "martillo", "manual", 10000, 1);
        ToolEntity tool2 = new ToolEntity(2L, "taladro", "electrico", 50000, 1);

        List<ToolEntity> tools = List.of(tool1, tool2);

        given(toolService.getToolsByStatus(1)).willReturn(tools);

        mockMvc.perform(get("/api/tools/available").with(employeeJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is(1)))
                .andExpect(jsonPath("$[1].status", is(1)));
    }
    // ⬇⬇⬇ Agrega desde aquí hacia abajo en tu ToolControllerTest ⬇⬇⬇

    @Test
    public void updateTool_WhenNotFound_ShouldReturn404() throws Exception {
        // El servicio devuelve null → controlador debe responder 404
        given(toolService.getToolById(1L)).willReturn(null);

        String body = """
            {
              "name": "martillo",
              "category": "manual",
              "replacementValue": 10000,
              "status": 1
            }
            """;

        mockMvc.perform(put("/api/tools/{id}", 1L)
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTool_WhenStatusChanges_ShouldCallUpdateToolStatus() throws Exception {
        // existing: status 1 → entra statusChanged = true cuando le mandamos status 3
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 1);
        ToolEntity updated  = new ToolEntity(1L, "martillo", "manual", 10000, 3);

        given(toolService.getToolById(1L)).willReturn(existing);
        given(toolService.updateToolFields(any(ToolEntity.class))).willReturn(updated);

        String body = """
            {
              "name": "martillo",
              "category": "manual",
              "replacementValue": 10000,
              "status": 3
            }
            """;

        mockMvc.perform(put("/api/tools/{id}", 1L)
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(3)));

        // 👇 Verificamos que se llamó al cambio de estado con el nombre que arma extractEmployeeName
        verify(toolService).updateToolStatus(1L, 3, "Alex Garcia");
    }

    @Test
    public void updateTool_WhenReplacementValueChanges_ShouldUpdateGroup() throws Exception {
        // existing: replacementValue 10000 → entra replacementChanged = true con 20000
        ToolEntity existing = new ToolEntity(1L, "martillo", "manual", 10000, 1);
        ToolEntity updated  = new ToolEntity(1L, "martillo", "manual", 20000, 1);

        given(toolService.getToolById(1L)).willReturn(existing);
        given(toolService.updateToolFields(any(ToolEntity.class))).willReturn(updated);

        String body = """
            {
              "name": "martillo",
              "category": "manual",
              "replacementValue": 20000,
              "status": 1
            }
            """;

        mockMvc.perform(put("/api/tools/{id}", 1L)
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replacementValue", is(20000)));

        // 👇 Verificamos que se llamó al método de grupo
        verify(toolService).updateToolGroupValues("martillo", "manual", 20000);
    }

    @Test
    public void updateTool_WhenServiceThrowsException_ShouldReturn500() throws Exception {
        // Forzamos una excepción en el servicio para cubrir el catch
        given(toolService.getToolById(1L)).willThrow(new RuntimeException("DB error"));

        String body = """
            {
              "name": "martillo",
              "category": "manual",
              "replacementValue": 10000,
              "status": 1
            }
            """;

        mockMvc.perform(put("/api/tools/{id}", 1L)
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }


}
