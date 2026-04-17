-- ============================================================
-- AVANCE 2 - PL/SQL (50% del proyecto)
-- Curso: SC-504 Lenguajes de Base de Datos
-- Proyecto: Suproli, Grupo #8
-- ============================================================


-- ============================================================
-- SECCIÓN 1: VISTAS (5)
-- ============================================================

-- Vista 1: Productos con su stock total disponible en inventario
CREATE OR REPLACE VIEW vw_stock_items AS
    SELECT
        i.id_item,
        i.nombre AS producto,
        i.unidad_medida,
        i.precio_unitario,
        NVL(SUM(inv.cantidad), 0) AS stock_total,
        e.nombre         AS estado
    FROM item i
    LEFT JOIN inventario_de_items inv ON i.id_item = inv.id_item
    LEFT JOIN estado e ON i.id_estado = e.id_estado
    GROUP BY i.id_item, i.nombre, i.unidad_medida, i.precio_unitario, e.nombre;

-- Vista 2: Órdenes con datos del cliente y estado
CREATE OR REPLACE VIEW vw_ordenes_detalle AS
    SELECT
        o.id_orden,
        o.fecha,
        c.nombre AS cliente,
        c.telefono,
        c.correo AS correo_cliente,
        e.nombre AS estado_orden,
        u.nombre AS atendido_por
    FROM orden o
    JOIN cliente  c ON o.id_cliente  = c.id_cliente
    JOIN estado   e ON o.id_estado   = e.id_estado
    JOIN usuario  u ON o.id_usuario  = u.id_usuario;

-- Vista 3: Detalle completo de órdenes (líneas de pedido)
CREATE OR REPLACE VIEW vw_detalle_ordenes AS
    SELECT
        d.id_detalle,
        d.id_orden,
        i.nombre AS producto,
        d.cantidad,
        i.precio_unitario,
        (d.cantidad * i.precio_unitario) AS subtotal
    FROM detalle_orden d
    JOIN item i ON d.id_item = i.id_item;

-- Vista 4: Materias primas con proveedor
CREATE OR REPLACE VIEW vw_materias_primas AS
    SELECT
        mp.id_materia_prima,
        mp.nombre_materia_prima,
        mp.precio_referencia,
        p.nombre         AS proveedor,
        p.telefono       AS telefono_proveedor,
        p.correo         AS correo_proveedor
    FROM materia_prima mp
    JOIN proveedor p ON mp.id_proveedor = p.id_proveedor;

-- Vista 5: Materias primas del producto
CREATE OR REPLACE VIEW vw_receta_items AS
    SELECT
        i.id_item,
        i.nombre         AS producto,
        mp.nombre_materia_prima,
        pp.medida_materia_prima,
        pp.unidad_medida
    FROM pre_producto_item pp
    JOIN item         i  ON pp.id_item         = i.id_item
    JOIN materia_prima mp ON pp.id_materia_prima = mp.id_materia_prima;


-- Vista 6: Usuarios con su rol y estado
CREATE OR REPLACE VIEW vw_usuarios AS
    SELECT
        u.id_usuario,
        u.nombre,
        u.correo,
        r.nombre    AS rol,
        e.nombre    AS estado
    FROM usuario u
    JOIN rol    r ON u.id_rol    = r.id_rol
    JOIN estado e ON u.id_estado = e.id_estado;

-- Vista 7: Proveedores activos con cantidad de materias primas que suministran
CREATE OR REPLACE VIEW vw_proveedores_activos AS
    SELECT
        p.id_proveedor,
        p.nombre,
        p.contacto,
        p.telefono,
        p.correo,
        COUNT(mp.id_materia_prima) AS total_materias
    FROM proveedor p
    LEFT JOIN materia_prima mp ON p.id_proveedor = mp.id_proveedor
    JOIN estado e              ON p.id_estado    = e.id_estado
    WHERE UPPER(e.nombre) = 'ACTIVO'
    GROUP BY p.id_proveedor, p.nombre, p.contacto, p.telefono, p.correo;

-- Vista 8: Resumen financiero de órdenes por cliente
CREATE OR REPLACE VIEW vw_resumen_financiero_clientes AS
    SELECT
        c.id_cliente,
        c.nombre            AS cliente,
        COUNT(o.id_orden)   AS total_ordenes,
        NVL(SUM(d.cantidad * i.precio_unitario), 0) AS total_facturado
    FROM cliente c
    LEFT JOIN orden        o ON c.id_cliente = o.id_cliente
    LEFT JOIN detalle_orden d ON o.id_orden  = d.id_orden
    LEFT JOIN item          i ON d.id_item   = i.id_item
    GROUP BY c.id_cliente, c.nombre;

-- Vista 9: Inventario con estado de alerta de stock
CREATE OR REPLACE VIEW vw_alerta_inventario AS
    SELECT
        i.id_item,
        i.nombre        AS producto,
        NVL(SUM(inv.cantidad), 0) AS stock_actual,
        CASE
            WHEN NVL(SUM(inv.cantidad), 0) = 0    THEN 'SIN STOCK'
            WHEN NVL(SUM(inv.cantidad), 0) < 10   THEN 'STOCK BAJO'
            WHEN NVL(SUM(inv.cantidad), 0) < 50   THEN 'STOCK MEDIO'
            ELSE 'STOCK OK'
        END AS alerta
    FROM item i
    LEFT JOIN inventario_de_items inv ON i.id_item = inv.id_item
    GROUP BY i.id_item, i.nombre;

-- Vista 10: Roles con cantidad de usuarios asignados
CREATE OR REPLACE VIEW vw_roles_usuarios AS
    SELECT
        r.id_rol,
        r.nombre        AS rol,
        r.descripcion,
        e.nombre        AS estado,
        COUNT(u.id_usuario) AS total_usuarios
    FROM rol r
    LEFT JOIN usuario u ON r.id_rol     = u.id_rol
    JOIN  estado      e ON r.id_estado  = e.id_estado
    GROUP BY r.id_rol, r.nombre, r.descripcion, e.nombre;
 
