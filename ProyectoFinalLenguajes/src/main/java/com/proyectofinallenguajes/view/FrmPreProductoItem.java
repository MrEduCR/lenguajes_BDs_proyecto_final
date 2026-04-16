package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.preProductoItemDAO;
import com.proyectofinallenguajes.model.preProductoItem;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FrmPreProductoItem extends JFrame {

    private JTable tabla;

    private JTextField txtIdPreProducto;
    private JTextField txtIdItem;
    private JTextField txtIdMateriaPrima;
    private JTextField txtMedida;
    private JTextField txtUnidadMedida;
    private JTextField txtFiltroItem;

    private JButton btnCargar;
    private JButton btnBuscarPorItem;
    private JButton btnInsertar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final preProductoItemDAO dao = new preProductoItemDAO();

    public FrmPreProductoItem() {
        setTitle("Receta de Producto");
        setSize(1020, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 210, 960, 230);

        txtIdPreProducto = new JTextField();
        txtIdItem = new JTextField();
        txtIdMateriaPrima = new JTextField();
        txtMedida = new JTextField();
        txtUnidadMedida = new JTextField();
        txtFiltroItem = new JTextField();

        txtIdPreProducto.setBounds(20, 20, 100, 25);
        txtIdItem.setBounds(140, 20, 100, 25);
        txtIdMateriaPrima.setBounds(260, 20, 120, 25);
        txtMedida.setBounds(400, 20, 120, 25);
        txtUnidadMedida.setBounds(540, 20, 140, 25);
        txtFiltroItem.setBounds(20, 100, 120, 25);

        txtIdPreProducto.setEditable(false);

        btnCargar = new JButton("Cargar");
        btnBuscarPorItem = new JButton("Buscar por item");
        btnInsertar = new JButton("Insertar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnCargar.setBounds(160, 100, 120, 30);
        btnBuscarPorItem.setBounds(300, 100, 150, 30);
        btnInsertar.setBounds(470, 100, 120, 30);
        btnEliminar.setBounds(610, 100, 120, 30);
        btnLimpiar.setBounds(750, 100, 120, 30);

        add(scroll);
        add(txtIdPreProducto);
        add(txtIdItem);
        add(txtIdMateriaPrima);
        add(txtMedida);
        add(txtUnidadMedida);
        add(txtFiltroItem);

        add(btnCargar);
        add(btnBuscarPorItem);
        add(btnInsertar);
        add(btnEliminar);
        add(btnLimpiar);

        btnCargar.addActionListener(e -> limpiarTabla());
        btnBuscarPorItem.addActionListener(e -> buscarPorItem());
        btnInsertar.addActionListener(e -> insertar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtIdPreProducto.setText(tabla.getValueAt(fila, 0).toString());
                }
            }
        });
    }

    private void limpiarTabla() {
        String[] columnas = {"ID Receta", "Producto", "Materia Prima", "Medida", "Unidad", "Precio Referencia"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla.setModel(modelo);
    }

    private void buscarPorItem() {
        if (txtFiltroItem.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID de item para consultar la receta.");
            return;
        }

        try {
            int idItem = Integer.parseInt(txtFiltroItem.getText().trim());
            List<preProductoItem> lista = dao.listarReceta(idItem);

            String[] columnas = {"ID Receta", "Producto", "Materia Prima", "Medida", "Unidad", "Precio Referencia"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (preProductoItem obj : lista) {
                modelo.addRow(new Object[]{
                    obj.getId_pre_producto(),
                    obj.getProducto(),
                    obj.getNombreMateriaPrima(),
                    obj.getMedida_materia_prima(),
                    obj.getUnidad_medida(),
                    obj.getPrecioReferencia()
                });
            }

            tabla.setModel(modelo);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID item debe ser numérico.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al listar receta:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertar() {
        if (txtIdItem.getText().trim().isEmpty()
                || txtIdMateriaPrima.getText().trim().isEmpty()
                || txtMedida.getText().trim().isEmpty()
                || txtUnidadMedida.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete item, materia prima, medida y unidad.");
            return;
        }

        try {
            int idItem = Integer.parseInt(txtIdItem.getText().trim());
            int idMateriaPrima = Integer.parseInt(txtIdMateriaPrima.getText().trim());
            BigDecimal medida = new BigDecimal(txtMedida.getText().trim());

            dao.insertarReceta(idItem, idMateriaPrima, medida, txtUnidadMedida.getText().trim());

            JOptionPane.showMessageDialog(this, "Línea de receta insertada correctamente.");
            txtFiltroItem.setText(String.valueOf(idItem));
            buscarPorItem();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Item, materia prima y medida deben ser numéricos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al insertar receta:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (txtIdPreProducto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una línea de receta.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar esta línea de receta?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int idPreProducto = Integer.parseInt(txtIdPreProducto.getText().trim());
            dao.eliminarReceta(idPreProducto);

            JOptionPane.showMessageDialog(this, "Línea de receta eliminada correctamente.");

            if (!txtFiltroItem.getText().trim().isEmpty()) {
                buscarPorItem();
            } else {
                limpiarTabla();
            }

            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar receta:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtIdPreProducto.setText("");
        txtIdItem.setText("");
        txtIdMateriaPrima.setText("");
        txtMedida.setText("");
        txtUnidadMedida.setText("");
        tabla.clearSelection();
    }
}
