package com.Tingeso.ToolRent.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "Rents")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class RentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (unique = true, nullable = false)
    private long id;

    private Long clientId;
    private Long toolId;
    private String employeeName;

    private String startDate;
    private String finishDate;
    private String returnDate;

    private int fineAmount;
    private int totalAmount;

    boolean active;

    boolean damaged;
    boolean irreparable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId", referencedColumnName = "id", insertable = false, updatable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toolId", referencedColumnName = "id", insertable = false, updatable = false)
    private ToolEntity tool;

}