-- ============================================================
-- SECCIÓN 1: pkg_fabrica (principal)
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_fabrica AS

    FUNCTION fn_stock_item(p_id_item IN INTEGER) RETURN NUMBER;
    FUNCTION fn_total_orden(p_id_orden IN INTEGER) RETURN NUMBER;
    FUNCTION fn_nombre_estado(p_id_estado IN INTEGER) RETURN VARCHAR2;
    FUNCTION fn_ordenes_cliente(p_id_cliente IN INTEGER) RETURN NUMBER;
    FUNCTION fn_hay_stock(p_id_item IN INTEGER, p_cantidad IN NUMBER) RETURN BOOLEAN;
    FUNCTION fn_costo_fabricacion(p_id_item IN INTEGER) RETURN NUMBER;
    FUNCTION fn_precio_item(p_id_item IN INTEGER) RETURN NUMBER;
    FUNCTION fn_total_proveedores_activos RETURN NUMBER;

    PROCEDURE sp_insertar_estado(p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2);
    PROCEDURE sp_actualizar_estado(p_id_estado IN INTEGER, p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2);
    PROCEDURE sp_eliminar_estado(p_id_estado IN INTEGER);
    PROCEDURE sp_obtener_estado(p_id_estado IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_estados(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_detalles(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_detalles_por_orden(p_id_orden IN INTEGER, p_cursor OUT SYS_REFCURSOR);


    PROCEDURE sp_insertar_cliente(p_nombre IN VARCHAR2, p_identificacion IN VARCHAR2,
                                   p_telefono IN VARCHAR2, p_correo IN VARCHAR2,
                                   p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);
    PROCEDURE sp_actualizar_cliente(p_id_cliente IN INTEGER, p_nombre IN VARCHAR2,
                                     p_telefono IN VARCHAR2, p_correo IN VARCHAR2);
    PROCEDURE sp_eliminar_cliente(p_id_cliente IN INTEGER);
    PROCEDURE sp_obtener_cliente(p_id_cliente IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_clientes(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_insertar_item(p_nombre IN VARCHAR2, p_descripcion IN CLOB,
                                p_unidad IN VARCHAR2, p_precio IN NUMBER,
                                p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);
    PROCEDURE sp_actualizar_item(p_id_item IN INTEGER, p_nombre IN VARCHAR2, p_precio IN NUMBER);
    PROCEDURE sp_eliminar_item(p_id_item IN INTEGER);
    PROCEDURE sp_obtener_item(p_id_item IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_items(p_cursor OUT SYS_REFCURSOR);


    PROCEDURE sp_insertar_proveedor(p_nombre IN VARCHAR2, p_contacto IN VARCHAR2,
                                     p_telefono IN VARCHAR2, p_correo IN VARCHAR2,
                                     p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);
    PROCEDURE sp_obtener_proveedor(p_id_proveedor IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_proveedores(p_cursor OUT SYS_REFCURSOR);

    PROCEDURE sp_crear_orden(p_id_cliente IN INTEGER, p_id_usuario IN INTEGER,
                              p_id_estado IN INTEGER, p_id_nueva OUT INTEGER);
    PROCEDURE sp_agregar_detalle_orden(p_id_orden IN INTEGER, p_id_item IN INTEGER,
                                        p_cantidad IN NUMBER);
    PROCEDURE sp_actualizar_detalle_orden(p_id_detalle IN INTEGER, p_cantidad IN NUMBER);
    PROCEDURE sp_eliminar_detalle_orden(p_id_detalle IN INTEGER);
    PROCEDURE sp_cancelar_orden(p_id_orden IN INTEGER);
    PROCEDURE sp_obtener_orden(p_id_orden IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_ordenes(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_ingresar_lote(p_id_item IN INTEGER, p_cantidad IN NUMBER,
                                p_fecha_venc IN TIMESTAMP);
    PROCEDURE sp_obtener_lote(p_id_lote IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_inventario(p_cursor OUT SYS_REFCURSOR);


    PROCEDURE sp_obtener_materia_prima(p_id_materia_prima IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_materias_primas(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_eliminar_materia_prima(p_id_materia_prima IN INTEGER);

    PROCEDURE sp_eliminar_receta(p_id_pre_producto IN INTEGER);
    PROCEDURE sp_listar_receta(p_id_item IN INTEGER, p_cursor OUT SYS_REFCURSOR);

    PROCEDURE sp_reporte_ordenes_cliente(p_id_cliente IN INTEGER, p_cursor OUT SYS_REFCURSOR);

END pkg_fabrica;
/


CREATE OR REPLACE PACKAGE BODY pkg_fabrica AS

    -- ==================== FUNCIONES ====================

    FUNCTION fn_stock_item(p_id_item IN INTEGER) RETURN NUMBER IS
        v_stock NUMBER;
    BEGIN
        SELECT NVL(SUM(cantidad), 0) INTO v_stock
        FROM inventario_de_items WHERE id_item = p_id_item;
        RETURN v_stock;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20001, 'Error calculando stock del item: ' || p_id_item);
    END fn_stock_item;

    FUNCTION fn_total_orden(p_id_orden IN INTEGER) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT NVL(SUM(d.cantidad * i.precio_unitario), 0) INTO v_total
        FROM detalle_orden d JOIN item i ON d.id_item = i.id_item
        WHERE d.id_orden = p_id_orden;
        RETURN v_total;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RETURN 0;
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20002, 'Error calculando total de orden: ' || p_id_orden);
    END fn_total_orden;

    FUNCTION fn_nombre_estado(p_id_estado IN INTEGER) RETURN VARCHAR2 IS
        v_nombre estado.nombre%TYPE;
    BEGIN
        SELECT nombre INTO v_nombre FROM estado WHERE id_estado = p_id_estado;
        RETURN v_nombre;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RETURN 'DESCONOCIDO';
    END fn_nombre_estado;

    FUNCTION fn_ordenes_cliente(p_id_cliente IN INTEGER) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total FROM orden WHERE id_cliente = p_id_cliente;
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20003, 'Error contando órdenes del cliente: ' || p_id_cliente);
    END fn_ordenes_cliente;

    FUNCTION fn_hay_stock(p_id_item IN INTEGER, p_cantidad IN NUMBER) RETURN BOOLEAN IS
    BEGIN
        RETURN fn_stock_item(p_id_item) >= p_cantidad;
    END fn_hay_stock;

    FUNCTION fn_costo_fabricacion(p_id_item IN INTEGER) RETURN NUMBER IS
        v_costo  NUMBER := 0;
        v_parcial NUMBER;
        CURSOR c_receta(p_item INTEGER) IS
            SELECT pp.medida_materia_prima, mp.precio_referencia
            FROM pre_producto_item pp
            JOIN materia_prima mp ON pp.id_materia_prima = mp.id_materia_prima
            WHERE pp.id_item = p_item;
        v_fila c_receta%ROWTYPE;
    BEGIN
        OPEN c_receta(p_id_item);
        LOOP
            FETCH c_receta INTO v_fila;
            EXIT WHEN c_receta%NOTFOUND;
            v_costo := v_costo + (v_fila.medida_materia_prima * v_fila.precio_referencia);
        END LOOP;
        CLOSE c_receta;
        RETURN v_costo;
    EXCEPTION
        WHEN OTHERS THEN
            IF c_receta%ISOPEN THEN CLOSE c_receta; END IF;
            RAISE_APPLICATION_ERROR(-20004, 'Error calculando costo de fabricación: ' || p_id_item);
    END fn_costo_fabricacion;

    FUNCTION fn_precio_item(p_id_item IN INTEGER) RETURN NUMBER IS
        v_precio item.precio_unitario%TYPE;
    BEGIN
        SELECT precio_unitario INTO v_precio FROM item WHERE id_item = p_id_item;
        RETURN v_precio;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20005, 'Item no encontrado: ' || p_id_item);
    END fn_precio_item;

    FUNCTION fn_total_proveedores_activos RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total
        FROM proveedor p JOIN estado e ON p.id_estado = e.id_estado
        WHERE UPPER(e.nombre) = 'ACTIVO';
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20006, 'Error contando proveedores activos.');
    END fn_total_proveedores_activos;


    -- ==================== ESTADO ====================

    PROCEDURE sp_insertar_estado(p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2) IS
        v_existe NUMBER;
        ex_ya_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM estado WHERE UPPER(nombre) = UPPER(p_nombre);
        IF v_existe > 0 THEN RAISE ex_ya_existe; END IF;
        INSERT INTO estado(nombre, descripcion) VALUES (p_nombre, p_descripcion);
        COMMIT;
    EXCEPTION
        WHEN ex_ya_existe THEN
            RAISE_APPLICATION_ERROR(-20010, 'El estado ya existe: ' || p_nombre);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20011, 'Error insertando estado: ' || SQLERRM);
    END sp_insertar_estado;

    PROCEDURE sp_actualizar_estado(p_id_estado IN INTEGER, p_nombre IN VARCHAR2,
                                    p_descripcion IN VARCHAR2) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM estado WHERE id_estado = p_id_estado;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        UPDATE estado SET nombre = p_nombre, descripcion = p_descripcion
        WHERE id_estado = p_id_estado;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20012, 'Estado no encontrado: ' || p_id_estado);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20013, 'Error actualizando estado: ' || SQLERRM);
    END sp_actualizar_estado;

    PROCEDURE sp_eliminar_estado(p_id_estado IN INTEGER) IS
        v_en_uso     NUMBER;
        ex_en_uso    EXCEPTION;
        ex_no_existe EXCEPTION;
        v_existe     NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM estado WHERE id_estado = p_id_estado;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;

        -- Verificar que no esté en uso en otras tablas
        SELECT COUNT(*) INTO v_en_uso FROM (
            SELECT id_estado FROM rol      WHERE id_estado = p_id_estado UNION ALL
            SELECT id_estado FROM usuario  WHERE id_estado = p_id_estado UNION ALL
            SELECT id_estado FROM cliente  WHERE id_estado = p_id_estado UNION ALL
            SELECT id_estado FROM proveedor WHERE id_estado = p_id_estado UNION ALL
            SELECT id_estado FROM item     WHERE id_estado = p_id_estado UNION ALL
            SELECT id_estado FROM orden    WHERE id_estado = p_id_estado
        );
        IF v_en_uso > 0 THEN RAISE ex_en_uso; END IF;

        DELETE FROM estado WHERE id_estado = p_id_estado;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20014, 'Estado no encontrado: ' || p_id_estado);
        WHEN ex_en_uso THEN
            RAISE_APPLICATION_ERROR(-20015, 'No se puede eliminar: el estado está en uso.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20016, 'Error eliminando estado: ' || SQLERRM);
    END sp_eliminar_estado;

    PROCEDURE sp_obtener_estado(p_id_estado IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_estado, nombre, descripcion
            FROM estado
            WHERE id_estado = p_id_estado;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20017, 'Error obteniendo estado: ' || SQLERRM);
    END sp_obtener_estado;

    PROCEDURE sp_listar_estados(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_estado, nombre, descripcion
            FROM estado
            ORDER BY nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20018, 'Error listando estados: ' || SQLERRM);
    END sp_listar_estados;


    -- ==================== CLIENTE ====================

    PROCEDURE sp_insertar_cliente(p_nombre IN VARCHAR2, p_identificacion IN VARCHAR2,
                                   p_telefono IN VARCHAR2, p_correo IN VARCHAR2,
                                   p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
        v_existe NUMBER;
        ex_duplicado EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM cliente WHERE identificacion = p_identificacion;
        IF v_existe > 0 THEN RAISE ex_duplicado; END IF;
        INSERT INTO cliente(nombre, identificacion, telefono, correo, id_estado)
        VALUES (p_nombre, p_identificacion, p_telefono, p_correo, p_id_estado)
        RETURNING id_cliente INTO p_id_nuevo;
        COMMIT;
    EXCEPTION
        WHEN ex_duplicado THEN
            RAISE_APPLICATION_ERROR(-20020, 'Cliente ya registrado con identificación: ' || p_identificacion);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20021, 'Error insertando cliente: ' || SQLERRM);
    END sp_insertar_cliente;

    PROCEDURE sp_actualizar_cliente(p_id_cliente IN INTEGER, p_nombre IN VARCHAR2,
                                     p_telefono IN VARCHAR2, p_correo IN VARCHAR2) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM cliente WHERE id_cliente = p_id_cliente;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        UPDATE cliente SET nombre = p_nombre, telefono = p_telefono, correo = p_correo
        WHERE id_cliente = p_id_cliente;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20022, 'Cliente no encontrado: ' || p_id_cliente);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20023, 'Error actualizando cliente: ' || SQLERRM);
    END sp_actualizar_cliente;

    PROCEDURE sp_eliminar_cliente(p_id_cliente IN INTEGER) IS
        v_id_inactivo estado.id_estado%TYPE;
        v_existe      NUMBER;
        ex_no_existe  EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM cliente WHERE id_cliente = p_id_cliente;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        SELECT id_estado INTO v_id_inactivo FROM estado
        WHERE UPPER(nombre) = 'INACTIVO' AND ROWNUM = 1;
        UPDATE cliente SET id_estado = v_id_inactivo WHERE id_cliente = p_id_cliente;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20024, 'Cliente no encontrado: ' || p_id_cliente);
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20025, 'Estado INACTIVO no existe.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20026, 'Error eliminando cliente: ' || SQLERRM);
    END sp_eliminar_cliente;

    PROCEDURE sp_obtener_cliente(p_id_cliente IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT c.id_cliente, c.nombre, c.identificacion,
                   c.telefono, c.correo,
                   e.nombre AS estado, c.id_estado
            FROM cliente c
            JOIN estado e ON c.id_estado = e.id_estado
            WHERE c.id_cliente = p_id_cliente;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20027, 'Error obteniendo cliente: ' || SQLERRM);
    END sp_obtener_cliente;

    PROCEDURE sp_listar_clientes(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT c.id_cliente, c.nombre, c.identificacion,
                   c.telefono, c.correo,
                   e.nombre AS estado
            FROM cliente c
            JOIN estado e ON c.id_estado = e.id_estado
            ORDER BY c.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20028, 'Error listando clientes: ' || SQLERRM);
    END sp_listar_clientes;


    -- ==================== ITEM ====================

    PROCEDURE sp_insertar_item(p_nombre IN VARCHAR2, p_descripcion IN CLOB,
                                p_unidad IN VARCHAR2, p_precio IN NUMBER,
                                p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
    BEGIN
        IF p_precio <= 0 THEN
            RAISE_APPLICATION_ERROR(-20030, 'El precio debe ser mayor a cero.');
        END IF;
        INSERT INTO item(nombre, descripcion, unidad_medida, precio_unitario, id_estado)
        VALUES (p_nombre, p_descripcion, p_unidad, p_precio, p_id_estado)
        RETURNING id_item INTO p_id_nuevo;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20031, 'Error insertando item: ' || SQLERRM);
    END sp_insertar_item;

    PROCEDURE sp_actualizar_item(p_id_item IN INTEGER, p_nombre IN VARCHAR2,
                                  p_precio IN NUMBER) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM item WHERE id_item = p_id_item;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        UPDATE item SET nombre = p_nombre, precio_unitario = p_precio
        WHERE id_item = p_id_item;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20032, 'Item no encontrado: ' || p_id_item);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20033, 'Error actualizando item: ' || SQLERRM);
    END sp_actualizar_item;

    PROCEDURE sp_eliminar_item(p_id_item IN INTEGER) IS
        v_id_inactivo estado.id_estado%TYPE;
    BEGIN
        SELECT id_estado INTO v_id_inactivo FROM estado
        WHERE UPPER(nombre) = 'INACTIVO' AND ROWNUM = 1;
        UPDATE item SET id_estado = v_id_inactivo WHERE id_item = p_id_item;
        COMMIT;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20034, 'Item o estado INACTIVO no encontrado.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20035, 'Error eliminando item: ' || SQLERRM);
    END sp_eliminar_item;

    PROCEDURE sp_obtener_item(p_id_item IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT i.id_item, i.nombre, i.descripcion,
                   i.unidad_medida, i.precio_unitario,
                   e.nombre AS estado, i.id_estado
            FROM item i
            JOIN estado e ON i.id_estado = e.id_estado
            WHERE i.id_item = p_id_item;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20036, 'Error obteniendo item: ' || SQLERRM);
    END sp_obtener_item;

    PROCEDURE sp_listar_items(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT i.id_item, i.nombre, i.descripcion,
                   i.unidad_medida, i.precio_unitario,
                   e.nombre AS estado
            FROM item i
            JOIN estado e ON i.id_estado = e.id_estado
            ORDER BY i.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20037, 'Error listando items: ' || SQLERRM);
    END sp_listar_items;


    -- ==================== PROVEEDOR ====================

    PROCEDURE sp_insertar_proveedor(p_nombre IN VARCHAR2, p_contacto IN VARCHAR2,
                                     p_telefono IN VARCHAR2, p_correo IN VARCHAR2,
                                     p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
    BEGIN
        INSERT INTO proveedor(nombre, contacto, telefono, correo, id_estado)
        VALUES (p_nombre, p_contacto, p_telefono, p_correo, p_id_estado)
        RETURNING id_proveedor INTO p_id_nuevo;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20040, 'Error insertando proveedor: ' || SQLERRM);
    END sp_insertar_proveedor;

    PROCEDURE sp_obtener_proveedor(p_id_proveedor IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT p.id_proveedor, p.nombre, p.contacto,
                   p.telefono, p.correo,
                   e.nombre AS estado, p.id_estado
            FROM proveedor p
            JOIN estado e ON p.id_estado = e.id_estado
            WHERE p.id_proveedor = p_id_proveedor;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20041, 'Error obteniendo proveedor: ' || SQLERRM);
    END sp_obtener_proveedor;

    PROCEDURE sp_listar_proveedores(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT p.id_proveedor, p.nombre, p.contacto,
                   p.telefono, p.correo,
                   e.nombre AS estado
            FROM proveedor p
            JOIN estado e ON p.id_estado = e.id_estado
            ORDER BY p.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20042, 'Error listando proveedores: ' || SQLERRM);
    END sp_listar_proveedores;


    -- ==================== ORDEN ====================

    PROCEDURE sp_crear_orden(p_id_cliente IN INTEGER, p_id_usuario IN INTEGER,
                              p_id_estado IN INTEGER, p_id_nueva OUT INTEGER) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM cliente WHERE id_cliente = p_id_cliente;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        INSERT INTO orden(fecha, id_cliente, id_usuario, id_estado)
        VALUES (SYSTIMESTAMP, p_id_cliente, p_id_usuario, p_id_estado)
        RETURNING id_orden INTO p_id_nueva;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20050, 'Cliente no encontrado: ' || p_id_cliente);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20051, 'Error creando orden: ' || SQLERRM);
    END sp_crear_orden;

    PROCEDURE sp_agregar_detalle_orden(p_id_orden IN INTEGER, p_id_item IN INTEGER,
                                        p_cantidad IN NUMBER) IS
        ex_sin_stock EXCEPTION;
    BEGIN
        IF NOT fn_hay_stock(p_id_item, p_cantidad) THEN RAISE ex_sin_stock; END IF;
        INSERT INTO detalle_orden(id_orden, id_item, cantidad)
        VALUES (p_id_orden, p_id_item, p_cantidad);
        COMMIT;
    EXCEPTION
        WHEN ex_sin_stock THEN
            RAISE_APPLICATION_ERROR(-20052, 'Stock insuficiente para el item: ' || p_id_item);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20053, 'Error agregando detalle: ' || SQLERRM);
    END sp_agregar_detalle_orden;

    PROCEDURE sp_actualizar_detalle_orden(p_id_detalle IN INTEGER, p_cantidad IN NUMBER) IS
        v_existe     NUMBER;
        v_id_item    detalle_orden.id_item%TYPE;
        ex_no_existe EXCEPTION;
        ex_sin_stock EXCEPTION;
    BEGIN
        SELECT COUNT(*), MAX(id_item) INTO v_existe, v_id_item
        FROM detalle_orden WHERE id_detalle = p_id_detalle;

        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        IF NOT fn_hay_stock(v_id_item, p_cantidad) THEN RAISE ex_sin_stock; END IF;

        UPDATE detalle_orden SET cantidad = p_cantidad WHERE id_detalle = p_id_detalle;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20054, 'Detalle no encontrado: ' || p_id_detalle);
        WHEN ex_sin_stock THEN
            RAISE_APPLICATION_ERROR(-20055, 'Stock insuficiente para actualizar el detalle.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20056, 'Error actualizando detalle: ' || SQLERRM);
    END sp_actualizar_detalle_orden;

    PROCEDURE sp_eliminar_detalle_orden(p_id_detalle IN INTEGER) IS
        v_existe     NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM detalle_orden WHERE id_detalle = p_id_detalle;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        DELETE FROM detalle_orden WHERE id_detalle = p_id_detalle;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20057, 'Detalle no encontrado: ' || p_id_detalle);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20058, 'Error eliminando detalle: ' || SQLERRM);
    END sp_eliminar_detalle_orden;

        PROCEDURE sp_listar_detalles(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT d.id_detalle, d.id_orden, i.nombre AS producto,
                d.cantidad, i.precio_unitario,
                (d.cantidad * i.precio_unitario) AS subtotal
            FROM detalle_orden d
            JOIN item i ON d.id_item = i.id_item
            ORDER BY d.id_orden, d.id_detalle;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20065, 'Error listando detalles: ' || SQLERRM);
    END sp_listar_detalles;

    PROCEDURE sp_listar_detalles_por_orden(p_id_orden IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT d.id_detalle, d.id_orden, i.nombre AS producto,
                d.cantidad, i.precio_unitario,
                (d.cantidad * i.precio_unitario) AS subtotal
            FROM detalle_orden d
            JOIN item i ON d.id_item = i.id_item
            WHERE d.id_orden = p_id_orden
            ORDER BY d.id_detalle;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20066, 'Error listando detalles por orden: ' || SQLERRM);
    END sp_listar_detalles_por_orden;

    PROCEDURE sp_cancelar_orden(p_id_orden IN INTEGER) IS
        v_id_cancelado estado.id_estado%TYPE;
        v_existe       NUMBER;
        ex_no_existe   EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM orden WHERE id_orden = p_id_orden;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        SELECT id_estado INTO v_id_cancelado FROM estado
        WHERE UPPER(nombre) = 'CANCELADO' AND ROWNUM = 1;
        UPDATE orden SET id_estado = v_id_cancelado WHERE id_orden = p_id_orden;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20059, 'Orden no encontrada: ' || p_id_orden);
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20060, 'Estado CANCELADO no existe.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20061, 'Error cancelando orden: ' || SQLERRM);
    END sp_cancelar_orden;

    PROCEDURE sp_obtener_orden(p_id_orden IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT o.id_orden, o.fecha,
                   c.nombre  AS cliente,  o.id_cliente,
                   u.nombre  AS usuario,  o.id_usuario,
                   e.nombre  AS estado,   o.id_estado,
                   fn_total_orden(o.id_orden) AS total
            FROM orden o
            JOIN cliente c ON o.id_cliente = c.id_cliente
            JOIN usuario u ON o.id_usuario = u.id_usuario
            JOIN estado  e ON o.id_estado  = e.id_estado
            WHERE o.id_orden = p_id_orden;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20062, 'Error obteniendo orden: ' || SQLERRM);
    END sp_obtener_orden;

    PROCEDURE sp_listar_ordenes(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT o.id_orden, o.fecha,
                   c.nombre AS cliente,
                   u.nombre AS usuario,
                   e.nombre AS estado,
                   fn_total_orden(o.id_orden) AS total
            FROM orden o
            JOIN cliente c ON o.id_cliente = c.id_cliente
            JOIN usuario u ON o.id_usuario = u.id_usuario
            JOIN estado  e ON o.id_estado  = e.id_estado
            ORDER BY o.fecha DESC;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20063, 'Error listando ordenes: ' || SQLERRM);
    END sp_listar_ordenes;


    -- ==================== INVENTARIO ====================

    PROCEDURE sp_ingresar_lote(p_id_item IN INTEGER, p_cantidad IN NUMBER,
                                p_fecha_venc IN TIMESTAMP) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM item WHERE id_item = p_id_item;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        IF p_cantidad <= 0 THEN
            RAISE_APPLICATION_ERROR(-20070, 'La cantidad debe ser mayor a cero.');
        END IF;
        INSERT INTO inventario_de_items(id_item, cantidad, fecha_ingreso, fecha_vencimiento)
        VALUES (p_id_item, p_cantidad, SYSTIMESTAMP, p_fecha_venc);
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20071, 'Item no encontrado: ' || p_id_item);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20072, 'Error ingresando lote: ' || SQLERRM);
    END sp_ingresar_lote;

    PROCEDURE sp_obtener_lote(p_id_lote IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT inv.id_lote, i.nombre AS producto, inv.id_item,
                   inv.cantidad, inv.fecha_ingreso, inv.fecha_vencimiento
            FROM inventario_de_items inv
            JOIN item i ON inv.id_item = i.id_item
            WHERE inv.id_lote = p_id_lote;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20073, 'Error obteniendo lote: ' || SQLERRM);
    END sp_obtener_lote;

    PROCEDURE sp_listar_inventario(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT inv.id_lote, i.nombre AS producto, inv.id_item,
                   inv.cantidad, inv.fecha_ingreso, inv.fecha_vencimiento
            FROM inventario_de_items inv
            JOIN item i ON inv.id_item = i.id_item
            ORDER BY i.nombre, inv.fecha_ingreso;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20074, 'Error listando inventario: ' || SQLERRM);
    END sp_listar_inventario;


    -- ==================== MATERIA PRIMA ====================

    PROCEDURE sp_obtener_materia_prima(p_id_materia_prima IN INTEGER,
                                        p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT mp.id_materia_prima, mp.nombre_materia_prima,
                   mp.precio_referencia,
                   p.nombre AS proveedor, mp.id_proveedor
            FROM materia_prima mp
            JOIN proveedor p ON mp.id_proveedor = p.id_proveedor
            WHERE mp.id_materia_prima = p_id_materia_prima;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20080, 'Error obteniendo materia prima: ' || SQLERRM);
    END sp_obtener_materia_prima;

    PROCEDURE sp_listar_materias_primas(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT mp.id_materia_prima, mp.nombre_materia_prima,
                   mp.precio_referencia,
                   p.nombre AS proveedor
            FROM materia_prima mp
            JOIN proveedor p ON mp.id_proveedor = p.id_proveedor
            ORDER BY mp.nombre_materia_prima;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20081, 'Error listando materias primas: ' || SQLERRM);
    END sp_listar_materias_primas;

    PROCEDURE sp_eliminar_materia_prima(p_id_materia_prima IN INTEGER) IS
        v_en_uso     NUMBER;
        v_existe     NUMBER;
        ex_no_existe EXCEPTION;
        ex_en_uso    EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe
        FROM materia_prima WHERE id_materia_prima = p_id_materia_prima;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;

        SELECT COUNT(*) INTO v_en_uso
        FROM pre_producto_item WHERE id_materia_prima = p_id_materia_prima;
        IF v_en_uso > 0 THEN RAISE ex_en_uso; END IF;

        DELETE FROM materia_prima WHERE id_materia_prima = p_id_materia_prima;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20082, 'Materia prima no encontrada: ' || p_id_materia_prima);
        WHEN ex_en_uso THEN
            RAISE_APPLICATION_ERROR(-20083, 'No se puede eliminar: materia prima está en uso en recetas.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20084, 'Error eliminando materia prima: ' || SQLERRM);
    END sp_eliminar_materia_prima;


    -- ==================== RECETA (PRE_PRODUCTO_ITEM) ====================

    PROCEDURE sp_eliminar_receta(p_id_pre_producto IN INTEGER) IS
        v_existe     NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe
        FROM pre_producto_item WHERE id_pre_producto = p_id_pre_producto;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        DELETE FROM pre_producto_item WHERE id_pre_producto = p_id_pre_producto;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20090, 'Línea de receta no encontrada: ' || p_id_pre_producto);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20091, 'Error eliminando receta: ' || SQLERRM);
    END sp_eliminar_receta;

    PROCEDURE sp_listar_receta(p_id_item IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT pp.id_pre_producto, i.nombre AS producto,
                   mp.nombre_materia_prima, mp.id_materia_prima,
                   pp.medida_materia_prima, pp.unidad_medida,
                   mp.precio_referencia
            FROM pre_producto_item pp
            JOIN item          i  ON pp.id_item         = i.id_item
            JOIN materia_prima mp ON pp.id_materia_prima = mp.id_materia_prima
            WHERE pp.id_item = p_id_item
            ORDER BY mp.nombre_materia_prima;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20092, 'Error listando receta: ' || SQLERRM);
    END sp_listar_receta;


    -- ==================== REPORTE ====================

    PROCEDURE sp_reporte_ordenes_cliente(p_id_cliente IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
        v_existe     NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM cliente WHERE id_cliente = p_id_cliente;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        OPEN p_cursor FOR
            SELECT o.id_orden, o.fecha, e.nombre AS estado,
                   fn_total_orden(o.id_orden) AS total
            FROM orden o
            JOIN estado e ON o.id_estado = e.id_estado
            WHERE o.id_cliente = p_id_cliente
            ORDER BY o.fecha DESC;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20095, 'Cliente no encontrado: ' || p_id_cliente);
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20096, 'Error generando reporte: ' || SQLERRM);
    END sp_reporte_ordenes_cliente;

