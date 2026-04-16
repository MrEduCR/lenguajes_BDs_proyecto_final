package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.estadoDAO;
import com.proyectofinallenguajes.model.estado;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmEstados extends JFrame {

    private JTable tabla;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final estadoDAO dao = new estadoDAO();

    public FrmEstados() {

        setTitle("Gestión de Estados");
        setSize(850, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 190, 790, 230);

        JLabel lblId = new JLabel("ID Estado:");
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblDescripcion = new JLabel("Descripción:");

        lblId.setBounds(20, 20, 100, 20);
        lblNombre.setBounds(140, 20, 100, 20);
        lblDescripcion.setBounds(320, 20, 100, 20);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();

        txtId.setBounds(20, 45, 90, 25);
        txtNombre.setBounds(140, 45, 150, 25);
        txtDescripcion.setBounds(320, 45, 250, 25);

        txtId.setEditable(false);

    
        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 100, 100, 30);
        btnInsertar.setBounds(140, 100, 100, 30);
        btnActualizar.setBounds(260, 100, 120, 30);
        btnEliminar.setBounds(400, 100, 100, 30);
        btnLimpiar.setBounds(520, 100, 100, 30);

        // ADD COMPONENTS
        add(scroll);

        add(lblId);
        add(lblNombre);
        add(lblDescripcion);

        add(txtId);
        add(txtNombre);
        add(txtDescripcion);

        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnLimpiar);

        // EVENTOS
        btnCargar.addActionListener(e -> cargar());
        btnInsertar.addActionListener(e -> insertar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        // SELECCIÓN TABLA
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();

                if (fila != -1) {
                    txtId.setText(tabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    txtDescripcion.setText(tabla.getValueAt(fila, 2).toString());
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<estado> lista = dao.listarEstados();

            String[] columnas = {"ID", "Nombre", "Descripción"};

            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (estado e : lista) {
                modelo.addRow(new Object[]{
                        e.getId_estado(),
                        e.getNombre(),
                        e.getDescripcion()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar estados:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el nombre del estado.");
            return;
        }

        try {
            dao.insertarEstado(
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim()
            );

            JOptionPane.showMessageDialog(this,
                    "Estado insertado correctamente.");

            cargar();
            limpiar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un registro.");
            return;
        }

        try {
            dao.actualizarEstado(
                    Integer.parseInt(txtId.getText()),
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim()
            );

            JOptionPane.showMessageDialog(this,
                    "Estado actualizado correctamente.");

            cargar();
            limpiar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un registro.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar este estado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.eliminarEstado(
                    Integer.parseInt(txtId.getText())
            );

            JOptionPane.showMessageDialog(this,
                    "Estado eliminado correctamente.");

            cargar();
            limpiar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        tabla.clearSelection();
    }
}