package com.proyecto.modelos;

import java.time.LocalDateTime;

public class Encuesta {
    private int idEncuesta;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean activa;
    private String tipoEncuesta;
    private int creadaPor;
    private int totalOpciones;
    private int totalVotos;

    public Encuesta(int idEncuesta, String titulo, String descripcion, LocalDateTime fechaInicio,
            LocalDateTime fechaFin, boolean activa, String tipoEncuesta, int creadaPor,
            int totalOpciones, int totalVotos) {
        this.idEncuesta = idEncuesta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
        this.tipoEncuesta = tipoEncuesta;
        this.creadaPor = creadaPor;
        this.totalOpciones = totalOpciones;
        this.totalVotos = totalVotos;
    }

    public Encuesta(String titulo, String descripcion, LocalDateTime fechaInicio,
            LocalDateTime fechaFin, boolean activa, String tipoEncuesta, int creadaPor) {
        this(0, titulo, descripcion, fechaInicio, fechaFin, activa, tipoEncuesta, creadaPor, 0, 0);
    }

    public int getIdEncuesta() {
        return idEncuesta;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public String getTipoEncuesta() {
        return tipoEncuesta;
    }

    public int getCreadaPor() {
        return creadaPor;
    }

    public int getTotalOpciones() {
        return totalOpciones;
    }

    public int getTotalVotos() {
        return totalVotos;
    }

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public void setTipoEncuesta(String tipoEncuesta) {
        this.tipoEncuesta = tipoEncuesta;
    }

    public void setCreadaPor(int creadaPor) {
        this.creadaPor = creadaPor;
    }

    public void setTotalOpciones(int totalOpciones) {
        this.totalOpciones = totalOpciones;
    }

    public void setTotalVotos(int totalVotos) {
        this.totalVotos = totalVotos;
    }
}
