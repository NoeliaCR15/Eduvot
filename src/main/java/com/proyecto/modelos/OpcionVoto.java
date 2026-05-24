package com.proyecto.modelos;

public class OpcionVoto {
    private int idOpcion;
    private int idEncuesta;
    private String textoOpcion;

    public OpcionVoto(int idOpcion, int idEncuesta, String textoOpcion) {
        this.idOpcion = idOpcion;
        this.idEncuesta = idEncuesta;
        this.textoOpcion = textoOpcion;
    }

    public int getIdOpcion() {
        return idOpcion;
    }

    public int getIdEncuesta() {
        return idEncuesta;
    }

    public String getTextoOpcion() {
        return textoOpcion;
    }

    @Override
    public String toString() {
        return textoOpcion;
    }
}
