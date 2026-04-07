package com.proyectofinallenguajes.model;

import java.time.LocalDateTime;

public class orden {
    private int id_orden;
    private LocalDateTime fecha;
    private int id_cliente;
    private int id_usuario;
    private int id_estado;
    
    public orden() {
    }

    public orden(int id_orden, LocalDateTime fecha, int id_cliente, int id_usuario, int id_estado) {
        this.id_orden = id_orden;
        this.fecha = fecha;
        this.id_cliente = id_cliente;
        this.id_usuario = id_usuario;
        this.id_estado = id_estado;
    }
    
    public int getId_orden() {
        return id_orden;
    }
    public void setId_orden(int id_orden) {
        this.id_orden = id_orden;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    public int getId_cliente() {
        return id_cliente;
    }
    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }
    public int getId_usuario() {
        return id_usuario;
    }
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    public int getId_estado() {
        return id_estado;
    }
    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }
}
