package com.proyectofinallenguajes.model;

import java.math.BigDecimal;

public class item {
    private int id_item;
    private String nombre;
    private String descripcion;
    private String unidad_medida;
    private BigDecimal precio_unitario;
    private int id_estado;
    
    public item() {
    }

    public item(int id_item, String nombre, String descripcion, String unidad_medida, BigDecimal precio_unitario,
            int id_estado) {
        this.id_item = id_item;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad_medida = unidad_medida;
        this.precio_unitario = precio_unitario;
        this.id_estado = id_estado;
    }
    
    public int getId_item() {
        return id_item;
    }
    public void setId_item(int id_item) {
        this.id_item = id_item;
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
    public String getUnidad_medida() {
        return unidad_medida;
    }
    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }
    public BigDecimal getPrecio_unitario() {
        return precio_unitario;
    }
    public void setPrecio_unitario(BigDecimal precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
    public int getId_estado() {
        return id_estado;
    }
    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }



}