END pkg_fabrica;
/



-- ============================================================
-- SECCIÓN 2: pkg_usuarios
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_usuarios AS
    PROCEDURE sp_insertar_usuario(p_nombre IN VARCHAR2, p_correo IN VARCHAR2,
                                   p_contrasena IN VARCHAR2, p_id_rol IN INTEGER,
                                   p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);
    PROCEDURE sp_actualizar_usuario(p_id_usuario IN INTEGER, p_nombre IN VARCHAR2,
                                     p_correo IN VARCHAR2, p_id_rol IN INTEGER);
    PROCEDURE sp_eliminar_usuario(p_id_usuario IN INTEGER);
    PROCEDURE sp_obtener_usuario(p_id_usuario IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_usuarios(p_cursor OUT SYS_REFCURSOR);
    FUNCTION fn_existe_correo(p_correo IN VARCHAR2) RETURN NUMBER;
    FUNCTION fn_contar_usuarios_rol(p_id_rol IN INTEGER) RETURN NUMBER;
    PROCEDURE sp_login(
    p_correo IN VARCHAR2,
    p_contrasena IN VARCHAR2,
    p_cursor OUT SYS_REFCURSOR
    );
END pkg_usuarios;
/

CREATE OR REPLACE PACKAGE BODY pkg_usuarios AS

    FUNCTION fn_existe_correo(p_correo IN VARCHAR2) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total FROM usuario WHERE UPPER(correo) = UPPER(p_correo);
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN RETURN 0;
    END fn_existe_correo;

    FUNCTION fn_contar_usuarios_rol(p_id_rol IN INTEGER) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total FROM usuario WHERE id_rol = p_id_rol;
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20107, 'Error contando usuarios del rol: ' || p_id_rol);
    END fn_contar_usuarios_rol;

    PROCEDURE sp_insertar_usuario(p_nombre IN VARCHAR2, p_correo IN VARCHAR2,
                                   p_contrasena IN VARCHAR2, p_id_rol IN INTEGER,
                                   p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
        ex_correo_duplicado EXCEPTION;
    BEGIN
        IF fn_existe_correo(p_correo) > 0 THEN RAISE ex_correo_duplicado; END IF;
        INSERT INTO usuario(nombre, correo, contrasena, id_rol, id_estado)
        VALUES (p_nombre, p_correo, p_contrasena, p_id_rol, p_id_estado)
        RETURNING id_usuario INTO p_id_nuevo;
        COMMIT;
    EXCEPTION
        WHEN ex_correo_duplicado THEN
            RAISE_APPLICATION_ERROR(-20100, 'El correo ya está registrado: ' || p_correo);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20101, 'Error insertando usuario: ' || SQLERRM);
    END sp_insertar_usuario;

    PROCEDURE sp_actualizar_usuario(p_id_usuario IN INTEGER, p_nombre IN VARCHAR2,
                                     p_correo IN VARCHAR2, p_id_rol IN INTEGER) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM usuario WHERE id_usuario = p_id_usuario;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        UPDATE usuario SET nombre = p_nombre, correo = p_correo, id_rol = p_id_rol
        WHERE id_usuario = p_id_usuario;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20102, 'Usuario no encontrado: ' || p_id_usuario);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20103, 'Error actualizando usuario: ' || SQLERRM);
    END sp_actualizar_usuario;

    PROCEDURE sp_eliminar_usuario(p_id_usuario IN INTEGER) IS
        v_id_inactivo estado.id_estado%TYPE;
        v_existe      NUMBER;
        ex_no_existe  EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM usuario WHERE id_usuario = p_id_usuario;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        SELECT id_estado INTO v_id_inactivo FROM estado
        WHERE UPPER(nombre) = 'INACTIVO' AND ROWNUM = 1;
        UPDATE usuario SET id_estado = v_id_inactivo WHERE id_usuario = p_id_usuario;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20104, 'Usuario no encontrado: ' || p_id_usuario);
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20105, 'Estado INACTIVO no existe.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20106, 'Error eliminando usuario: ' || SQLERRM);
    END sp_eliminar_usuario;

    PROCEDURE sp_obtener_usuario(p_id_usuario IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT u.id_usuario, u.nombre, u.correo,
                   r.nombre AS rol,    u.id_rol,
                   e.nombre AS estado, u.id_estado
            FROM usuario u
            JOIN rol    r ON u.id_rol    = r.id_rol
            JOIN estado e ON u.id_estado = e.id_estado
            WHERE u.id_usuario = p_id_usuario;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20108, 'Error obteniendo usuario: ' || SQLERRM);
    END sp_obtener_usuario;

    PROCEDURE sp_listar_usuarios(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT u.id_usuario, u.nombre, u.correo,
                   r.nombre AS rol,
                   e.nombre AS estado
            FROM usuario u
            JOIN rol    r ON u.id_rol    = r.id_rol
            JOIN estado e ON u.id_estado = e.id_estado
            ORDER BY u.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20109, 'Error listando usuarios: ' || SQLERRM);
    END sp_listar_usuarios;

        PROCEDURE sp_login(
        p_correo IN VARCHAR2,
        p_contrasena IN VARCHAR2,
        p_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT u.id_usuario,
                u.nombre,
                u.correo,
                u.id_rol,
                u.id_estado,
                r.nombre AS rol,
                e.nombre AS estado
            FROM usuario u
            JOIN rol r ON u.id_rol = r.id_rol
            JOIN estado e ON u.id_estado = e.id_estado
            WHERE UPPER(u.correo) = UPPER(p_correo)
            AND u.contrasena = p_contrasena
            AND e.nombre = 'Activo';
    END sp_login;

END pkg_usuarios;
/


-- ============================================================
-- SECCIÓN 3: pkg_proveedores
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_proveedores AS
    PROCEDURE sp_actualizar_proveedor(
        p_id_proveedor IN INTEGER,
        p_nombre       IN VARCHAR2,
        p_contacto     IN VARCHAR2,
        p_telefono     IN VARCHAR2,
        p_correo       IN VARCHAR2
    );
    PROCEDURE sp_eliminar_proveedor(p_id_proveedor IN INTEGER);

    FUNCTION fn_total_materias_proveedor(p_id_proveedor IN INTEGER) RETURN NUMBER;
END pkg_proveedores;
/

CREATE OR REPLACE PACKAGE BODY pkg_proveedores AS

    -- SP1: Actualizar proveedor
    PROCEDURE sp_actualizar_proveedor(
        p_id_proveedor IN INTEGER,
        p_nombre       IN VARCHAR2,
        p_contacto     IN VARCHAR2,
        p_telefono     IN VARCHAR2,
        p_correo       IN VARCHAR2
    ) IS
        v_existe     NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe
        FROM proveedor WHERE id_proveedor = p_id_proveedor;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        UPDATE proveedor
        SET nombre   = p_nombre,
            contacto = p_contacto,
            telefono = p_telefono,
            correo   = p_correo
        WHERE id_proveedor = p_id_proveedor;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20200, 'Proveedor no encontrado: ' || p_id_proveedor);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20201, 'Error actualizando proveedor: ' || SQLERRM);
    END sp_actualizar_proveedor;

    -- SP2: Eliminar proveedor (lógico)
    PROCEDURE sp_eliminar_proveedor(p_id_proveedor IN INTEGER) IS
        v_id_inactivo estado.id_estado%TYPE;
        v_existe      NUMBER;
        ex_no_existe  EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe
        FROM proveedor WHERE id_proveedor = p_id_proveedor;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        SELECT id_estado INTO v_id_inactivo
        FROM estado
        WHERE UPPER(nombre) = 'INACTIVO'
        AND ROWNUM = 1;

        UPDATE proveedor
        SET id_estado = v_id_inactivo
        WHERE id_proveedor = p_id_proveedor;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20202, 'Proveedor no encontrado: ' || p_id_proveedor);
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20203, 'Estado INACTIVO no existe en tabla estado.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20204, 'Error eliminando proveedor: ' || SQLERRM);
    END sp_eliminar_proveedor;

    -- FN: Cuenta materias primas de un proveedor
    FUNCTION fn_total_materias_proveedor(p_id_proveedor IN INTEGER) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total
        FROM materia_prima
        WHERE id_proveedor = p_id_proveedor;
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20205, 'Error contando materias del proveedor: ' || p_id_proveedor);
    END fn_total_materias_proveedor;

