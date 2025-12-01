package com.Tingeso.ToolRent.DTOs;

public class ToolRankingReportDTO {
    public Long toolId;
    public String toolName;
    public String category;
    public Long rentalCount;

    public ToolRankingReportDTO(Long toolId, String toolName, String category,
                                Long rentalCount) {
        this.toolId = toolId;
        this.toolName = toolName;
        this.category = category;
        this.rentalCount = rentalCount;
    }
}
