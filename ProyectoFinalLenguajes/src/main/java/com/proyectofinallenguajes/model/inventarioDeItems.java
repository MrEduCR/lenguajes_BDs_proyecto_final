package com.proyectofinallenguajes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class inventarioDeItems {
    private int id_lote;
    private int id_item;
    private BigDecimal cantidad;
    private LocalDateTime fecha_ingreso;
    private LocalDateTime fecha_vencimiento;

    public inventarioDeItems() {

    }
    public inventarioDeItems(int id_lote, int id_item, BigDecimal cantidad, LocalDateTime fecha_ingreso,
            LocalDateTime fecha_vencimiento) {
        this.id_lote = id_lote;
        this.id_item = id_item;
        this.cantidad = cantidad;
        this.fecha_ingreso = fecha_ingreso;
        this.fecha_vencimiento = fecha_vencimiento;
    }
    public int getId_lote() {
        return id_lote;
    }
    public void setId_lote(int id_lote) {
        this.id_lote = id_lote;
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
    public LocalDateTime getFecha_ingreso() {
        return fecha_ingreso;
    }
    public void setFecha_ingreso(LocalDateTime fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }
    public LocalDateTime getFecha_vencimiento() {
        return fecha_vencimiento;
    }
    public void setFecha_vencimiento(LocalDateTime fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }
}
