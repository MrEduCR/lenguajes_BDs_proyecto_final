package com.proyectofinallenguajes.model;

import java.time.LocalDateTime;

public class auditoria {
    private int id_auditoria;
    private String tabla_afectada;
    private String operacion;
    private String valor_anterior;
    private String valor_nuevo;
    private LocalDateTime fecha;

    public auditoria() {
    }
    
    public auditoria(int id_auditoria, String tabla_afectada, String operacion, String valor_anterior,
            String valor_nuevo, LocalDateTime fecha) {
        this.id_auditoria = id_auditoria;
        this.tabla_afectada = tabla_afectada;
        this.operacion = operacion;
        this.valor_anterior = valor_anterior;
        this.valor_nuevo = valor_nuevo;
        this.fecha = fecha;
    }

    public int getId_auditoria() {
        return id_auditoria;
    }
    public void setId_auditoria(int id_auditoria) {
        this.id_auditoria = id_auditoria;
    }
    public String getTabla_afectada() {
        return tabla_afectada;
    }
    public void setTabla_afectada(String tabla_afectada) {
        this.tabla_afectada = tabla_afectada;
    }
    public String getOperacion() {
        return operacion;
    }
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }
    public String getValor_anterior() {
        return valor_anterior;
    }
    public void setValor_anterior(String valor_anterior) {
        this.valor_anterior = valor_anterior;
    }
    public String getValor_nuevo() {
        return valor_nuevo;
    }
    public void setValor_nuevo(String valor_nuevo) {
        this.valor_nuevo = valor_nuevo;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }


}
