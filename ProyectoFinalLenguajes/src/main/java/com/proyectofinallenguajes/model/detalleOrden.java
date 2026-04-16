package com.proyectofinallenguajes.model;

import java.math.BigDecimal;

public class detalleOrden {
    private int id_orden;
    private int id_detalle;
    private int id_item;
    private BigDecimal cantidad;
    private String producto;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public detalleOrden() {
    }

    public detalleOrden(int id_orden, int id_detalle, int id_item, BigDecimal cantidad) {
        this.id_orden = id_orden;
        this.id_detalle = id_detalle;
        this.id_item = id_item;
        this.cantidad = cantidad;
    }

    public detalleOrden(int id_detalle, int id_orden, String producto,
                        BigDecimal cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.id_detalle = id_detalle;
        this.id_orden = id_orden;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
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

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
