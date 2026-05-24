package com.proyecto.modelos;

public class ResultadoOpcion {
    private int idOpcion;
    private String textoOpcion;
    private int totalVotos;

    public ResultadoOpcion(int idOpcion, String textoOpcion, int totalVotos) {
        this.idOpcion = idOpcion;
        this.textoOpcion = textoOpcion;
        this.totalVotos = totalVotos;
    }

    public int getIdOpcion() {
        return idOpcion;
    }

    public String getTextoOpcion() {
        return textoOpcion;
    }

    public int getTotalVotos() {
        return totalVotos;
    }
}
