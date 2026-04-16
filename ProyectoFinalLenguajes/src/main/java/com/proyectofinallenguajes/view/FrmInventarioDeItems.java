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
    private JButton btnActualizarFecha;

    private final inventarioDeItemsDAO dao = new inventarioDeItemsDAO();

    public FrmInventarioDeItems() {

        setTitle("Inventario de Items");
        setSize(1200, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 250, 1140, 250);

        JLabel lblIdLote = new JLabel("ID Lote:");
        JLabel lblIdItem = new JLabel("ID Item:");
        JLabel lblCantidad = new JLabel("Cantidad:");
        JLabel lblFechaVencimiento = new JLabel("Fecha Vencimiento:");
        JLabel lblNuevaCantidad = new JLabel("Nueva Cantidad:");
        JLabel lblCantidadRebajar = new JLabel("Cantidad Rebajar:");
        JLabel lblFormatoFecha = new JLabel("Formato fecha: yyyy-MM-ddTHH:mm:ss");

        lblIdLote.setBounds(20, 20, 100, 20);
        lblIdItem.setBounds(140, 20, 100, 20);
        lblCantidad.setBounds(260, 20, 100, 20);
        lblFechaVencimiento.setBounds(400, 20, 150, 20);
        lblNuevaCantidad.setBounds(640, 20, 150, 20);
        lblCantidadRebajar.setBounds(800, 20, 150, 20);
        lblFormatoFecha.setBounds(400, 75, 250, 20);

        
        txtIdLote = new JTextField();
        txtIdItem = new JTextField();
        txtCantidad = new JTextField();
        txtFechaVencimiento = new JTextField();
        txtNuevaCantidad = new JTextField();
        txtCantidadRebajar = new JTextField();

        txtIdLote.setBounds(20, 45, 100, 25);
        txtIdItem.setBounds(140, 45, 100, 25);
        txtCantidad.setBounds(260, 45, 120, 25);
        txtFechaVencimiento.setBounds(400, 45, 220, 25);
        txtNuevaCantidad.setBounds(640, 45, 120, 25);
        txtCantidadRebajar.setBounds(800, 45, 120, 25);

        txtIdLote.setEditable(false);

    
        btnCargar = new JButton("Cargar");
        btnIngresarLote = new JButton("Ingresar lote");
        btnAjustar = new JButton("Ajustar lote");
        btnRebajar = new JButton("Rebajar item");
        btnValorTotal = new JButton("Valor total");
        btnLimpiar = new JButton("Limpiar");
        btnActualizarFecha = new JButton("Actualizar fecha");

        btnCargar.setBounds(20, 120, 120, 30);
        btnIngresarLote.setBounds(160, 120, 140, 30);
        btnAjustar.setBounds(320, 120, 140, 30);
        btnRebajar.setBounds(480, 120, 140, 30);
        btnValorTotal.setBounds(640, 120, 140, 30);
        btnLimpiar.setBounds(800, 120, 120, 30);
        btnActualizarFecha.setBounds(940, 120, 180, 30);

     
        add(scroll);

        add(lblIdLote);
        add(lblIdItem);
        add(lblCantidad);
        add(lblFechaVencimiento);
        add(lblNuevaCantidad);
        add(lblCantidadRebajar);
        add(lblFormatoFecha);

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
        add(btnActualizarFecha);


        btnCargar.addActionListener(e -> cargar());
        btnIngresarLote.addActionListener(e -> ingresarLote());
        btnAjustar.addActionListener(e -> ajustarLote());
        btnRebajar.addActionListener(e -> rebajarItem());
        btnValorTotal.addActionListener(e -> mostrarValorTotal());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnActualizarFecha.addActionListener(e -> actualizarFecha());


        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();

                if (fila != -1) {
                    try {
                        int idLote = Integer.parseInt(tabla.getValueAt(fila, 0).toString());
                        inventarioDeItems obj = dao.obtenerLote(idLote);

                        if (obj != null) {
                            txtIdLote.setText(String.valueOf(obj.getId_lote()));
                            txtIdItem.setText(String.valueOf(obj.getId_item()));
                            txtCantidad.setText(obj.getCantidad().toString());
                            txtNuevaCantidad.setText(obj.getCantidad().toString());

                            if (obj.getFecha_vencimiento() != null) {
                                txtFechaVencimiento.setText(obj.getFecha_vencimiento().toString());
                            } else {
                                txtFechaVencimiento.setText("");
                            }
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Error:\n" + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        cargar();
    }

    private void cargar() {
        try {
            List<inventarioDeItems> lista = dao.listarInventario();

            String[] columnas = {
                    "ID Lote",
                    "ID Item",
                    "Producto",
                    "Cantidad",
                    "Fecha Ingreso",
                    "Fecha Vencimiento"
            };

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
            JOptionPane.showMessageDialog(this,
                    "ID Item y Cantidad deben ser numéricos.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato inválido de fecha.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al ingresar lote:\n" + e.getMessage());
        }
    }

    private void ajustarLote() {
        try {
            int idLote = Integer.parseInt(txtIdLote.getText().trim());

            BigDecimal nuevaCantidad;
            if (txtNuevaCantidad.getText().trim().isEmpty()) {
                nuevaCantidad = new BigDecimal(txtCantidad.getText().trim());
            } else {
                nuevaCantidad = new BigDecimal(txtNuevaCantidad.getText().trim());
            }

            Timestamp fechaVenc = null;
            if (!txtFechaVencimiento.getText().trim().isEmpty()) {
                LocalDateTime fecha = LocalDateTime.parse(txtFechaVencimiento.getText().trim());
                fechaVenc = Timestamp.valueOf(fecha);
            }

            dao.ajustarInventario(idLote, nuevaCantidad, fechaVenc);

            JOptionPane.showMessageDialog(this, "Inventario ajustado correctamente.");
            cargar();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al ajustar inventario:\n" + e.getMessage());
        }
    }

    private void actualizarFecha() {
        try {
            int idLote = Integer.parseInt(txtIdLote.getText().trim());

            Timestamp fechaVenc = null;
            if (!txtFechaVencimiento.getText().trim().isEmpty()) {
                LocalDateTime fecha = LocalDateTime.parse(txtFechaVencimiento.getText().trim());
                fechaVenc = Timestamp.valueOf(fecha);
            }

            dao.actualizarFechaVencimiento(idLote, fechaVenc);

            JOptionPane.showMessageDialog(this, "Fecha de vencimiento actualizada correctamente.");
            cargar();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar fecha:\n" + e.getMessage());
        }
    }

    private void rebajarItem() {
        try {
            int idItem = Integer.parseInt(txtIdItem.getText().trim());
            BigDecimal cantidad = new BigDecimal(txtCantidadRebajar.getText().trim());

            dao.rebajarInventario(idItem, cantidad);

            JOptionPane.showMessageDialog(this, "Inventario rebajado correctamente.");
            cargar();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al rebajar inventario:\n" + e.getMessage());
        }
    }

    private void mostrarValorTotal() {
        try {
            BigDecimal total = dao.obtenerValorTotalInventario();

            JOptionPane.showMessageDialog(this,
                    "Valor total del inventario: " + total);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener valor total:\n" + e.getMessage());
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