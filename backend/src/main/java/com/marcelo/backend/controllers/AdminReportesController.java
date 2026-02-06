package com.marcelo.backend.controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.marcelo.backend.dto.ReportesAsesoriasDashboardDTO;
import com.marcelo.backend.dto.ReportesProyectosDashboardDTO;
import com.marcelo.backend.models.Asesoria;
import com.marcelo.backend.models.Proyecto;
import com.marcelo.backend.repositories.AsesoriaRepository;
import com.marcelo.backend.repositories.ProyectoRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/reportes")
@CrossOrigin(origins = "*")
public class AdminReportesController {

    @Autowired
    private AsesoriaRepository asesoriaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    // -----------------------
    // ASESORÍAS - DASHBOARD
    // -----------------------
    @GetMapping("/asesorias")
    public List<ReportesAsesoriasDashboardDTO> dashboardAsesorias(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID programadorId,
            @RequestParam(required = false) String estado
    ) {
        List<Object[]> rows = asesoriaRepository.dashboardAsesorias(from, to, programadorId, estado);
        List<ReportesAsesoriasDashboardDTO> out = new ArrayList<>();

        for (Object[] r : rows) {
            UUID pid = (UUID) r[0];
            String nombre = (String) r[1];
            long pendiente = ((Number) r[2]).longValue();
            long aprobada = ((Number) r[3]).longValue();
            long rechazada = ((Number) r[4]).longValue();
            long total = ((Number) r[5]).longValue();

            out.add(new ReportesAsesoriasDashboardDTO(pid, nombre, pendiente, aprobada, rechazada, total));
        }
        return out;
    }

