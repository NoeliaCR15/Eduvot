package com.proyecto.modelos;

import java.time.LocalDateTime;

public class ParticipacionUsuario {
    private int idVoto;
    private String tituloEncuesta;
    private String tipoEncuesta;
    private LocalDateTime fechaVoto;
    private String opcionesElegidas;
    private String codigoVerificacion;

    public ParticipacionUsuario(int idVoto, String tituloEncuesta, String tipoEncuesta,
            LocalDateTime fechaVoto, String opcionesElegidas, String codigoVerificacion) {
        this.idVoto = idVoto;
        this.tituloEncuesta = tituloEncuesta;
        this.tipoEncuesta = tipoEncuesta;
        this.fechaVoto = fechaVoto;
        this.opcionesElegidas = opcionesElegidas;
        this.codigoVerificacion = codigoVerificacion;
    }

    public int getIdVoto() {
        return idVoto;
    }

    public String getTituloEncuesta() {
        return tituloEncuesta;
    }

    public String getTipoEncuesta() {
        return tipoEncuesta;
    }

    public LocalDateTime getFechaVoto() {
        return fechaVoto;
    }

    public String getOpcionesElegidas() {
        return opcionesElegidas;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }
}
