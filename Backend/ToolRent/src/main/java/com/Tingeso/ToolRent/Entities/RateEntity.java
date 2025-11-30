package com.Tingeso.ToolRent.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table (name = "Rate")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class RateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private int dailyRentalRate;
    private int dailyLateFeeRent;
}
