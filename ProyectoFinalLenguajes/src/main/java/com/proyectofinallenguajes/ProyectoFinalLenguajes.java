package com.proyectofinallenguajes;

import com.proyectofinallenguajes.view.MenuPrincipal;

public class ProyectoFinalLenguajes {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}