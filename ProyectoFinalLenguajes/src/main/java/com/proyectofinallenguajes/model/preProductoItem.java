package com.proyectofinallenguajes.model;

import java.math.BigDecimal;

public class preProductoItem {
    private int id_pre_producto;
    private int id_item;
    private int id_materia_prima;
    private BigDecimal medida_materia_prima;
    private String unidad_medida;

    public preProductoItem() {
    }

    public preProductoItem(int id_pre_producto, int id_item, int id_materia_prima, BigDecimal medida_materia_prima,
            String unidad_medida) {
        this.id_pre_producto = id_pre_producto;
        this.id_item = id_item;
        this.id_materia_prima = id_materia_prima;
        this.medida_materia_prima = medida_materia_prima;
        this.unidad_medida = unidad_medida;
    }
    public int getId_pre_producto() {
        return id_pre_producto;
    }
    public void setId_pre_producto(int id_pre_producto) {
        this.id_pre_producto = id_pre_producto;
    }
    public int getId_item() {
        return id_item;
    }
    public void setId_item(int id_item) {
        this.id_item = id_item;
    }
    public int getId_materia_prima() {
        return id_materia_prima;
    }
    public void setId_materia_prima(int id_materia_prima) {
        this.id_materia_prima = id_materia_prima;
    }
    public BigDecimal getMedida_materia_prima() {
        return medida_materia_prima;
    }
    public void setMedida_materia_prima(BigDecimal medida_materia_prima) {
        this.medida_materia_prima = medida_materia_prima;
    }
    public String getUnidad_medida() {
        return unidad_medida;
    }
    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }

}
