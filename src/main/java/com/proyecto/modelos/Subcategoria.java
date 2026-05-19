package com.proyecto.modelos;

public class Subcategoria {
    private int idSubcategoria;
    private String nombre;
    // Valor calculado para mostrar cuantos usuarios pertenecen a esta subcategoria.
    private int usuariosAsociados;

    public Subcategoria(int idSubcategoria, String nombre, int usuariosAsociados) {
        this.idSubcategoria = idSubcategoria;
        this.nombre = nombre;
        this.usuariosAsociados = usuariosAsociados;
    }

    public Subcategoria(int idSubcategoria, String nombre) {
        this(idSubcategoria, nombre, 0);
    }

    public Subcategoria(String nombre) {
        this(0, nombre, 0);
    }

    public int getIdSubcategoria() {
        return idSubcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public int getUsuariosAsociados() {
        return usuariosAsociados;
    }

    public void setIdSubcategoria(int idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUsuariosAsociados(int usuariosAsociados) {
        this.usuariosAsociados = usuariosAsociados;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
