package com.Tingeso.ToolRent.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Client")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String rut;
    private String name;
    private String email;
    private String phoneNumber;
    private int status; // Active = 1, restricted = 0

}
