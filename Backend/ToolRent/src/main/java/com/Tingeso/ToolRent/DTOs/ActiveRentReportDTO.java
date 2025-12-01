package com.Tingeso.ToolRent.DTOs;
public class ActiveRentReportDTO {
    public Long rentId;
    public String clientName;
    public String toolName;
    public String startDate;
    public String finishDate;
    public boolean late;

    public ActiveRentReportDTO(Long rentId, String clientName, String toolName,
                               String startDate, String finishDate, boolean late) {
        this.rentId = rentId;
        this.clientName = clientName;
        this.toolName = toolName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.late = late;
    }
}
