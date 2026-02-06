package com.marcelo.backend.dto;

import java.util.UUID;

public class ReportesAsesoriasDashboardDTO {

    private UUID programadorId;
    private String programadorNombre;

    private long pendiente;
    private long aprobada;
    private long rechazada;
    private long total;

    public ReportesAsesoriasDashboardDTO() {}

    public ReportesAsesoriasDashboardDTO(UUID programadorId, String programadorNombre,
                                         long pendiente, long aprobada, long rechazada, long total) {
        this.programadorId = programadorId;
        this.programadorNombre = programadorNombre;
        this.pendiente = pendiente;
        this.aprobada = aprobada;
        this.rechazada = rechazada;
        this.total = total;
    }

    public UUID getProgramadorId() { return programadorId; }
    public void setProgramadorId(UUID programadorId) { this.programadorId = programadorId; }

    public String getProgramadorNombre() { return programadorNombre; }
    public void setProgramadorNombre(String programadorNombre) { this.programadorNombre = programadorNombre; }

    public long getPendiente() { return pendiente; }
    public void setPendiente(long pendiente) { this.pendiente = pendiente; }

    public long getAprobada() { return aprobada; }
    public void setAprobada(long aprobada) { this.aprobada = aprobada; }

    public long getRechazada() { return rechazada; }
    public void setRechazada(long rechazada) { this.rechazada = rechazada; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
