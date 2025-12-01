package com.Tingeso.ToolRent.DTOs;

public class LateClientReportDTO {
    public Long clientId;
    public String clientName;
    public String rut;
    public Long totalLateDays;
    public Long totalLateOccurrences;

    public LateClientReportDTO(Long clientId, String clientName, String rut,
                               Long totalLateDays, Long totalLateOccurrences) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.rut = rut;
        this.totalLateDays = totalLateDays;
        this.totalLateOccurrences = totalLateOccurrences;
    }
}
