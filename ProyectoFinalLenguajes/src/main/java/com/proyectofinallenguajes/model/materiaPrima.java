package com.proyectofinallenguajes.model;

import java.math.BigDecimal;

public class materiaPrima {
    private int id_materia_prima;
    private int id_proveedor;
    private BigDecimal precio_referencia;
    private String nombre_materia_prima;
    private String proveedor;

    public materiaPrima() {
    }

    public materiaPrima(int id_materia_prima, int id_proveedor, BigDecimal precio_referencia,
            String nombre_materia_prima) {
        this.id_materia_prima = id_materia_prima;
        this.id_proveedor = id_proveedor;
        this.precio_referencia = precio_referencia;
        this.nombre_materia_prima = nombre_materia_prima;
    }

    public materiaPrima(int id_materia_prima, String nombre_materia_prima,
            BigDecimal precio_referencia, String proveedor) {
        this.id_materia_prima = id_materia_prima;
        this.nombre_materia_prima = nombre_materia_prima;
        this.precio_referencia = precio_referencia;
        this.proveedor = proveedor;
    }

    public int getId_materia_prima() {
        return id_materia_prima;
    }

    public void setId_materia_prima(int id_materia_prima) {
        this.id_materia_prima = id_materia_prima;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public BigDecimal getPrecio_referencia() {
        return precio_referencia;
    }

    public void setPrecio_referencia(BigDecimal precio_referencia) {
        this.precio_referencia = precio_referencia;
    }

    public String getNombre_materia_prima() {
        return nombre_materia_prima;
    }

    public void setNombre_materia_prima(String nombre_materia_prima) {
        this.nombre_materia_prima = nombre_materia_prima;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
}
