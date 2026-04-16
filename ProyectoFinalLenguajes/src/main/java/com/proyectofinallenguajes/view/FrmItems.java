package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.itemDAO;
import com.proyectofinallenguajes.model.item;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmItems extends JFrame {

    private JTable tabla;
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtUnidadMedida;
    private JTextField txtPrecio;

    private JButton btnCargar;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final itemDAO dao = new itemDAO();

    public FrmItems() {
        setTitle("CRUD Items");
        setSize(1000, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 200, 940, 220);

        txtId = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();
        txtUnidadMedida = new JTextField();
        txtPrecio = new JTextField();

        txtId.setBounds(20, 20, 80, 25);
        txtNombre.setBounds(120, 20, 160, 25);
        txtDescripcion.setBounds(300, 20, 200, 25);
        txtUnidadMedida.setBounds(520, 20, 120, 25);
        txtPrecio.setBounds(660, 20, 120, 25);

        txtId.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 100, 120, 30);
        btnInsertar.setBounds(160, 100, 120, 30);
        btnActualizar.setBounds(300, 100, 120, 30);
        btnEliminar.setBounds(440, 100, 120, 30);
        btnLimpiar.setBounds(580, 100, 120, 30);

        add(scroll);
        add(txtId);
        add(txtNombre);
        add(txtDescripcion);
        add(txtUnidadMedida);
        add(txtPrecio);
        add(btnCargar);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargar());

        btnInsertar.addActionListener(e -> insertarItem());

        btnActualizar.addActionListener(e -> actualizarItem());

        btnEliminar.addActionListener(e -> eliminarItem());

        btnLimpiar.addActionListener(e -> limpiarCampos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtId.setText(tabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(tabla.getValueAt(fila, 1).toString());
                    txtDescripcion.setText(tabla.getValueAt(fila, 2).toString());
                    txtUnidadMedida.setText(tabla.getValueAt(fila, 3).toString());
                    txtPrecio.setText(tabla.getValueAt(fila, 4).toString());
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<item> lista = dao.listarItems();

            String[] columnas = {"ID", "Nombre", "Descripción", "Unidad", "Precio", "Estado"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (item obj : lista) {
                Object[] fila = {
                    obj.getId_item(),
                    obj.getNombre(),
                    obj.getDescripcion(),
                    obj.getUnidad_medida(),
                    obj.getPrecio_unitario(),
                    obj.getEstado()
                };
                modelo.addRow(fila);
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar items:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertarItem() {
        if (txtNombre.getText().trim().isEmpty()
                || txtDescripcion.getText().trim().isEmpty()
                || txtUnidadMedida.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Debe completar todos los campos.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());

            int idNuevo = dao.insertarItem(
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(),
                    txtUnidadMedida.getText().trim(),
                    precio,
                    1
            );

            JOptionPane.showMessageDialog(this,
                    "Item insertado correctamente. ID generado: " + idNuevo);

            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El precio debe ser numérico.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar item:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarItem() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un item de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());

            dao.actualizarItem(
                    Integer.parseInt(txtId.getText().trim()),
                    txtNombre.getText().trim(),
                    precio
            );

            JOptionPane.showMessageDialog(this, "Item actualizado correctamente.");
            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El precio debe ser numérico.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar item:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarItem() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un item de la tabla.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar lógicamente este item?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.eliminarItem(Integer.parseInt(txtId.getText().trim()));
            JOptionPane.showMessageDialog(this, "Item eliminado correctamente.");
            cargar();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar item:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtUnidadMedida.setText("");
        txtPrecio.setText("");
        tabla.clearSelection();
    }
}
