package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.ToolEntity;
import com.Tingeso.ToolRent.Repositories.ToolRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ToolService {
    @Autowired
    private final ToolRepository toolRepository;

    @Autowired
    private KardexService kardexService;

    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public ToolEntity addTool(ToolEntity tool, String employeeName){

        //convertir a minusculas el nombre y categoria
        tool.setName(tool.getName().trim().toLowerCase());
        tool.setCategory(tool.getCategory().trim().toLowerCase());

        // asignar estado 1 (disponible) al crear una herramienta
        tool.setStatus(1);

        // GUARDAR primero para obtener ID
        ToolEntity saved = toolRepository.save(tool);

        // Ahora sí el ID existe
        kardexService.registerMovement(4, saved.getId(), employeeName); // 4 = Ingreso

        return saved;
    }

    public List<ToolEntity> getToolsByName(String namePart){
        return toolRepository.findByNameContainingIgnoreCase(namePart);
    }

    public ToolEntity getToolById(Long id){
        return toolRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tool not found with id: " + id));
    }

    public List<ToolEntity> getAllTools() {

        List<ToolEntity> tools = toolRepository.findAll();

        // contar cuántas hay disponibles POR NOMBRE
        Map<String, Long> disponiblesMap = tools.stream()
                .filter(t -> t.getStatus() == 1)
                .collect(Collectors.groupingBy(
                        ToolEntity::getName,
                        Collectors.counting()
                ));

        // insertar ese stock en cada herramienta
        for (ToolEntity t : tools) {
            long stock = disponiblesMap.getOrDefault(t.getName(), 0L);
            t.setStock((int) stock);
        }

        return tools;
    }

    public ArrayList<ToolEntity> getToolsByCategory(String category){
        return (ArrayList<ToolEntity>)toolRepository.findByCategory(category);
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase();
    }

    @Transactional
    public ToolEntity updateToolFields(ToolEntity tool) {

        ToolEntity existing = toolRepository.findById(tool.getId())
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        // NO modificar status aquí
        existing.setName(tool.getName());
        existing.setCategory(tool.getCategory());
        existing.setReplacementValue(tool.getReplacementValue());

        return toolRepository.save(existing);
    }

    @Transactional
    public void updateToolGroupValues(String name, String category, Integer newValue) {

        List<ToolEntity> group = toolRepository.findByNameAndCategory(
                normalize(name),
                normalize(category)
        );

        for (ToolEntity t : group) {
            t.setReplacementValue(newValue);
        }

        toolRepository.saveAll(group);
    }

    public ToolEntity updateToolStatus(Long toolId, int newStatus, String employeeName) {

        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        int oldStatus = tool.getStatus(); // ✔ CAPTURAR ANTES DE CAMBIARLO
        System.out.println("OLD STATUS = " + oldStatus + " | NEW = " + newStatus);

        // Aplicar nuevo estado
        tool.setStatus(newStatus);
        ToolEntity saved = toolRepository.save(tool);

        Long id = saved.getId();

        // Registrar movimiento en el Kardex
        switch (newStatus) {

            case 3: // En reparación
                if (oldStatus == 1 || oldStatus == 2) {
                    System.out.println("MOVIMIENTO: En reparación");
                    kardexService.registerMovement(5, id, employeeName);
                }
                break;

            case 1: // Disponible (cuando termina reparación)
                if (oldStatus == 3) {
                    System.out.println("MOVIMIENTO: Reparación completada");
                    kardexService.registerMovement(4, id, employeeName);
                }
                break;

            case 4: // Dada de baja
                System.out.println("MOVIMIENTO: Baja");
                kardexService.registerMovement(3, id, employeeName);
                break;

            default:
                System.out.println("Estado sin movimiento de kardex");
                break;
        }

        return saved;
    }

    public ToolEntity deactivateTool(Long id){
        ToolEntity tool = toolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada con id: " + id));
        tool.setStatus(4); // Estado 4 = dado de baja
        return toolRepository.save(tool);
    }

    public void deleteToolById(Long id){
        toolRepository.deleteById(id);
    }

    public Map<String, Object> checkDuplicateAndSuggestPrice(@NotNull String name, @NotNull String category) {
        Map<String, Object> response = new HashMap<>();

        // Normalizar entrada (como lo hace addTool)
        String normalizedName = name.trim().toLowerCase();
        String normalizedCategory = category.trim().toLowerCase();

        // Buscar herramientas con el mismo nombre y categoría
        List<ToolEntity> existingTools = toolRepository.findByNameAndCategory(normalizedName, normalizedCategory);

        if (!existingTools.isEmpty()) {
            // Si existe, usar el precio de reposición de la primera coincidencia
            ToolEntity existingTool = existingTools.get(0);
            response.put("exists", true);
            response.put("suggestedPrice", existingTool.getReplacementValue());
            response.put("message", "Se encontró una herramienta similar. Precio sugerido basado en datos existentes.");
        } else {
            // No existe duplicado
            response.put("exists", false);
            response.put("suggestedPrice", null);
            response.put("message", "No se encontraron herramientas similares.");
        }

        return response;
    }

    public List<ToolEntity> getToolsByStatus(int status) {
        return toolRepository.findByStatus(status);
    }

    @Transactional
    public void updateReplacementValueForGroup(String name, String category, int newRP){
        List<ToolEntity> tools = toolRepository.findByNameAndCategory(
                name.toLowerCase().trim(),
                category.toLowerCase().trim()
        );
        for (ToolEntity t : tools) {
            t.setReplacementValue(newRP);
        }

        toolRepository.saveAll(tools);
    }


    private Map<String, Long> calcularDisponiblesPorNombre(List<ToolEntity> herramientas) {
        return herramientas.stream()
                .filter(t -> t.getStatus() == 1)  // status 1 = disponible
                .collect(Collectors.groupingBy(
                        ToolEntity::getName,
                        Collectors.counting()
                ));
    }



}
