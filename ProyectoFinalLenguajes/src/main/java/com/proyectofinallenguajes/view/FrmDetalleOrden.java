package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.detalleOrdenDAO;
import com.proyectofinallenguajes.model.detalleOrden;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmDetalleOrden extends JFrame {

    private JTable tabla;
    private JTextField txtIdDetalle;
    private JTextField txtIdOrden;
    private JTextField txtIdItem;
    private JTextField txtCantidad;
    private JTextField txtFiltroOrden;

    private JButton btnCargar;
    private JButton btnBuscarPorOrden;
    private JButton btnInsertar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final detalleOrdenDAO dao = new detalleOrdenDAO();

    public FrmDetalleOrden() {
        setTitle("CRUD Detalle de Orden");
        setSize(1000, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 210, 940, 230);

        txtIdDetalle = new JTextField();
        txtIdOrden = new JTextField();
        txtIdItem = new JTextField();
        txtCantidad = new JTextField();
        txtFiltroOrden = new JTextField();

        txtIdDetalle.setBounds(20, 20, 100, 25);
        txtIdOrden.setBounds(140, 20, 100, 25);
        txtIdItem.setBounds(260, 20, 100, 25);
        txtCantidad.setBounds(380, 20, 120, 25);
        txtFiltroOrden.setBounds(20, 90, 120, 25);

        txtIdDetalle.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnBuscarPorOrden = new JButton("Buscar por orden");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(160, 90, 120, 30);
        btnBuscarPorOrden.setBounds(300, 90, 150, 30);
        btnInsertar.setBounds(470, 90, 110, 30);
        btnActualizar.setBounds(600, 90, 120, 30);
        btnEliminar.setBounds(740, 90, 110, 30);
        btnLimpiar.setBounds(860, 90, 100, 30);

        add(scroll);
        add(txtIdDetalle);
        add(txtIdOrden);
        add(txtIdItem);
        add(txtCantidad);
        add(txtFiltroOrden);

        add(btnCargar);
        add(btnBuscarPorOrden);
        add(btnInsertar);
        add(btnActualizar);
        add(btnEliminar);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargarTodos());
        btnBuscarPorOrden.addActionListener(e -> buscarPorOrden());
        btnInsertar.addActionListener(e -> insertarDetalle());
        btnActualizar.addActionListener(e -> actualizarDetalle());
        btnEliminar.addActionListener(e -> eliminarDetalle());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtIdDetalle.setText(tabla.getValueAt(fila, 0).toString());
                    txtIdOrden.setText(tabla.getValueAt(fila, 1).toString());
                    txtCantidad.setText(tabla.getValueAt(fila, 3).toString());
                    txtIdItem.setText("");
                }
            }
        });

        cargarTodos();
    }

    private void cargarTodos() {
        try {
            cargarTabla(dao.listarDetalles());
        } catch (SQLException e) {
            mostrarError("Error al cargar detalles", e);
        }
    }

    private void buscarPorOrden() {
        if (txtFiltroOrden.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID de orden para filtrar.");
            return;
        }

        try {
            int idOrden = Integer.parseInt(txtFiltroOrden.getText().trim());
            cargarTabla(dao.listarDetallesPorOrden(idOrden));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID de orden debe ser numérico.");
        } catch (SQLException e) {
            mostrarError("Error al buscar detalles por orden", e);
        }
    }

    private void insertarDetalle() {
        if (txtIdOrden.getText().trim().isEmpty()
                || txtIdItem.getText().trim().isEmpty()
                || txtCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete ID orden, ID item y cantidad.");
            return;
        }

        try {
            int idOrden = Integer.parseInt(txtIdOrden.getText().trim());
            int idItem = Integer.parseInt(txtIdItem.getText().trim());
            BigDecimal cantidad = new BigDecimal(txtCantidad.getText().trim());

            dao.insertarDetalle(idOrden, idItem, cantidad);
            JOptionPane.showMessageDialog(this, "Detalle insertado correctamente.");
            cargarTodos();
            limpiarCampos();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los valores numéricos no son válidos.");
        } catch (SQLException e) {
            mostrarError("Error al insertar detalle", e);
        }
    }

    private void actualizarDetalle() {
        if (txtIdDetalle.getText().trim().isEmpty()
                || txtCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un detalle y escriba la nueva cantidad.");
            return;
        }

        try {
            int idDetalle = Integer.parseInt(txtIdDetalle.getText().trim());
            BigDecimal cantidad = new BigDecimal(txtCantidad.getText().trim());

            dao.actualizarDetalle(idDetalle, cantidad);
            JOptionPane.showMessageDialog(this, "Detalle actualizado correctamente.");
            cargarTodos();
            limpiarCampos();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser numérica.");
        } catch (SQLException e) {
            mostrarError("Error al actualizar detalle", e);
        }
    }

    private void eliminarDetalle() {
        if (txtIdDetalle.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un detalle de la tabla.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar este detalle?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int idDetalle = Integer.parseInt(txtIdDetalle.getText().trim());
            dao.eliminarDetalle(idDetalle);
            JOptionPane.showMessageDialog(this, "Detalle eliminado correctamente.");
            cargarTodos();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarError("Error al eliminar detalle", e);
        }
    }

    private void cargarTabla(List<detalleOrden> lista) {
        String[] columnas = {"ID Detalle", "ID Orden", "Producto", "Cantidad", "Precio Unitario", "Subtotal"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (detalleOrden obj : lista) {
            Object[] fila = {
                obj.getId_detalle(),
                obj.getId_orden(),
                obj.getProducto(),
                obj.getCantidad(),
                obj.getPrecioUnitario(),
                obj.getSubtotal()
            };
            modelo.addRow(fila);
        }

        tabla.setModel(modelo);
    }

    private void limpiarCampos() {
        txtIdDetalle.setText("");
        txtIdOrden.setText("");
        txtIdItem.setText("");
        txtCantidad.setText("");
        tabla.clearSelection();
    }

    private void mostrarError(String titulo, SQLException e) {
        JOptionPane.showMessageDialog(
                this,
                titulo + ":\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
