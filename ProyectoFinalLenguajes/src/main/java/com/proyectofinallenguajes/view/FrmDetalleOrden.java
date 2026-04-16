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

        setTitle("Gestión de Detalle de Órdenes");
        setSize(1050, 580);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // LABELS
        JLabel lblIdDetalle = new JLabel("ID Detalle:");
        JLabel lblIdOrden = new JLabel("ID Orden:");
        JLabel lblIdItem = new JLabel("ID Item:");
        JLabel lblCantidad = new JLabel("Cantidad:");
        JLabel lblFiltroOrden = new JLabel("Filtrar por ID Orden:");

        lblIdDetalle.setBounds(20, 20, 100, 20);
        lblIdOrden.setBounds(140, 20, 100, 20);
        lblIdItem.setBounds(260, 20, 100, 20);
        lblCantidad.setBounds(380, 20, 100, 20);
        lblFiltroOrden.setBounds(20, 75, 150, 20);

        // TEXTFIELDS
        txtIdDetalle = new JTextField();
        txtIdOrden = new JTextField();
        txtIdItem = new JTextField();
        txtCantidad = new JTextField();
        txtFiltroOrden = new JTextField();

        txtIdDetalle.setBounds(20, 45, 100, 25);
        txtIdOrden.setBounds(140, 45, 100, 25);
        txtIdItem.setBounds(260, 45, 100, 25);
        txtCantidad.setBounds(380, 45, 120, 25);
        txtFiltroOrden.setBounds(20, 100, 120, 25);

        txtIdDetalle.setEditable(false);

        // BUTTONS
        btnCargar = new JButton("Cargar Todos");
        btnBuscarPorOrden = new JButton("Buscar por Orden");
        btnInsertar = new JButton("Insertar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(160, 98, 130, 30);
        btnBuscarPorOrden.setBounds(310, 98, 160, 30);
        btnInsertar.setBounds(490, 98, 110, 30);
        btnActualizar.setBounds(620, 98, 120, 30);
        btnEliminar.setBounds(760, 98, 110, 30);
        btnLimpiar.setBounds(890, 98, 110, 30);

        // TABLE
        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 150, 980, 360);

        // ADD COMPONENTS
        add(lblIdDetalle);
        add(lblIdOrden);
        add(lblIdItem);
        add(lblCantidad);
        add(lblFiltroOrden);

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
        add(scroll);

        // EVENTS
        btnCargar.addActionListener(e -> cargarTodos());
        btnBuscarPorOrden.addActionListener(e -> buscarPorOrden());
        btnInsertar.addActionListener(e -> insertarDetalle());
        btnActualizar.addActionListener(e -> actualizarDetalle());
        btnEliminar.addActionListener(e -> eliminarDetalle());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // TABLE SELECTION
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();

                if (fila != -1) {
                    txtIdDetalle.setText(tabla.getValueAt(fila, 0).toString());
                    txtIdOrden.setText(tabla.getValueAt(fila, 1).toString());
                    txtCantidad.setText(tabla.getValueAt(fila, 3).toString());

                    // ID item no viene en tabla porque no se muestra
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
            JOptionPane.showMessageDialog(this,
                    "Ingrese un ID de orden para filtrar.");
            return;
        }

        try {
            int idOrden = Integer.parseInt(txtFiltroOrden.getText().trim());
            cargarTabla(dao.listarDetallesPorOrden(idOrden));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El ID de orden debe ser numérico.");
        } catch (SQLException e) {
            mostrarError("Error al buscar detalles por orden", e);
        }
    }

    private void insertarDetalle() {
        if (txtIdOrden.getText().trim().isEmpty()
                || txtIdItem.getText().trim().isEmpty()
                || txtCantidad.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Complete ID Orden, ID Item y Cantidad.");
            return;
        }

        try {
            int idOrden = Integer.parseInt(txtIdOrden.getText().trim());
            int idItem = Integer.parseInt(txtIdItem.getText().trim());
            BigDecimal cantidad = new BigDecimal(txtCantidad.getText().trim());

            dao.insertarDetalle(idOrden, idItem, cantidad);

            JOptionPane.showMessageDialog(this,
                    "Detalle insertado correctamente.");

            cargarTodos();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Los valores numéricos no son válidos.");
        } catch (SQLException e) {
            mostrarError("Error al insertar detalle", e);
        }
    }

    private void actualizarDetalle() {
        if (txtIdDetalle.getText().trim().isEmpty()
                || txtCantidad.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Seleccione un detalle y escriba nueva cantidad.");
            return;
        }

        try {
            int idDetalle = Integer.parseInt(txtIdDetalle.getText().trim());
            BigDecimal cantidad = new BigDecimal(txtCantidad.getText().trim());

            dao.actualizarDetalle(idDetalle, cantidad);

            JOptionPane.showMessageDialog(this,
                    "Detalle actualizado correctamente.");

            cargarTodos();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser numérica.");
        } catch (SQLException e) {
            mostrarError("Error al actualizar detalle", e);
        }
    }

    private void eliminarDetalle() {
        if (txtIdDetalle.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un detalle de la tabla.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar este detalle?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int idDetalle = Integer.parseInt(txtIdDetalle.getText().trim());

            dao.eliminarDetalle(idDetalle);

            JOptionPane.showMessageDialog(this,
                    "Detalle eliminado correctamente.");

            cargarTodos();
            limpiarCampos();

        } catch (SQLException e) {
            mostrarError("Error al eliminar detalle", e);
        }
    }

    private void cargarTabla(List<detalleOrden> lista) {

        String[] columnas = {
                "ID Detalle",
                "ID Orden",
                "Producto",
                "Cantidad",
                "Precio Unitario",
                "Subtotal (₡) "
        };

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (detalleOrden obj : lista) {
            modelo.addRow(new Object[]{
                    obj.getId_detalle(),
                    obj.getId_orden(),
                    obj.getProducto(),
                    obj.getCantidad(),
                    obj.getPrecioUnitario(),
                    obj.getSubtotal()
            });
        }

        tabla.setModel(modelo);
    }

    private void limpiarCampos() {
        txtIdDetalle.setText("");
        txtIdOrden.setText("");
        txtIdItem.setText("");
        txtCantidad.setText("");
        txtFiltroOrden.setText("");
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