    // -----------------------
    // PROYECTOS - DASHBOARD
    // -----------------------
    @GetMapping("/proyectos")
    public List<ReportesProyectosDashboardDTO> dashboardProyectos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) UUID programadorId,
            @RequestParam(required = false) String estado
    ) {
        List<Object[]> rows = proyectoRepository.dashboardProyectos(from, to, programadorId, estado);
        List<ReportesProyectosDashboardDTO> out = new ArrayList<>();

        for (Object[] r : rows) {
            UUID pid = (UUID) r[0];
            String nombre = (String) r[1];
            long activos = ((Number) r[2]).longValue();
            long inactivos = ((Number) r[3]).longValue();
            long total = ((Number) r[4]).longValue();
            out.add(new ReportesProyectosDashboardDTO(pid, nombre, activos, inactivos, total));
        }
        return out;
    }

    // =======================
    // EXPORT XLSX - ASESORÍAS
    // =======================
    @GetMapping("/asesorias/export/xlsx")
    public ResponseEntity<byte[]> exportAsesoriasXlsx(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID programadorId,
            @RequestParam(required = false) String estado
    ) throws Exception {

        List<Asesoria> data = asesoriaRepository.detalleAsesorias(from, to, programadorId, estado);
        if (data == null) data = Collections.emptyList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Asesorias");

            Font bold = wb.createFont();
            bold.setBold(true);

            CellStyle head = wb.createCellStyle();
            head.setFont(bold);

            int rowIdx = 0;

            Row h = sh.createRow(rowIdx++);
            String[] cols = {"Programador", "Solicitante", "Email", "Fecha", "Hora", "Estado", "Comentario", "Respuesta", "CreadoEn"};

            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell c = h.createCell(i); // ✅ POI Cell explícito
                c.setCellValue(cols[i]);
                c.setCellStyle(head);
            }

            for (Asesoria a : data) {
                Row r = sh.createRow(rowIdx++);

                String prog = (a.getProgramador() != null && a.getProgramador().getUsuario() != null)
                        ? a.getProgramador().getUsuario().getNombre()
                        : "Programador";

                r.createCell(0).setCellValue(nvl(prog));
                r.createCell(1).setCellValue(nvl(a.getNombreSolicitante()));
                r.createCell(2).setCellValue(nvl(a.getEmailSolicitante()));
                r.createCell(3).setCellValue(a.getFecha() != null ? a.getFecha().toString() : "");
                r.createCell(4).setCellValue(a.getHora() != null ? a.getHora().toString() : "");
                r.createCell(5).setCellValue(nvl(a.getEstado()));
                r.createCell(6).setCellValue(nvl(a.getComentario()));
                r.createCell(7).setCellValue(nvl(a.getRespuestaProgramador()));
                r.createCell(8).setCellValue(a.getCreadoEn() != null ? a.getCreadoEn().toString() : "");
            }

            for (int i = 0; i < cols.length; i++) sh.autoSizeColumn(i);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_asesorias.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(baos.toByteArray());
        }
    }

    // ====================
    // EXPORT PDF - ASESORÍAS
    // ====================
    @GetMapping("/asesorias/export/pdf")
    public ResponseEntity<byte[]> exportAsesoriasPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID programadorId,
            @RequestParam(required = false) String estado
    ) throws Exception {

        List<Asesoria> data = asesoriaRepository.detalleAsesorias(from, to, programadorId, estado);
        if (data == null) data = Collections.emptyList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Reporte de Asesorías").setBold().setFontSize(16));

        doc.add(new Paragraph("Filtros: " +
                "from=" + (from != null ? from : "-") +
                ", to=" + (to != null ? to : "-") +
                ", programadorId=" + (programadorId != null ? programadorId : "-") +
                ", estado=" + (estado != null ? estado : "-")
        ));

        Table t = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2, 2, 2, 4}))
                .useAllAvailableWidth();

        // ✅ iText Cell con nombre completo (sin import)
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Programador").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Solicitante").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Fecha").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Hora").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Estado").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Comentario").setBold()));

        for (Asesoria a : data) {
            String prog = (a.getProgramador() != null && a.getProgramador().getUsuario() != null)
                    ? a.getProgramador().getUsuario().getNombre()
                    : "Programador";

            t.addCell(nvl(prog));
            t.addCell(nvl(a.getNombreSolicitante()));
            t.addCell(a.getFecha() != null ? a.getFecha().toString() : "");
            t.addCell(a.getHora() != null ? a.getHora().toString() : "");
            t.addCell(nvl(a.getEstado()));
            t.addCell(nvl(a.getComentario()));
        }

        doc.add(t);
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_asesorias.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(baos.toByteArray());
    }

    // ======================
    // EXPORT XLSX - PROYECTOS
    // ======================
    @GetMapping("/proyectos/export/xlsx")
    public ResponseEntity<byte[]> exportProyectosXlsx(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) UUID programadorId,
            @RequestParam(required = false) String estado
    ) throws Exception {

        List<Proyecto> data = proyectoRepository.detalleProyectos(from, to, programadorId, estado);
        if (data == null) data = Collections.emptyList();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Proyectos");

            Font bold = wb.createFont();
            bold.setBold(true);

            CellStyle head = wb.createCellStyle();
            head.setFont(bold);

            int rowIdx = 0;

            Row h = sh.createRow(rowIdx++);
            String[] cols = {"Programador", "Titulo", "Tecnologias", "Estado", "Repo", "Demo", "CreadoEn"};

            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell c = h.createCell(i); // ✅ POI Cell explícito
                c.setCellValue(cols[i]);
                c.setCellStyle(head);
            }

            for (Proyecto p : data) {
                Row r = sh.createRow(rowIdx++);

                String prog = (p.getProgramador() != null && p.getProgramador().getUsuario() != null)
                        ? p.getProgramador().getUsuario().getNombre()
                        : "Programador";

                r.createCell(0).setCellValue(nvl(prog));
                r.createCell(1).setCellValue(nvl(p.getTitulo()));
                r.createCell(2).setCellValue(nvl(p.getTecnologias()));
                r.createCell(3).setCellValue(nvl(p.getEstado(), "sin_estado"));
                r.createCell(4).setCellValue(nvl(p.getUrlRepo()));
                r.createCell(5).setCellValue(nvl(p.getUrlDemo()));
                r.createCell(6).setCellValue(p.getCreadoEn() != null ? p.getCreadoEn().toString() : "");
            }

            for (int i = 0; i < cols.length; i++) sh.autoSizeColumn(i);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_proyectos.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(baos.toByteArray());
        }
    }

    // ====================
    // EXPORT PDF - PROYECTOS
    // ====================
    @GetMapping("/proyectos/export/pdf")
    public ResponseEntity<byte[]> exportProyectosPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) UUID programadorId,
            @RequestParam(required = false) String estado
    ) throws Exception {

        List<Proyecto> data = proyectoRepository.detalleProyectos(from, to, programadorId, estado);
        if (data == null) data = Collections.emptyList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Reporte de Proyectos").setBold().setFontSize(16));
        doc.add(new Paragraph("Filtros: " +
                "from=" + (from != null ? from : "-") +
                ", to=" + (to != null ? to : "-") +
                ", programadorId=" + (programadorId != null ? programadorId : "-") +
                ", estado=" + (estado != null ? estado : "-")
        ));

        Table t = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2, 2, 3}))
                .useAllAvailableWidth();

        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Programador").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Título").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Estado").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Creado").setBold()));
        t.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Tecnologías").setBold()));

        for (Proyecto p : data) {
            String prog = (p.getProgramador() != null && p.getProgramador().getUsuario() != null)
                    ? p.getProgramador().getUsuario().getNombre()
                    : "Programador";

            t.addCell(nvl(prog));
            t.addCell(nvl(p.getTitulo()));
            t.addCell(nvl(p.getEstado(), "sin_estado"));
            t.addCell(p.getCreadoEn() != null ? p.getCreadoEn().toString() : "");
            t.addCell(nvl(p.getTecnologias()));
        }

        doc.add(t);
        doc.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_proyectos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(baos.toByteArray());
    }

    private String nvl(String s) { return s == null ? "" : s; }
    private String nvl(String s, String def) { return (s == null || s.isBlank()) ? def : s; }
}
