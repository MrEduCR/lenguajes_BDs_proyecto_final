package com.proyectofinallenguajes.model;

public class usuario {
    private int id_usuario;
    private String nombre;
    private String contrasena;
    private int id_rol;
    private int id_estado;

    public usuario() {
    }
    
    public usuario(int id_usuario, String nombre, String contrasena, int id_rol, int id_estado) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.id_rol = id_rol;
        this.id_estado = id_estado;
    }
    public int getId_usuario() {
        return id_usuario;
    }
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    public int getId_rol() {
        return id_rol;
    }
    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }
    public int getId_estado() {
        return id_estado;
    }
    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }
}
