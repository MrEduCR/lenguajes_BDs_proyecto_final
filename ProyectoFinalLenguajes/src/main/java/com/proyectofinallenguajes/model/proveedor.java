package com.proyectofinallenguajes.model;

public class proveedor {
    private int id_proveedor;
    private String nombre;
    private String contacto;
    private String telefono;
    private String correo;
    private int id_estado;

    public proveedor() {
    }

    public proveedor(int id_proveedor, String nombre, String contacto, String telefono, String correo, int id_estado) {
        this.id_proveedor = id_proveedor;
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.id_estado = id_estado;
    }
    public int getId_proveedor() {
        return id_proveedor;
    }
    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getContacto() {
        return contacto;
    }
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public int getId_estado() {
        return id_estado;
    }
    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }




}