END pkg_proveedores;
/


-- ============================================================
-- SECCIÓN 4: pkg_inventario
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_inventario AS
    PROCEDURE sp_rebajar_inventario(
        p_id_item  IN INTEGER,
        p_cantidad IN NUMBER
    )
    PROCEDURE sp_ajustar_inventario(
        p_id_lote IN INTEGER,
        p_nueva_cantidad IN NUMBER,
        p_fecha_vencimiento IN TIMESTAMP
    )

    PROCEDURE sp_obtener_lote(
    p_id_lote IN INTEGER,
    p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE sp_actualizar_fecha_vencimiento(
    p_id_lote IN INTEGER,
    p_fecha_vencimiento IN TIMESTAMP
);

    FUNCTION fn_valor_total_inventario RETURN NUMBER;
END pkg_inventario;
/

CREATE OR REPLACE PACKAGE BODY pkg_inventario AS

    -- SP1: Rebajar stock del inventario (descuenta del lote más antiguo)
    PROCEDURE sp_rebajar_inventario(
        p_id_item  IN INTEGER,
        p_cantidad IN NUMBER
    ) IS
        v_stock_actual NUMBER;
        ex_sin_stock   EXCEPTION;

        CURSOR c_lotes(p_item INTEGER) IS
            SELECT id_lote, cantidad
            FROM inventario_de_items
            WHERE id_item = p_item AND cantidad > 0
            ORDER BY fecha_ingreso ASC;

        v_lote      c_lotes%ROWTYPE;
        v_pendiente NUMBER;
        v_a_descontar NUMBER;
    BEGIN
        SELECT NVL(SUM(cantidad), 0)
        INTO v_stock_actual
        FROM inventario_de_items
        WHERE id_item = p_id_item;

        IF v_stock_actual < p_cantidad THEN
            RAISE ex_sin_stock;
        END IF;

        v_pendiente := p_cantidad;

        OPEN c_lotes(p_id_item);
        LOOP
            FETCH c_lotes INTO v_lote;
            EXIT WHEN c_lotes%NOTFOUND OR v_pendiente <= 0;

            IF v_lote.cantidad >= v_pendiente THEN
                v_a_descontar := v_pendiente;
            ELSE
                v_a_descontar := v_lote.cantidad;
            END IF;

            UPDATE inventario_de_items
            SET cantidad = cantidad - v_a_descontar
            WHERE id_lote = v_lote.id_lote;

            v_pendiente := v_pendiente - v_a_descontar;
        END LOOP;
        CLOSE c_lotes;

        COMMIT;
    EXCEPTION
        WHEN ex_sin_stock THEN
            RAISE_APPLICATION_ERROR(-20300, 'Stock insuficiente para el item: ' || p_id_item);
        WHEN OTHERS THEN
            IF c_lotes%ISOPEN THEN CLOSE c_lotes; END IF;
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20301, 'Error rebajando inventario: ' || SQLERRM);
    END sp_rebajar_inventario;

    -- SP2: Ajustar cantidad de un lote específico
        PROCEDURE sp_ajustar_inventario(
        p_id_lote IN INTEGER,
        p_nueva_cantidad IN NUMBER,
        p_fecha_vencimiento IN TIMESTAMP
    ) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        IF p_nueva_cantidad < 0 THEN
            RAISE_APPLICATION_ERROR(-20302, 'La cantidad no puede ser negativa.');
        END IF;

        SELECT COUNT(*) INTO v_existe
        FROM inventario_de_items
        WHERE id_lote = p_id_lote;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        UPDATE inventario_de_items
        SET cantidad = p_nueva_cantidad,
            fecha_vencimiento = p_fecha_vencimiento
        WHERE id_lote = p_id_lote;

        COMMIT;

    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20303, 'Lote no encontrado: ' || p_id_lote);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20304, 'Error ajustando inventario: ' || SQLERRM);
    END sp_ajustar_inventario;

    -- FN: Valor total del inventario (stock * precio_unitario)
    FUNCTION fn_valor_total_inventario RETURN NUMBER IS
        v_valor NUMBER;
    BEGIN
        SELECT NVL(SUM(inv.cantidad * i.precio_unitario), 0)
        INTO v_valor
        FROM inventario_de_items inv
        JOIN item i ON inv.id_item = i.id_item;
        RETURN v_valor;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20305, 'Error calculando valor del inventario: ' || SQLERRM);
    END fn_valor_total_inventario;

    PROCEDURE sp_obtener_lote(
            p_id_lote IN INTEGER,
            p_cursor OUT SYS_REFCURSOR
        ) IS
        BEGIN
            OPEN p_cursor FOR
                SELECT inv.id_lote,
                    inv.id_item,
                    i.nombre AS producto,
                    inv.cantidad,
                    inv.fecha_ingreso,
                    inv.fecha_vencimiento
                FROM inventario_de_items inv
                JOIN item i ON inv.id_item = i.id_item
                WHERE inv.id_lote = p_id_lote;
        END;

        PROCEDURE sp_actualizar_fecha_vencimiento(
        p_id_lote IN INTEGER,
        p_fecha_vencimiento IN TIMESTAMP
    ) IS
    BEGIN
        UPDATE inventario_de_items
        SET fecha_vencimiento = p_fecha_vencimiento
        WHERE id_lote = p_id_lote;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20306, 'Error actualizando fecha: ' || SQLERRM);
    END;

