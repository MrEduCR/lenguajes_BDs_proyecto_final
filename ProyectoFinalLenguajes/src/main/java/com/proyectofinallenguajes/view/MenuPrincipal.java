package com.proyectofinallenguajes.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MenuPrincipal extends JFrame {

    private JLabel lblTitulo;

    private JButton btnClientes;
    private JButton btnItems;
    private JButton btnEstados;
    private JButton btnRoles;
    private JButton btnUsuarios;
    private JButton btnProveedores;
    private JButton btnOrdenes;
    private JButton btnDetalleOrden;
    private JButton btnInventario;
    private JButton btnMateriaPrima;
    private JButton btnAuditoria;

    public MenuPrincipal() {
        setTitle("Sistema Suproli - Menú Principal");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        lblTitulo = new JLabel("MENÚ PRINCIPAL");
        lblTitulo.setBounds(270, 20, 200, 30);

        btnClientes = new JButton("Clientes");
        btnItems = new JButton("Items");
        btnEstados = new JButton("Estados");
        btnRoles = new JButton("Roles");
        btnUsuarios = new JButton("Usuarios");
        btnProveedores = new JButton("Proveedores");
        btnOrdenes = new JButton("Órdenes");
        btnDetalleOrden = new JButton("Detalle Orden");
        btnInventario = new JButton("Inventario");
        btnMateriaPrima = new JButton("Materia Prima");
        btnAuditoria = new JButton("Auditoría");

        btnClientes.setBounds(80, 80, 180, 40);
        btnItems.setBounds(400, 80, 180, 40);

        btnEstados.setBounds(80, 140, 180, 40);
        btnRoles.setBounds(400, 140, 180, 40);

        btnUsuarios.setBounds(80, 200, 180, 40);
        btnProveedores.setBounds(400, 200, 180, 40);

        btnOrdenes.setBounds(80, 260, 180, 40);
        btnDetalleOrden.setBounds(400, 260, 180, 40);

        btnInventario.setBounds(80, 320, 180, 40);
        btnMateriaPrima.setBounds(400, 320, 180, 40);

        btnAuditoria.setBounds(400, 380, 180, 40);

        add(lblTitulo);

        add(btnClientes);
        add(btnItems);
        add(btnEstados);
        add(btnRoles);
        add(btnUsuarios);
        add(btnProveedores);
        add(btnOrdenes);
        add(btnDetalleOrden);
        add(btnInventario);
        add(btnMateriaPrima);
        add(btnAuditoria);

        btnClientes.addActionListener(e -> new FrmClientes().setVisible(true));
        btnItems.addActionListener(e -> new FrmItems().setVisible(true));
        btnEstados.addActionListener(e -> new FrmEstados().setVisible(true));
        btnRoles.addActionListener(e -> new FrmRoles().setVisible(true));
        btnUsuarios.addActionListener(e -> new FrmUsuarios().setVisible(true));
        btnProveedores.addActionListener(e -> new FrmProveedores().setVisible(true));
        btnOrdenes.addActionListener(e -> new FrmOrdenes().setVisible(true));
        btnDetalleOrden.addActionListener(e -> new FrmDetalleOrden().setVisible(true));
        btnInventario.addActionListener(e -> new FrmInventarioDeItems().setVisible(true));
        btnMateriaPrima.addActionListener(e -> new FrmMateriaPrima().setVisible(true));
        btnAuditoria.addActionListener(e -> new FrmAuditoria().setVisible(true));
    }
}
