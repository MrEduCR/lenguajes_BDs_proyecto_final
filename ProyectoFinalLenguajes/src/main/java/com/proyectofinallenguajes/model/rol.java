package com.proyectofinallenguajes.model;

public class rol {

    private int id_rol;
    private String nombre;
    private String descripcion;
    private int id_estado;

    private String estado;

    public rol() {
    }

    public rol(int id_rol, String nombre, String descripcion, int id_estado) {
        this.id_rol = id_rol;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.id_estado = id_estado;
    }

    public rol(int id_rol, String nombre, String descripcion, int id_estado, String estado) {
        this.id_rol = id_rol;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.id_estado = id_estado;
        this.estado = estado;
    }

    public int getId_rol() {
        return id_rol;
    }

    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