END pkg_inventario;
/

-- ============================================================
-- SECCIÓN 5: pkg_ordenes
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_ordenes AS
    PROCEDURE sp_finalizar_orden(p_id_orden IN INTEGER);
    FUNCTION fn_total_ordenes_pendientes RETURN NUMBER;
    PROCEDURE sp_obtener_orden(
    p_id_orden IN INTEGER,
    p_cursor OUT SYS_REFCURSOR
);
END pkg_ordenes;
/

CREATE OR REPLACE PACKAGE BODY pkg_ordenes AS

    -- SP: Finalizar una orden (cambia estado a 'Activo' = completada)
    PROCEDURE sp_finalizar_orden(p_id_orden IN INTEGER) IS
        v_id_completado estado.id_estado%TYPE;
        v_existe        NUMBER;
        ex_no_existe    EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe
        FROM orden WHERE id_orden = p_id_orden;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        SELECT id_estado INTO v_id_completado
        FROM estado
        WHERE UPPER(nombre) = 'ACTIVO'
        AND ROWNUM = 1;

        UPDATE orden
        SET id_estado = v_id_completado
        WHERE id_orden = p_id_orden;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20400, 'Orden no encontrada: ' || p_id_orden);
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20401, 'Estado ACTIVO no existe en tabla estado.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20402, 'Error finalizando orden: ' || SQLERRM);
    END sp_finalizar_orden;

    -- FN: Cuenta órdenes en estado 'Pendiente'
    FUNCTION fn_total_ordenes_pendientes RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total
        FROM orden o
        JOIN estado e ON o.id_estado = e.id_estado
        WHERE UPPER(e.nombre) = 'PENDIENTE';
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20403, 'Error contando órdenes pendientes: ' || SQLERRM);
    END fn_total_ordenes_pendientes;

            PROCEDURE sp_obtener_orden(
            p_id_orden IN INTEGER,
            p_cursor OUT SYS_REFCURSOR
        ) IS
        BEGIN
            OPEN p_cursor FOR
                SELECT o.id_orden,
                    o.fecha,
                    o.id_cliente,
                    o.id_usuario,
                    o.id_estado,
                    c.nombre AS cliente,
                    u.nombre AS usuario,
                    e.nombre AS estado
                FROM orden o
                JOIN cliente c ON o.id_cliente = c.id_cliente
                JOIN usuario u ON o.id_usuario = u.id_usuario
                JOIN estado e ON o.id_estado = e.id_estado
                WHERE o.id_orden = p_id_orden;
        END;

END pkg_ordenes;
/

-- ============================================================
-- SECCIÓN 6: pkg_reportes
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_reportes AS
    PROCEDURE sp_reporte_stock(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_reporte_proveedores(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_reporte_usuarios(p_cursor OUT SYS_REFCURSOR);
    FUNCTION fn_items_en_orden(p_id_orden IN INTEGER) RETURN NUMBER;
END pkg_reportes;
/

CREATE OR REPLACE PACKAGE BODY pkg_reportes AS

    -- SP1: Reporte de stock de todos los productos
    PROCEDURE sp_reporte_stock(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT i.id_item,
                   i.nombre        AS producto,
                   i.unidad_medida,
                   i.precio_unitario,
                   NVL(SUM(inv.cantidad), 0) AS stock_total,
                   e.nombre        AS estado
            FROM item i
            LEFT JOIN inventario_de_items inv ON i.id_item   = inv.id_item
            LEFT JOIN estado              e   ON i.id_estado = e.id_estado
            GROUP BY i.id_item, i.nombre, i.unidad_medida, i.precio_unitario, e.nombre
            ORDER BY i.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20500, 'Error generando reporte de stock: ' || SQLERRM);
    END sp_reporte_stock;

    -- SP2: Reporte de todos los proveedores con sus materias
    PROCEDURE sp_reporte_proveedores(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT p.id_proveedor,
                   p.nombre       AS proveedor,
                   p.contacto,
                   p.telefono,
                   COUNT(mp.id_materia_prima) AS total_materias,
                   e.nombre       AS estado
            FROM proveedor p
            LEFT JOIN materia_prima mp ON p.id_proveedor = mp.id_proveedor
            JOIN  estado            e  ON p.id_estado    = e.id_estado
            GROUP BY p.id_proveedor, p.nombre, p.contacto, p.telefono, e.nombre
            ORDER BY p.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20501, 'Error generando reporte de proveedores: ' || SQLERRM);
    END sp_reporte_proveedores;

    -- SP3: Reporte de usuarios con rol y estado
    PROCEDURE sp_reporte_usuarios(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT u.id_usuario,
                   u.nombre   AS usuario,
                   u.correo,
                   r.nombre   AS rol,
                   e.nombre   AS estado
            FROM usuario u
            JOIN rol    r ON u.id_rol    = r.id_rol
            JOIN estado e ON u.id_estado = e.id_estado
            ORDER BY u.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20502, 'Error generando reporte de usuarios: ' || SQLERRM);
    END sp_reporte_usuarios;

    -- FN: Cuenta cuántos ítems distintos tiene una orden
    FUNCTION fn_items_en_orden(p_id_orden IN INTEGER) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total
        FROM detalle_orden
        WHERE id_orden = p_id_orden;
        RETURN v_total;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0;
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20503, 'Error contando items de la orden: ' || p_id_orden);
    END fn_items_en_orden;

END pkg_reportes;
/


-- ============================================================
-- SECCIÓN 7: pkg_items
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_items AS
    PROCEDURE sp_insertar_receta(
        p_id_item            IN INTEGER,
        p_id_materia_prima   IN INTEGER,
        p_medida             IN NUMBER,
        p_unidad_medida      IN VARCHAR2
    );
    FUNCTION fn_margen_ganancia(p_id_item IN INTEGER) RETURN NUMBER;
END pkg_items;
/

CREATE OR REPLACE PACKAGE BODY pkg_items AS

    -- SP: Insertar una línea de receta (materia prima → producto)
    PROCEDURE sp_insertar_receta(
        p_id_item          IN INTEGER,
        p_id_materia_prima IN INTEGER,
        p_medida           IN NUMBER,
        p_unidad_medida    IN VARCHAR2
    ) IS
        v_existe_item   NUMBER;
        v_existe_mp     NUMBER;
        ex_item_no_existe EXCEPTION;
        ex_mp_no_existe   EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe_item FROM item          WHERE id_item          = p_id_item;
        SELECT COUNT(*) INTO v_existe_mp   FROM materia_prima WHERE id_materia_prima = p_id_materia_prima;

        IF v_existe_item = 0 THEN RAISE ex_item_no_existe; END IF;
        IF v_existe_mp   = 0 THEN RAISE ex_mp_no_existe;   END IF;

        INSERT INTO pre_producto_item(id_item, id_materia_prima, medida_materia_prima, unidad_medida)
        VALUES (p_id_item, p_id_materia_prima, p_medida, p_unidad_medida);
        COMMIT;
    EXCEPTION
        WHEN ex_item_no_existe THEN
            RAISE_APPLICATION_ERROR(-20600, 'Item no encontrado: ' || p_id_item);
        WHEN ex_mp_no_existe THEN
            RAISE_APPLICATION_ERROR(-20601, 'Materia prima no encontrada: ' || p_id_materia_prima);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20602, 'Error insertando receta: ' || SQLERRM);
    END sp_insertar_receta;

    -- FN: Calcula el margen de ganancia de un item (precio - costo fabricación)
    FUNCTION fn_margen_ganancia(p_id_item IN INTEGER) RETURN NUMBER IS
        v_precio NUMBER;
        v_costo  NUMBER;
    BEGIN
        SELECT precio_unitario INTO v_precio
        FROM item WHERE id_item = p_id_item;

        v_costo := pkg_fabrica.fn_costo_fabricacion(p_id_item);

        RETURN v_precio - v_costo;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20603, 'Item no encontrado: ' || p_id_item);
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20604, 'Error calculando margen: ' || SQLERRM);
    END fn_margen_ganancia;

END pkg_items;
/


-- ============================================================
-- SECCIÓN 8: pkg_materias_primas
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_materias_primas AS
    PROCEDURE sp_insertar_materia_prima(
        p_id_proveedor    IN INTEGER,
        p_nombre          IN VARCHAR2,
        p_precio          IN NUMBER,
        p_id_nueva        OUT INTEGER
    );
    PROCEDURE sp_actualizar_materia_prima(
        p_id_materia_prima IN INTEGER,
        p_nombre           IN VARCHAR2,
        p_precio           IN NUMBER
    );
    FUNCTION fn_costo_promedio_materias RETURN NUMBER;
END pkg_materias_primas;
/

