package com.proyectofinallenguajes.model;

import java.math.BigDecimal;

public class detalleOrden {
    private int id_orden;
    private int id_detalle;
    private int id_item;
    private BigDecimal cantidad;

    public detalleOrden() {
       
    }

    public detalleOrden(int id_orden, int id_detalle, int id_item, BigDecimal cantidad) {
        this.id_orden = id_orden;
        this.id_detalle = id_detalle;
        this.id_item = id_item;
        this.cantidad = cantidad;
    }
    public int getId_orden() {
        return id_orden;
    }
    public void setId_orden(int id_orden) {
        this.id_orden = id_orden;
    }
    public int getId_detalle() {
        return id_detalle;
    }
    public void setId_detalle(int id_detalle) {
        this.id_detalle = id_detalle;
    }
    public int getId_item() {
        return id_item;
    }
    public void setId_item(int id_item) {
        this.id_item = id_item;
    }
    public BigDecimal getCantidad() {
        return cantidad;
    }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }


}
