package com.Tingeso.ToolRent.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Tool")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ToolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String name;
    private String category;
    private Integer replacementValue;
    private int status; // Available = 1, Lent = 2, Under repair = 3, Decommissioned = 4

    @Transient
    private Integer stock;
}