CREATE OR REPLACE PACKAGE BODY pkg_materias_primas AS

    -- SP1: Insertar materia prima
    PROCEDURE sp_insertar_materia_prima(
        p_id_proveedor IN INTEGER,
        p_nombre       IN VARCHAR2,
        p_precio       IN NUMBER,
        p_id_nueva     OUT INTEGER
    ) IS
        v_existe_prov   NUMBER;
        ex_prov_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe_prov
        FROM proveedor WHERE id_proveedor = p_id_proveedor;

        IF v_existe_prov = 0 THEN
            RAISE ex_prov_no_existe;
        END IF;

        IF p_precio < 0 THEN
            RAISE_APPLICATION_ERROR(-20700, 'El precio no puede ser negativo.');
        END IF;

        INSERT INTO materia_prima(id_proveedor, nombre_materia_prima, precio_referencia)
        VALUES (p_id_proveedor, p_nombre, p_precio)
        RETURNING id_materia_prima INTO p_id_nueva;
        COMMIT;
    EXCEPTION
        WHEN ex_prov_no_existe THEN
            RAISE_APPLICATION_ERROR(-20701, 'Proveedor no encontrado: ' || p_id_proveedor);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20702, 'Error insertando materia prima: ' || SQLERRM);
    END sp_insertar_materia_prima;

    -- SP2: Actualizar precio de materia prima
    PROCEDURE sp_actualizar_materia_prima(
        p_id_materia_prima IN INTEGER,
        p_nombre           IN VARCHAR2,
        p_precio           IN NUMBER
    ) IS
        v_existe     NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe
        FROM materia_prima WHERE id_materia_prima = p_id_materia_prima;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        UPDATE materia_prima
        SET nombre_materia_prima = p_nombre,
            precio_referencia    = p_precio
        WHERE id_materia_prima = p_id_materia_prima;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20703, 'Materia prima no encontrada: ' || p_id_materia_prima);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20704, 'Error actualizando materia prima: ' || SQLERRM);
    END sp_actualizar_materia_prima;

    -- FN: Promedio de precios de todas las materias primas
    FUNCTION fn_costo_promedio_materias RETURN NUMBER IS
        v_promedio NUMBER;
    BEGIN
        SELECT NVL(AVG(precio_referencia), 0)
        INTO v_promedio
        FROM materia_prima;
        RETURN v_promedio;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20705, 'Error calculando promedio de materias: ' || SQLERRM);
    END fn_costo_promedio_materias;

END pkg_materias_primas;
/


-- ============================================================
-- SECCIÓN 9: pkg_auditoria
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_auditoria AS
    PROCEDURE sp_registrar_auditoria(
        p_tabla     IN VARCHAR2,
        p_operacion IN VARCHAR2,
        p_anterior  IN CLOB,
        p_nuevo     IN CLOB
    );
END pkg_auditoria;
/

CREATE OR REPLACE PACKAGE BODY pkg_auditoria AS

    -- SP: Registrar entrada en auditoría
    PROCEDURE sp_registrar_auditoria(
        p_tabla     IN VARCHAR2,
        p_operacion IN VARCHAR2,
        p_anterior  IN CLOB,
        p_nuevo     IN CLOB
    ) IS
    BEGIN
        INSERT INTO auditoria(tabla_afectada, operacion, valor_anterior, valor_nuevo, usuario_bd, fecha)
        VALUES (p_tabla, p_operacion, p_anterior, p_nuevo, USER, SYSTIMESTAMP);
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20800, 'Error registrando auditoría: ' || SQLERRM);
    END sp_registrar_auditoria;

END pkg_auditoria;
/


-- ============================================================
-- SECCIÓN 10: pkg_roles
-- ============================================================
CREATE OR REPLACE PACKAGE pkg_roles AS
    PROCEDURE sp_insertar_rol(p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2,
                               p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);
    PROCEDURE sp_actualizar_rol(p_id_rol IN INTEGER, p_nombre IN VARCHAR2,
                                 p_descripcion IN VARCHAR2);
    PROCEDURE sp_eliminar_rol(p_id_rol IN INTEGER);
    PROCEDURE sp_obtener_rol(p_id_rol IN INTEGER, p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_listar_roles(p_cursor OUT SYS_REFCURSOR);
    FUNCTION fn_existe_rol(p_nombre IN VARCHAR2) RETURN NUMBER;
END pkg_roles;
/

CREATE OR REPLACE PACKAGE BODY pkg_roles AS

    FUNCTION fn_existe_rol(p_nombre IN VARCHAR2) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total FROM rol WHERE UPPER(nombre) = UPPER(p_nombre);
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN RETURN 0;
    END fn_existe_rol;

    PROCEDURE sp_insertar_rol(p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2,
                               p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
        ex_duplicado EXCEPTION;
    BEGIN
        IF fn_existe_rol(p_nombre) > 0 THEN RAISE ex_duplicado; END IF;
        INSERT INTO rol(nombre, descripcion, id_estado)
        VALUES (p_nombre, p_descripcion, p_id_estado)
        RETURNING id_rol INTO p_id_nuevo;
        COMMIT;
    EXCEPTION
        WHEN ex_duplicado THEN
            RAISE_APPLICATION_ERROR(-20900, 'El rol ya existe: ' || p_nombre);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20901, 'Error insertando rol: ' || SQLERRM);
    END sp_insertar_rol;

    PROCEDURE sp_actualizar_rol(p_id_rol IN INTEGER, p_nombre IN VARCHAR2,
                                 p_descripcion IN VARCHAR2) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM rol WHERE id_rol = p_id_rol;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        UPDATE rol SET nombre = p_nombre, descripcion = p_descripcion WHERE id_rol = p_id_rol;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20902, 'Rol no encontrado: ' || p_id_rol);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20903, 'Error actualizando rol: ' || SQLERRM);
    END sp_actualizar_rol;

    PROCEDURE sp_eliminar_rol(p_id_rol IN INTEGER) IS
        v_en_uso     NUMBER;
        v_existe     NUMBER;
        ex_no_existe EXCEPTION;
        ex_en_uso    EXCEPTION;
    BEGIN
        SELECT COUNT(*) INTO v_existe FROM rol WHERE id_rol = p_id_rol;
        IF v_existe = 0 THEN RAISE ex_no_existe; END IF;
        SELECT COUNT(*) INTO v_en_uso FROM usuario WHERE id_rol = p_id_rol;
        IF v_en_uso > 0 THEN RAISE ex_en_uso; END IF;
        DELETE FROM rol WHERE id_rol = p_id_rol;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20904, 'Rol no encontrado: ' || p_id_rol);
        WHEN ex_en_uso THEN
            RAISE_APPLICATION_ERROR(-20905, 'No se puede eliminar: el rol tiene usuarios asignados.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20906, 'Error eliminando rol: ' || SQLERRM);
    END sp_eliminar_rol;

    PROCEDURE sp_obtener_rol(p_id_rol IN INTEGER, p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT r.id_rol, r.nombre, r.descripcion,
                   e.nombre AS estado, r.id_estado
            FROM rol r
            JOIN estado e ON r.id_estado = e.id_estado
            WHERE r.id_rol = p_id_rol;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20907, 'Error obteniendo rol: ' || SQLERRM);
    END sp_obtener_rol;

   PROCEDURE sp_listar_roles(p_cursor OUT SYS_REFCURSOR) IS
    BEGIN
        OPEN p_cursor FOR
            SELECT r.id_rol,
                r.nombre,
                r.descripcion,
                r.id_estado,
                e.nombre AS estado
            FROM rol r
            JOIN estado e ON r.id_estado = e.id_estado
            ORDER BY r.nombre;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20908, 'Error listando roles: ' || SQLERRM);
    END sp_listar_roles;

END pkg_roles;
/

