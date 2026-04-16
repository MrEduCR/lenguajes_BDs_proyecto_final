package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.inventarioDeItemsDAO;
import com.proyectofinallenguajes.model.inventarioDeItems;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmInventarioDeItems extends JFrame {

    private JTable tabla;

    private JTextField txtIdLote;
    private JTextField txtIdItem;
    private JTextField txtCantidad;
    private JTextField txtFechaVencimiento;
    private JTextField txtNuevaCantidad;
    private JTextField txtCantidadRebajar;

    private JButton btnCargar;
    private JButton btnIngresarLote;
    private JButton btnAjustar;
    private JButton btnRebajar;
    private JButton btnValorTotal;
    private JButton btnLimpiar;

    private final inventarioDeItemsDAO dao = new inventarioDeItemsDAO();

    public FrmInventarioDeItems() {
        setTitle("Inventario de Items");
        setSize(1100, 560);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 230, 1040, 250);

        txtIdLote = new JTextField();
        txtIdItem = new JTextField();
        txtCantidad = new JTextField();
        txtFechaVencimiento = new JTextField();
        txtNuevaCantidad = new JTextField();
        txtCantidadRebajar = new JTextField();

        txtIdLote.setBounds(20, 20, 100, 25);
        txtIdItem.setBounds(140, 20, 100, 25);
        txtCantidad.setBounds(260, 20, 120, 25);
        txtFechaVencimiento.setBounds(400, 20, 220, 25);
        txtNuevaCantidad.setBounds(640, 20, 120, 25);
        txtCantidadRebajar.setBounds(780, 20, 120, 25);

        txtIdLote.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnIngresarLote = new JButton("Ingresar lote");
        btnAjustar = new JButton("Ajustar lote");
        btnRebajar = new JButton("Rebajar item");
        btnValorTotal = new JButton("Valor total");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(20, 100, 120, 30);
        btnIngresarLote.setBounds(160, 100, 140, 30);
        btnAjustar.setBounds(320, 100, 140, 30);
        btnRebajar.setBounds(480, 100, 140, 30);
        btnValorTotal.setBounds(640, 100, 140, 30);
        btnLimpiar.setBounds(800, 100, 120, 30);

        add(scroll);
        add(txtIdLote);
        add(txtIdItem);
        add(txtCantidad);
        add(txtFechaVencimiento);
        add(txtNuevaCantidad);
        add(txtCantidadRebajar);

        add(btnCargar);
        add(btnIngresarLote);
        add(btnAjustar);
        add(btnRebajar);
        add(btnValorTotal);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> cargar());
        btnIngresarLote.addActionListener(e -> ingresarLote());
        btnAjustar.addActionListener(e -> ajustarLote());
        btnRebajar.addActionListener(e -> rebajarItem());
        btnValorTotal.addActionListener(e -> mostrarValorTotal());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtIdLote.setText(tabla.getValueAt(fila, 0).toString());
                    txtIdItem.setText(tabla.getValueAt(fila, 1).toString());
                    txtCantidad.setText(tabla.getValueAt(fila, 3).toString());
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<inventarioDeItems> lista = dao.listarInventario();

            String[] columnas = {"ID Lote", "ID Item", "Producto", "Cantidad", "Fecha Ingreso", "Fecha Vencimiento"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (inventarioDeItems obj : lista) {
                modelo.addRow(new Object[]{
                    obj.getId_lote(),
                    obj.getId_item(),
                    obj.getProducto(),
                    obj.getCantidad(),
                    obj.getFecha_ingreso(),
                    obj.getFecha_vencimiento()
                });
            }

            tabla.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ingresarLote() {
        if (txtIdItem.getText().trim().isEmpty() || txtCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar ID item y cantidad.");
            return;
        }

        try {
            int idItem = Integer.parseInt(txtIdItem.getText().trim());
            BigDecimal cantidad = new BigDecimal(txtCantidad.getText().trim());

            Timestamp fechaVenc = null;
            if (!txtFechaVencimiento.getText().trim().isEmpty()) {
                LocalDateTime fecha = LocalDateTime.parse(txtFechaVencimiento.getText().trim());
                fechaVenc = Timestamp.valueOf(fecha);
            }

            dao.ingresarLote(idItem, cantidad, fechaVenc);
            JOptionPane.showMessageDialog(this, "Lote ingresado correctamente.");
            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID item y cantidad deben ser numéricos.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "La fecha debe tener formato: 2026-12-31T23:59:59");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al ingresar lote:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajustarLote() {
        if (txtIdLote.getText().trim().isEmpty() || txtNuevaCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un lote y escriba la nueva cantidad.");
            return;
        }

        try {
            int idLote = Integer.parseInt(txtIdLote.getText().trim());
            BigDecimal nuevaCantidad = new BigDecimal(txtNuevaCantidad.getText().trim());

            dao.ajustarInventario(idLote, nuevaCantidad);
            JOptionPane.showMessageDialog(this, "Inventario ajustado correctamente.");
            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La nueva cantidad debe ser numérica.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al ajustar inventario:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rebajarItem() {
        if (txtIdItem.getText().trim().isEmpty() || txtCantidadRebajar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese ID item y cantidad a rebajar.");
            return;
        }

        try {
            int idItem = Integer.parseInt(txtIdItem.getText().trim());
            BigDecimal cantidad = new BigDecimal(txtCantidadRebajar.getText().trim());

            dao.rebajarInventario(idItem, cantidad);
            JOptionPane.showMessageDialog(this, "Inventario rebajado correctamente.");
            cargar();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los datos numéricos no son válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al rebajar inventario:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarValorTotal() {
        try {
            BigDecimal total = dao.obtenerValorTotalInventario();
            JOptionPane.showMessageDialog(this,
                    "Valor total del inventario: " + total);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener valor total:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtIdLote.setText("");
        txtIdItem.setText("");
        txtCantidad.setText("");
        txtFechaVencimiento.setText("");
        txtNuevaCantidad.setText("");
        txtCantidadRebajar.setText("");
        tabla.clearSelection();
    }
}
