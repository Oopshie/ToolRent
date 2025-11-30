package com.Tingeso.ToolRent.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "KardexMovement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KardexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    // 1 = Préstamo
    // 2 = Devolución
    // 3 = Baja
    // 4 = Ingreso manual (opcional)
    private int movementType;

    // Fecha/hora en formato ISO (LocalDateTime.toString())
    private String movementDate;

    // Cantidad de herramientas involucradas (en tu caso siempre 1)
    private int quantity;

    // Herramienta asociada
    private Long toolId;

    // Usuario que generó el movimiento (empleado/admin)
    private String employeeName;

    // Relación opcional (solo si quieres cargar datos de la herramienta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toolId", referencedColumnName = "id", insertable = false, updatable = false)
    private ToolEntity tool;
}