-- ============================================================
-- SECCIÓN 11: pkg_vistas
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_vistas AS
    PROCEDURE sp_stock_items(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_ordenes_detalle(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_detalle_ordenes(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_materias_primas(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_receta_items(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_usuarios(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_proveedores_activos(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_resumen_financiero_clientes(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_alerta_inventario(p_cursor OUT SYS_REFCURSOR);
    PROCEDURE sp_roles_usuarios(p_cursor OUT SYS_REFCURSOR);
END pkg_vistas;
/

CREATE OR REPLACE PACKAGE BODY pkg_vistas AS

    -- Vista 1: Stock de items
    PROCEDURE sp_stock_items(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                i.id_item,
                i.nombre AS producto,
                i.unidad_medida,
                i.precio_unitario,
                NVL(SUM(inv.cantidad), 0) AS stock_total,
                e.nombre AS estado
            FROM item i
            LEFT JOIN inventario_de_items inv ON i.id_item = inv.id_item
            LEFT JOIN estado e ON i.id_estado = e.id_estado
            GROUP BY i.id_item, i.nombre, i.unidad_medida, i.precio_unitario, e.nombre
            ORDER BY i.nombre;
    END sp_stock_items;

    -- Vista 2: Órdenes con datos del cliente y estado
    PROCEDURE sp_ordenes_detalle(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                o.id_orden,
                o.fecha,
                c.nombre AS cliente,
                c.telefono,
                c.correo AS correo_cliente,
                e.nombre AS estado_orden,
                u.nombre AS atendido_por
            FROM orden o
            JOIN cliente c ON o.id_cliente = c.id_cliente
            JOIN estado e ON o.id_estado = e.id_estado
            JOIN usuario u ON o.id_usuario = u.id_usuario
            ORDER BY o.fecha DESC;
    END sp_ordenes_detalle;

    -- Vista 3: Detalle completo de órdenes (líneas de pedido)
    PROCEDURE sp_detalle_ordenes(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                d.id_detalle,
                d.id_orden,
                i.nombre AS producto,
                d.cantidad,
                i.precio_unitario,
                (d.cantidad * i.precio_unitario) AS subtotal
            FROM detalle_orden d
            JOIN item i ON d.id_item = i.id_item
            ORDER BY d.id_orden, d.id_detalle;
    END sp_detalle_ordenes;

    -- Vista 4: Materias primas con proveedor
    PROCEDURE sp_materias_primas(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                mp.id_materia_prima,
                mp.nombre_materia_prima,
                mp.precio_referencia,
                p.nombre AS proveedor,
                p.telefono AS telefono_proveedor,
                p.correo AS correo_proveedor
            FROM materia_prima mp
            JOIN proveedor p ON mp.id_proveedor = p.id_proveedor
            ORDER BY p.nombre, mp.nombre_materia_prima;
    END sp_materias_primas;

    -- Vista 5: Receta de cada producto (materias primas que lo componen)
    PROCEDURE sp_receta_items(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                i.id_item,
                i.nombre AS producto,
                mp.nombre_materia_prima,
                pp.medida_materia_prima,
                pp.unidad_medida
            FROM pre_producto_item pp
            JOIN item i ON pp.id_item = i.id_item
            JOIN materia_prima mp ON pp.id_materia_prima = mp.id_materia_prima
            ORDER BY i.nombre, mp.nombre_materia_prima;
    END sp_receta_items;

    -- Vista 6: Usuarios con su rol y estado
    PROCEDURE sp_usuarios(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                u.id_usuario,
                u.nombre,
                u.correo,
                r.nombre AS rol,
                e.nombre AS estado
            FROM usuario u
            JOIN rol r ON u.id_rol = r.id_rol
            JOIN estado e ON u.id_estado = e.id_estado
            ORDER BY u.nombre;
    END sp_usuarios;

    -- Vista 7: Proveedores activos con cantidad de materias primas que suministran
    PROCEDURE sp_proveedores_activos(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                p.id_proveedor,
                p.nombre,
                p.contacto,
                p.telefono,
                p.correo,
                COUNT(mp.id_materia_prima) AS total_materias
            FROM proveedor p
            LEFT JOIN materia_prima mp ON p.id_proveedor = mp.id_proveedor
            JOIN estado e ON p.id_estado = e.id_estado
            WHERE UPPER(e.nombre) = 'ACTIVO'
            GROUP BY p.id_proveedor, p.nombre, p.contacto, p.telefono, p.correo
            ORDER BY p.nombre;
    END sp_proveedores_activos;

    -- Vista 8: Resumen financiero de órdenes por cliente
    PROCEDURE sp_resumen_financiero_clientes(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                c.id_cliente,
                c.nombre AS cliente,
                COUNT(o.id_orden) AS total_ordenes,
                NVL(SUM(d.cantidad * i.precio_unitario), 0) AS total_facturado
            FROM cliente c
            LEFT JOIN orden o ON c.id_cliente = o.id_cliente
            LEFT JOIN detalle_orden d ON o.id_orden = d.id_orden
            LEFT JOIN item i ON d.id_item = i.id_item
            GROUP BY c.id_cliente, c.nombre
            ORDER BY total_facturado DESC;
    END sp_resumen_financiero_clientes;

    -- Vista 9: Inventario con estado de alerta de stock
    PROCEDURE sp_alerta_inventario(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                i.id_item,
                i.nombre AS producto,
                NVL(SUM(inv.cantidad), 0) AS stock_actual,
                CASE
                    WHEN NVL(SUM(inv.cantidad), 0) = 0 THEN 'SIN STOCK'
                    WHEN NVL(SUM(inv.cantidad), 0) < 10 THEN 'STOCK BAJO'
                    WHEN NVL(SUM(inv.cantidad), 0) < 50 THEN 'STOCK MEDIO'
                    ELSE 'STOCK OK'
                END AS alerta
            FROM item i
            LEFT JOIN inventario_de_items inv ON i.id_item = inv.id_item
            GROUP BY i.id_item, i.nombre
            ORDER BY i.nombre;
    END sp_alerta_inventario;

    -- Vista 10: Roles con cantidad de usuarios asignados
    PROCEDURE sp_roles_usuarios(p_cursor OUT SYS_REFCURSOR) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT
                r.id_rol,
                r.nombre AS rol,
                r.descripcion,
                e.nombre AS estado,
                COUNT(u.id_usuario) AS total_usuarios
            FROM rol r
            LEFT JOIN usuario u ON r.id_rol = u.id_rol
            JOIN estado e ON r.id_estado = e.id_estado
            GROUP BY r.id_rol, r.nombre, r.descripcion, e.nombre
            ORDER BY r.nombre;
    END sp_roles_usuarios;

END pkg_vistas;
/

COMMIT;
-- ============================================================
-- SECCIÓN 12: CURSORES
-- ============================================================

-- Cursor 1: Listar todos los items activos con su stock
DECLARE
    CURSOR c_items_activos IS
        SELECT i.id_item, i.nombre, i.precio_unitario, NVL(SUM(inv.cantidad), 0) AS stock
        FROM item i
        LEFT JOIN inventario_de_items inv ON i.id_item = inv.id_item
        JOIN estado e ON i.id_estado = e.id_estado
        WHERE UPPER(e.nombre) = 'ACTIVO'
        GROUP BY i.id_item, i.nombre, i.precio_unitario;

    v_fila c_items_activos%ROWTYPE;
BEGIN
    OPEN c_items_activos;
    LOOP
        FETCH c_items_activos INTO v_fila;
        EXIT WHEN c_items_activos%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Producto: ' || v_fila.nombre ||' | Precio: ' || v_fila.precio_unitario ||' | Stock: '  || v_fila.stock);
    END LOOP;
    CLOSE c_items_activos;
END;
/

-- Cursor 2: Listar clientes con numero de órdenes
DECLARE
    CURSOR c_clientes_ordenes IS
        SELECT c.id_cliente, c.nombre, COUNT(o.id_orden) AS total_ordenes
        FROM cliente c
        LEFT JOIN orden o ON c.id_cliente = o.id_cliente
        GROUP BY c.id_cliente, c.nombre
        ORDER BY total_ordenes DESC;

    v_fila c_clientes_ordenes%ROWTYPE;
BEGIN
    OPEN c_clientes_ordenes;
    LOOP
        FETCH c_clientes_ordenes INTO v_fila;
        EXIT WHEN c_clientes_ordenes%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Cliente: ' || v_fila.nombre || ' | Órdenes: ' || v_fila.total_ordenes);
    END LOOP;
    CLOSE c_clientes_ordenes;
END;
/

-- Cursor 3: Materias primas con precio mayor a un monto dado 
DECLARE
    CURSOR c_mp_precio(p_precio_min NUMBER) IS
        SELECT mp.nombre_materia_prima, mp.precio_referencia, p.nombre AS proveedor
        FROM materia_prima mp
        JOIN proveedor p ON mp.id_proveedor = p.id_proveedor
        WHERE mp.precio_referencia > p_precio_min
        ORDER BY mp.precio_referencia DESC;
    v_fila c_mp_precio%ROWTYPE;
BEGIN
    OPEN c_mp_precio(500);
    LOOP
        FETCH c_mp_precio INTO v_fila;
        EXIT WHEN c_mp_precio%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Materia Prima: ' || v_fila.nombre_materia_prima || ' | Precio: '     || v_fila.precio_referencia    ||  ' | Proveedor: '  || v_fila.proveedor);
    END LOOP;
    CLOSE c_mp_precio;
END;
/

-- Cursor 4: Órdenes del día actual
DECLARE
    CURSOR c_ordenes_hoy IS
        SELECT o.id_orden, c.nombre AS cliente, e.nombre AS estado, o.fecha
        FROM orden o
        JOIN cliente c ON o.id_cliente = o.id_cliente
        JOIN estado  e ON o.id_estado  = e.id_estado
        WHERE TRUNC(o.fecha) = TRUNC(SYSDATE);

    v_fila c_ordenes_hoy%ROWTYPE;
BEGIN
    OPEN c_ordenes_hoy;
    LOOP
        FETCH c_ordenes_hoy INTO v_fila;
        EXIT WHEN c_ordenes_hoy%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Orden #' || v_fila.id_orden ||  ' | Cliente: ' || v_fila.cliente ||  ' | Estado: '  || v_fila.estado);
    END LOOP;
    CLOSE c_ordenes_hoy;
END;
/

-- Cursor 5: Lotes próximos a vencer (en los próximos 30 días)
DECLARE
    CURSOR c_lotes_por_vencer IS
        SELECT inv.id_lote, i.nombre AS producto, inv.cantidad,
               inv.fecha_vencimiento
        FROM inventario_de_items inv
        JOIN item i ON inv.id_item = i.id_item
        WHERE inv.fecha_vencimiento BETWEEN SYSTIMESTAMP AND SYSTIMESTAMP + INTERVAL '30' DAY
        ORDER BY inv.fecha_vencimiento;

    v_fila c_lotes_por_vencer%ROWTYPE;
BEGIN
    OPEN c_lotes_por_vencer;
    LOOP
        FETCH c_lotes_por_vencer INTO v_fila;
        EXIT WHEN c_lotes_por_vencer%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Lote #' || v_fila.id_lote || ' | Producto: '   || v_fila.producto  ||  ' | Cantidad: '   || v_fila.cantidad  ||' | Vence: '      || v_fila.fecha_vencimiento);
    END LOOP;
    CLOSE c_lotes_por_vencer;
END;
/

-- Cursor 6: Receta completa de un item (parametrizado)
DECLARE
    CURSOR c_receta(p_id_item INTEGER) IS
        SELECT i.nombre AS producto, mp.nombre_materia_prima,
               pp.medida_materia_prima, pp.unidad_medida,
               mp.precio_referencia
        FROM pre_producto_item pp
        JOIN item          i  ON pp.id_item         = i.id_item
        JOIN materia_prima mp ON pp.id_materia_prima = mp.id_materia_prima
        WHERE pp.id_item = p_id_item;

    v_fila c_receta%ROWTYPE;
BEGIN
    OPEN c_receta(1);
    LOOP
        FETCH c_receta INTO v_fila;
        EXIT WHEN c_receta%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Producto: ' || v_fila.producto     || ' | Materia: '     || v_fila.nombre_materia_prima || ' | Cantidad: '    || v_fila.medida_materia_prima || ' ' || v_fila.unidad_medida);
    END LOOP;
    CLOSE c_receta;
END;
/

-- Cursor 7: Proveedores con materias primas
DECLARE
    CURSOR c_prov_materias IS
        SELECT p.nombre AS proveedor, p.telefono,
               mp.nombre_materia_prima, mp.precio_referencia
        FROM proveedor p
        JOIN materia_prima mp ON p.id_proveedor = mp.id_proveedor
        ORDER BY p.nombre;
    v_fila c_prov_materias%ROWTYPE;
BEGIN
    OPEN c_prov_materias;
    LOOP
        FETCH c_prov_materias INTO v_fila;
        EXIT WHEN c_prov_materias%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Proveedor: '   || v_fila.proveedor  || ' | Materia: '  || v_fila.nombre_materia_prima || ' | Precio: '   || v_fila.precio_referencia);
    END LOOP;
    CLOSE c_prov_materias;
END;
/

-- Cursor 8: Detalle de una orden con subtotales 
DECLARE
    CURSOR c_detalle_orden(p_id_orden INTEGER) IS
        SELECT i.nombre AS producto, d.cantidad, i.precio_unitario, (d.cantidad * i.precio_unitario) AS subtotal
        FROM detalle_orden d
        JOIN item i ON d.id_item = i.id_item
        WHERE d.id_orden = p_id_orden;
    v_fila    c_detalle_orden%ROWTYPE;
    v_total   NUMBER := 0;
BEGIN
    OPEN c_detalle_orden(1);
    LOOP
        FETCH c_detalle_orden INTO v_fila;
        EXIT WHEN c_detalle_orden%NOTFOUND;
        v_total := v_total + v_fila.subtotal;
        DBMS_OUTPUT.PUT_LINE('Producto: '    || v_fila.producto    ||' | Cant: '     || v_fila.cantidad       ||' | Precio: '   || v_fila.precio_unitario||' | Subtotal: ' || v_fila.subtotal);
    END LOOP;
    CLOSE c_detalle_orden;
    DBMS_OUTPUT.PUT_LINE('----------------------------------');
    DBMS_OUTPUT.PUT_LINE('TOTAL DE LA ORDEEN: ' || v_total);
END;
/


-- Cursor 9: Reporte de usuarios activos con su rol
DECLARE
    CURSOR c_usuarios_activos IS
        SELECT u.id_usuario, u.nombre AS usuario, u.correo,
               r.nombre AS rol, e.nombre AS estado
        FROM usuario u
        JOIN rol    r ON u.id_rol    = r.id_rol
        JOIN estado e ON u.id_estado = e.id_estado
        WHERE UPPER(e.nombre) = 'ACTIVO'
        ORDER BY r.nombre, u.nombre;

    v_fila c_usuarios_activos%ROWTYPE;
BEGIN
    OPEN c_usuarios_activos;
    LOOP
        FETCH c_usuarios_activos INTO v_fila;
        EXIT WHEN c_usuarios_activos%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Usuario: ' || v_fila.usuario ||
                             ' | Correo: ' || v_fila.correo ||
                             ' | Rol: '    || v_fila.rol);
    END LOOP;
    CLOSE c_usuarios_activos;
END;
/

-- Cursor 10: Órdenes pendientes con total calculado
DECLARE
    CURSOR c_ordenes_pendientes IS
        SELECT o.id_orden, c.nombre AS cliente, o.fecha,
               NVL(SUM(d.cantidad * i.precio_unitario), 0) AS total
        FROM orden o
        JOIN cliente       c ON o.id_cliente = c.id_cliente
        JOIN estado        e ON o.id_estado  = e.id_estado
        LEFT JOIN detalle_orden d ON o.id_orden  = d.id_orden
        LEFT JOIN item          i ON d.id_item   = i.id_item
        WHERE UPPER(e.nombre) = 'PENDIENTE'
        GROUP BY o.id_orden, c.nombre, o.fecha
        ORDER BY o.fecha;

    v_fila c_ordenes_pendientes%ROWTYPE;
BEGIN
    OPEN c_ordenes_pendientes;
    LOOP
        FETCH c_ordenes_pendientes INTO v_fila;
        EXIT WHEN c_ordenes_pendientes%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Orden #' || v_fila.id_orden  ||
                             ' | Cliente: ' || v_fila.cliente ||
                             ' | Total: ₡'  || v_fila.total);
    END LOOP;
    CLOSE c_ordenes_pendientes;
END;
/

-- Cursor 11: Margen de ganancia por producto
DECLARE
    CURSOR c_margenes IS
        SELECT i.id_item, i.nombre AS producto, i.precio_unitario
        FROM item i
        JOIN estado e ON i.id_estado = e.id_estado
        WHERE UPPER(e.nombre) = 'ACTIVO';

    v_fila   c_margenes%ROWTYPE;
    v_margen NUMBER;
BEGIN
    OPEN c_margenes;
    LOOP
        FETCH c_margenes INTO v_fila;
        EXIT WHEN c_margenes%NOTFOUND;
        v_margen := pkg_items.fn_margen_ganancia(v_fila.id_item);
        DBMS_OUTPUT.PUT_LINE('Producto: '  || v_fila.producto ||
                             ' | Precio: ' || v_fila.precio_unitario ||
                             ' | Margen: ' || v_margen);
    END LOOP;
    CLOSE c_margenes;
END;
/

-- Cursor 12: Todos los roles con la cantidad de usuarios asignados
DECLARE
    CURSOR c_roles_usuarios IS
        SELECT r.id_rol, r.nombre AS rol,
               COUNT(u.id_usuario) AS total_usuarios
        FROM rol r
        LEFT JOIN usuario u ON r.id_rol = u.id_rol
        GROUP BY r.id_rol, r.nombre
        ORDER BY total_usuarios DESC;

    v_fila c_roles_usuarios%ROWTYPE;
BEGIN
    OPEN c_roles_usuarios;
    LOOP
        FETCH c_roles_usuarios INTO v_fila;
        EXIT WHEN c_roles_usuarios%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Rol: '       || v_fila.rol ||
                             ' | Usuarios: ' || v_fila.total_usuarios);
    END LOOP;
    CLOSE c_roles_usuarios;
END;
/

-- Cursor 13: Lotes ya vencidos con su producto
DECLARE
    CURSOR c_lotes_vencidos IS
        SELECT inv.id_lote, i.nombre AS producto,
               inv.cantidad, inv.fecha_vencimiento
        FROM inventario_de_items inv
        JOIN item i ON inv.id_item = i.id_item
        WHERE inv.fecha_vencimiento < SYSTIMESTAMP
        ORDER BY inv.fecha_vencimiento;

    v_fila c_lotes_vencidos%ROWTYPE;
BEGIN
    OPEN c_lotes_vencidos;
    LOOP
        FETCH c_lotes_vencidos INTO v_fila;
        EXIT WHEN c_lotes_vencidos%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Lote #' || v_fila.id_lote ||
                             ' | Producto: '  || v_fila.producto ||
                             ' | Venció: '    || v_fila.fecha_vencimiento);
    END LOOP;
    CLOSE c_lotes_vencidos;
END;
/

-- Cursor 14: Resumen de facturación por usuario vendedor
DECLARE
    CURSOR c_ventas_usuario IS
        SELECT u.id_usuario, u.nombre AS vendedor,
               COUNT(o.id_orden) AS ordenes_gestionadas,
               NVL(SUM(d.cantidad * i.precio_unitario), 0) AS total_vendido
        FROM usuario u
        LEFT JOIN orden         o ON u.id_usuario = o.id_usuario
        LEFT JOIN detalle_orden d ON o.id_orden   = d.id_orden
        LEFT JOIN item          i ON d.id_item    = i.id_item
        GROUP BY u.id_usuario, u.nombre
        ORDER BY total_vendido DESC;

    v_fila c_ventas_usuario%ROWTYPE;
BEGIN
    OPEN c_ventas_usuario;
    LOOP
        FETCH c_ventas_usuario INTO v_fila;
        EXIT WHEN c_ventas_usuario%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Vendedor: '  || v_fila.vendedor ||
                             ' | Órdenes: ' || v_fila.ordenes_gestionadas ||
                             ' | Total: ₡'  || v_fila.total_vendido);
    END LOOP;
    CLOSE c_ventas_usuario;
END;
/

-- Cursor 15: Buscar órdenes de un rango de fechas (parametrizado)
DECLARE
    CURSOR c_ordenes_rango(p_inicio TIMESTAMP, p_fin TIMESTAMP) IS
        SELECT o.id_orden, c.nombre AS cliente,
               o.fecha, e.nombre AS estado
        FROM orden o
        JOIN cliente c ON o.id_cliente = o.id_cliente
        JOIN estado  e ON o.id_estado  = e.id_estado
        WHERE o.fecha BETWEEN p_inicio AND p_fin
        ORDER BY o.fecha;

    v_fila c_ordenes_rango%ROWTYPE;
BEGIN
    OPEN c_ordenes_rango(
        SYSTIMESTAMP - INTERVAL '30' DAY,
        SYSTIMESTAMP
    );
    LOOP
        FETCH c_ordenes_rango INTO v_fila;
        EXIT WHEN c_ordenes_rango%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Orden #' || v_fila.id_orden  ||
                             ' | ' || v_fila.cliente ||
                             ' | ' || v_fila.estado);
    END LOOP;
    CLOSE c_ordenes_rango;
END;
/

-- ============================================================
-- SECCIÓN 13: TRIGGERS
-- ============================================================
------------------------Cambios para Triggers-----------------------------------

DROP TABLE auditoria CASCADE CONSTRAINTS;

CREATE TABLE auditoria (
    id_auditoria    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tabla_afectada  VARCHAR2(100),
    operacion       VARCHAR2(10),
    id_registro     VARCHAR2(100),
    valor_anterior  CLOB,
    valor_nuevo     CLOB,
    usuario_bd      VARCHAR2(100),
    fecha           TIMESTAMP
);
-------------------Triggers-----------------------------------------------------

CREATE OR REPLACE TRIGGER trg_aud_cliente
AFTER INSERT OR UPDATE OR DELETE ON cliente
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;

    v_operacion VARCHAR2(10);
    v_anterior  CLOB;
    v_nuevo     CLOB;
BEGIN
    IF INSERTING THEN
        v_operacion := 'INSERT';

        v_anterior := NULL;
        v_nuevo := 'nombre=' || :NEW.nombre ||
                   ', identificacion=' || :NEW.identificacion ||
                   ', telefono=' || :NEW.telefono ||
                   ', correo=' || :NEW.correo ||
                   ', estado=' || :NEW.id_estado;

        INSERT INTO auditoria VALUES (
            DEFAULT, 'CLIENTE', v_operacion, :NEW.id_cliente,
            v_anterior, v_nuevo, USER, SYSTIMESTAMP
        );

    ELSIF UPDATING THEN
        v_operacion := 'UPDATE';

        v_anterior := 'nombre=' || :OLD.nombre ||
                      ', identificacion=' || :OLD.identificacion ||
                      ', telefono=' || :OLD.telefono ||
                      ', correo=' || :OLD.correo ||
                      ', estado=' || :OLD.id_estado;

        v_nuevo := 'nombre=' || :NEW.nombre ||
                   ', identificacion=' || :NEW.identificacion ||
                   ', telefono=' || :NEW.telefono ||
                   ', correo=' || :NEW.correo ||
                   ', estado=' || :NEW.id_estado;

        INSERT INTO auditoria VALUES (
            DEFAULT, 'CLIENTE', v_operacion, :NEW.id_cliente,
            v_anterior, v_nuevo, USER, SYSTIMESTAMP
        );

    ELSIF DELETING THEN
        v_operacion := 'DELETE';

        v_anterior := 'nombre=' || :OLD.nombre ||
                      ', identificacion=' || :OLD.identificacion ||
                      ', telefono=' || :OLD.telefono ||
                      ', correo=' || :OLD.correo ||
                      ', estado=' || :OLD.id_estado;

        INSERT INTO auditoria VALUES (
            DEFAULT, 'CLIENTE', v_operacion, :OLD.id_cliente,
            v_anterior, NULL, USER, SYSTIMESTAMP
        );
    END IF;

    COMMIT;
END;
/

CREATE OR REPLACE TRIGGER trg_aud_usuario
AFTER INSERT OR UPDATE OR DELETE ON usuario
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    IF INSERTING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'USUARIO', 'INSERT', :NEW.id_usuario,
            NULL,
            'nombre=' || :NEW.nombre || ', correo=' || :NEW.correo,
            USER, SYSTIMESTAMP
        );

    ELSIF UPDATING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'USUARIO', 'UPDATE', :NEW.id_usuario,
            'nombre=' || :OLD.nombre || ', correo=' || :OLD.correo,
            'nombre=' || :NEW.nombre || ', correo=' || :NEW.correo,
            USER, SYSTIMESTAMP
        );

    ELSIF DELETING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'USUARIO', 'DELETE', :OLD.id_usuario,
            'nombre=' || :OLD.nombre || ', correo=' || :OLD.correo,
            NULL,
            USER, SYSTIMESTAMP
        );
    END IF;

    COMMIT;
END;
/

CREATE OR REPLACE TRIGGER trg_aud_item
AFTER INSERT OR UPDATE OR DELETE ON item
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    IF INSERTING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'ITEM', 'INSERT', :NEW.id_item,
            NULL,
            'nombre=' || :NEW.nombre || ', precio=' || :NEW.precio_unitario,
            USER, SYSTIMESTAMP
        );

    ELSIF UPDATING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'ITEM', 'UPDATE', :NEW.id_item,
            'nombre=' || :OLD.nombre || ', precio=' || :OLD.precio_unitario,
            'nombre=' || :NEW.nombre || ', precio=' || :NEW.precio_unitario,
            USER, SYSTIMESTAMP
        );

    ELSIF DELETING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'ITEM', 'DELETE', :OLD.id_item,
            'nombre=' || :OLD.nombre,
            NULL,
            USER, SYSTIMESTAMP
        );
    END IF;

    COMMIT;
END;
/

CREATE OR REPLACE TRIGGER trg_aud_proveedor
AFTER INSERT OR UPDATE OR DELETE ON proveedor
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    IF INSERTING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'PROVEEDOR', 'INSERT', :NEW.id_proveedor,
            NULL,
            'nombre=' || :NEW.nombre,
            USER, SYSTIMESTAMP
        );

    ELSIF UPDATING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'PROVEEDOR', 'UPDATE', :NEW.id_proveedor,
            'nombre=' || :OLD.nombre,
            'nombre=' || :NEW.nombre,
            USER, SYSTIMESTAMP
        );

    ELSIF DELETING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'PROVEEDOR', 'DELETE', :OLD.id_proveedor,
            'nombre=' || :OLD.nombre,
            NULL,
            USER, SYSTIMESTAMP
        );
    END IF;

    COMMIT;
END;
/

CREATE OR REPLACE TRIGGER trg_aud_orden
AFTER INSERT OR UPDATE OR DELETE ON orden
FOR EACH ROW
DECLARE
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    IF INSERTING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'ORDEN', 'INSERT', :NEW.id_orden,
            NULL,
            'cliente=' || :NEW.id_cliente || ', usuario=' || :NEW.id_usuario,
            USER, SYSTIMESTAMP
        );

    ELSIF UPDATING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'ORDEN', 'UPDATE', :NEW.id_orden,
            'estado=' || :OLD.id_estado,
            'estado=' || :NEW.id_estado,
            USER, SYSTIMESTAMP
        );

    ELSIF DELETING THEN
        INSERT INTO auditoria VALUES (
            DEFAULT, 'ORDEN', 'DELETE', :OLD.id_orden,
            'cliente=' || :OLD.id_cliente,
            NULL,
            USER, SYSTIMESTAMP
        );
    END IF;

    COMMIT;
END;
/



----------------Prueba Trigger-------------------------------------------------

UPDATE cliente
SET telefono = '9999-9999'
WHERE id_cliente = 1;

SELECT * FROM auditoria ORDER BY id_auditoria DESC;
