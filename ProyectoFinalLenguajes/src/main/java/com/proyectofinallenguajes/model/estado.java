package com.proyectofinallenguajes.model;

public class estado {
    private int id_estado;
    private String nombre;
    private String descripcion;
    

    public estado() {
    }
    
    public estado(int id_estado, String nombre, String descripcion) {
        this.id_estado = id_estado;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId_estado() {
        return id_estado;
    }
    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
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


}
