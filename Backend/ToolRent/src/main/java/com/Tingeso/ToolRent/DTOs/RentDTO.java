package com.Tingeso.ToolRent.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentDTO {

    private Long id;

    private String clientName;
    private String toolName;

    private String startDate;
    private String finishDate;
    private String returnDate;

    private boolean active;

    private boolean damaged;
    private boolean irreparable;

    private int fineAmount;
    private int totalAmount;

    private String employeeName;
}
