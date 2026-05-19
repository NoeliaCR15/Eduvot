package com.proyecto.modelos;

public class Usuario {
    private int idUsuario;
    private String dni;
    private String nombreUsuario;
    private String password;
    private boolean esAdministrador;
    private boolean activo;
    // Curso o grupo academico del usuario. Puede quedar vacio para perfiles sin curso.
    private String grupo;

    public Usuario(int idUsuario, String dni, String nombreUsuario, String password, boolean esAdministrador, boolean activo, String grupo) {
        this.idUsuario = idUsuario;
        this.dni = dni;
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.esAdministrador = esAdministrador;
        this.activo = activo;
        this.grupo = grupo;
    }

    public Usuario(String dni, String nombreUsuario, String password, boolean esAdministrador, boolean activo, String grupo) {
        this(0, dni, nombreUsuario, password, esAdministrador, activo, grupo);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getDni() {
        return dni;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    @Override
    public String toString() {
        // Texto usado en desplegables, por ejemplo al vincular familia/tutor con alumnado.
        return nombreUsuario + " (" + dni + ")";
    }
}
