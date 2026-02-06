package com.marcelo.backend.dto;

import java.util.UUID;

public class ReportesProyectosDashboardDTO {

    private UUID programadorId;
    private String programadorNombre;

    private long activos;
    private long inactivos;
    private long total;

    public ReportesProyectosDashboardDTO() {}

    public ReportesProyectosDashboardDTO(UUID programadorId, String programadorNombre,
                                         long activos, long inactivos, long total) {
        this.programadorId = programadorId;
        this.programadorNombre = programadorNombre;
        this.activos = activos;
        this.inactivos = inactivos;
        this.total = total;
    }

    public UUID getProgramadorId() { return programadorId; }
    public void setProgramadorId(UUID programadorId) { this.programadorId = programadorId; }

    public String getProgramadorNombre() { return programadorNombre; }
    public void setProgramadorNombre(String programadorNombre) { this.programadorNombre = programadorNombre; }

    public long getActivos() { return activos; }
    public void setActivos(long activos) { this.activos = activos; }

    public long getInactivos() { return inactivos; }
    public void setInactivos(long inactivos) { this.inactivos = inactivos; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
