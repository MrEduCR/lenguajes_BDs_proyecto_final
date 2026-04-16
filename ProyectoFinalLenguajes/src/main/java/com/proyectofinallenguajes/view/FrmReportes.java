package com.proyectofinallenguajes.view;

import com.proyectofinallenguajes.dao.viewDAO;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class FrmReportes extends JFrame {

    private final viewDAO dao = new viewDAO();

    private JButton btnStockItems;
    private JButton btnOrdenesDetalle;
    private JButton btnDetalleOrdenes;
    private JButton btnMateriasPrimas;
    private JButton btnRecetaItems;
    private JButton btnUsuarios;
    private JButton btnProveedoresActivos;
    private JButton btnResumenFinanciero;
    private JButton btnAlertaInventario;
    private JButton btnRolesUsuarios;

    public FrmReportes() {
        setTitle("Menú de Reportes");
        setSize(760, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel lblTitulo = new JLabel("REPORTES");
        lblTitulo.setBounds(330, 20, 120, 30);

        btnStockItems = new JButton("Stock de Items");
        btnOrdenesDetalle = new JButton("Órdenes con detalle");
        btnDetalleOrdenes = new JButton("Detalle de órdenes");
        btnMateriasPrimas = new JButton("Materias primas");
        btnRecetaItems = new JButton("Receta de items");
        btnUsuarios = new JButton("Usuarios");
        btnProveedoresActivos = new JButton("Proveedores activos");
        btnResumenFinanciero = new JButton("Resumen financiero clientes");
        btnAlertaInventario = new JButton("Alerta inventario");
        btnRolesUsuarios = new JButton("Roles con usuarios");

        btnStockItems.setBounds(60, 80, 250, 40);
        btnOrdenesDetalle.setBounds(390, 80, 250, 40);

        btnDetalleOrdenes.setBounds(60, 140, 250, 40);
        btnMateriasPrimas.setBounds(390, 140, 250, 40);

        btnRecetaItems.setBounds(60, 200, 250, 40);
        btnUsuarios.setBounds(390, 200, 250, 40);

        btnProveedoresActivos.setBounds(60, 260, 250, 40);
        btnResumenFinanciero.setBounds(390, 260, 250, 40);

        btnAlertaInventario.setBounds(60, 320, 250, 40);
        btnRolesUsuarios.setBounds(390, 320, 250, 40);

        add(lblTitulo);

        add(btnStockItems);
        add(btnOrdenesDetalle);
        add(btnDetalleOrdenes);
        add(btnMateriasPrimas);
        add(btnRecetaItems);
        add(btnUsuarios);
        add(btnProveedoresActivos);
        add(btnResumenFinanciero);
        add(btnAlertaInventario);
        add(btnRolesUsuarios);

        btnStockItems.addActionListener(e -> abrir("Stock de Items", "sp_stock_items"));
        btnOrdenesDetalle.addActionListener(e -> abrir("Órdenes con detalle", "sp_ordenes_detalle"));
        btnDetalleOrdenes.addActionListener(e -> abrir("Detalle de órdenes", "sp_detalle_ordenes"));
        btnMateriasPrimas.addActionListener(e -> abrir("Materias primas", "sp_materias_primas"));
        btnRecetaItems.addActionListener(e -> abrir("Receta de items", "sp_receta_items"));
        btnUsuarios.addActionListener(e -> abrir("Usuarios", "sp_usuarios"));
        btnProveedoresActivos.addActionListener(e -> abrir("Proveedores activos", "sp_proveedores_activos"));
        btnResumenFinanciero.addActionListener(e -> abrir("Resumen financiero clientes", "sp_resumen_financiero_clientes"));
        btnAlertaInventario.addActionListener(e -> abrir("Alerta inventario", "sp_alerta_inventario"));
        btnRolesUsuarios.addActionListener(e -> abrir("Roles con usuarios", "sp_roles_usuarios"));
    }

    private void abrir(String titulo, String procedimiento) {
        try {
            new FrmVistaReporte(titulo, dao.ejecutarVista(procedimiento)).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar reporte:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}