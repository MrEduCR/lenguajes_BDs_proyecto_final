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

-- Vista 5: Receta de cada producto (osea materias primas que lo componen)
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


-- ============================================================
-- SECCIÓN 2: PAQUETE PRINCIPAL
-- ============================================================

CREATE OR REPLACE PACKAGE pkg_fabrica AS

    -- F1: Calcula el stock total de un item
    FUNCTION fn_stock_item(p_id_item IN INTEGER) RETURN NUMBER;

    -- F2: Calcula el total de una orden (osea suma de subtotales)
    FUNCTION fn_total_orden(p_id_orden IN INTEGER) RETURN NUMBER;

    -- F3: Devuelve el nombre del estado dado su id
    FUNCTION fn_nombre_estado(p_id_estado IN INTEGER) RETURN VARCHAR2;

    -- F4: Cuenta ordenes activas de un cliente
    FUNCTION fn_ordenes_cliente(p_id_cliente IN INTEGER) RETURN NUMBER;

    -- F5: Verifica si hay stock suficiente para una cantidad pedida
    FUNCTION fn_hay_stock(p_id_item IN INTEGER, p_cantidad IN NUMBER) RETURN BOOLEAN;

    -- F6: Calcula el costo estimado de fabricar un item (osea suma materia prima)
    FUNCTION fn_costo_fabricacion(p_id_item IN INTEGER) RETURN NUMBER;

    -- F7: Devuelve el precio unitario de un item
    FUNCTION fn_precio_item(p_id_item IN INTEGER) RETURN NUMBER;

    -- F8: Cuenta proveedores activos
    FUNCTION fn_total_proveedores_activos RETURN NUMBER;

    -- -------------------------------------------------------
    -- PROCEDIMIENTOS (13)
    -- -------------------------------------------------------

-- CRUD ESTADO
    PROCEDURE sp_insertar_estado(p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2);
    PROCEDURE sp_actualizar_estado(p_id_estado IN INTEGER, p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2);

    -- CRUD CLIENTE
    PROCEDURE sp_insertar_cliente(p_nombre IN VARCHAR2, p_identificacion IN VARCHAR2, p_telefono IN VARCHAR2, p_correo IN VARCHAR2, p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);
    PROCEDURE sp_actualizar_cliente(p_id_cliente IN INTEGER, p_nombre IN VARCHAR2, p_telefono IN VARCHAR2, p_correo IN VARCHAR2);
    PROCEDURE sp_eliminar_cliente(p_id_cliente IN INTEGER);

    -- CRUD ITEM
    PROCEDURE sp_insertar_item(p_nombre IN VARCHAR2, p_descripcion IN CLOB, p_unidad IN VARCHAR2, p_precio IN NUMBER, p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);
    PROCEDURE sp_actualizar_item(p_id_item IN INTEGER, p_nombre IN VARCHAR2, p_precio IN NUMBER);
    PROCEDURE sp_eliminar_item(p_id_item IN INTEGER);

    -- CRUD PROVEEDOR
    PROCEDURE sp_insertar_proveedor(p_nombre IN VARCHAR2, p_contacto IN VARCHAR2, p_telefono IN VARCHAR2, p_correo IN VARCHAR2, p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER);

    -- CRUD ORDEN
    PROCEDURE sp_crear_orden(p_id_cliente IN INTEGER, p_id_usuario IN INTEGER, p_id_estado IN INTEGER, p_id_nueva OUT INTEGER);
    PROCEDURE sp_agregar_detalle_orden(p_id_orden IN INTEGER, p_id_item IN INTEGER, p_cantidad IN NUMBER);
    PROCEDURE sp_cancelar_orden(p_id_orden IN INTEGER);

    -- INVENTARIO
    PROCEDURE sp_ingresar_lote(p_id_item IN INTEGER, p_cantidad IN NUMBER, p_fecha_venc IN TIMESTAMP);

    -- REPORTE con cursor
    PROCEDURE sp_reporte_ordenes_cliente(p_id_cliente IN INTEGER, p_cursor OUT SYS_REFCURSOR);

    END pkg_fabrica;
/


-- ============================================================
-- SECCIÓN 3: PAQUETE PRINCIPAL - BODY
-- ============================================================

CREATE OR REPLACE PACKAGE BODY pkg_fabrica AS

    -- -------------------------------------------------------
    -- FUNCIONES
    -- -------------------------------------------------------

    -- F1: Stock total de un item
    FUNCTION fn_stock_item(p_id_item IN INTEGER) RETURN NUMBER IS
        v_stock NUMBER;
    BEGIN
        SELECT NVL(SUM(cantidad), 0)
        INTO v_stock
        FROM inventario_de_items
        WHERE id_item = p_id_item;
        RETURN v_stock;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20001, 'Error calculando stock del item: ' || p_id_item);
    END fn_stock_item;

    -- F2: Total de una orden
    FUNCTION fn_total_orden(p_id_orden IN INTEGER) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT NVL(SUM(d.cantidad * i.precio_unitario), 0)
        INTO v_total
        FROM detalle_orden d
        JOIN item i ON d.id_item = i.id_item
        WHERE d.id_orden = p_id_orden;
        RETURN v_total;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0;
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20002, 'Error calculando total de orden: ' || p_id_orden);
    END fn_total_orden;

    -- F3: Nombre del estado
    FUNCTION fn_nombre_estado(p_id_estado IN INTEGER) RETURN VARCHAR2 IS
        v_nombre estado.nombre%TYPE;
    BEGIN
        SELECT nombre
        INTO v_nombre
        FROM estado
        WHERE id_estado = p_id_estado;
        RETURN v_nombre;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 'DESCONOCIDO';
    END fn_nombre_estado;

    -- F4: Órdenes activas de un cliente
    FUNCTION fn_ordenes_cliente(p_id_cliente IN INTEGER) RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_total
        FROM orden
        WHERE id_cliente = p_id_cliente;
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20003, 'Error contando órdenes del cliente: ' || p_id_cliente);
    END fn_ordenes_cliente;

    -- F5: Verificar si hay stock suficiente
    FUNCTION fn_hay_stock(p_id_item IN INTEGER, p_cantidad IN NUMBER) RETURN BOOLEAN IS
        v_stock NUMBER;
    BEGIN
        v_stock := fn_stock_item(p_id_item);
        RETURN v_stock >= p_cantidad;
    END fn_hay_stock;

    -- F6: Costo estimado de fabricación de un item
    FUNCTION fn_costo_fabricacion(p_id_item IN INTEGER) RETURN NUMBER IS
        v_costo NUMBER := 0;
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
            v_parcial := v_fila.medida_materia_prima * v_fila.precio_referencia;
            v_costo   := v_costo + v_parcial;
        END LOOP;
        CLOSE c_receta;
        RETURN v_costo;
    EXCEPTION
        WHEN OTHERS THEN
            IF c_receta%ISOPEN THEN CLOSE c_receta; END IF;
            RAISE_APPLICATION_ERROR(-20004, 'Error calculando costo de fabricación: ' || p_id_item);
    END fn_costo_fabricacion;

    -- F7: Precio unitario de un item
    FUNCTION fn_precio_item(p_id_item IN INTEGER) RETURN NUMBER IS
        v_precio item.precio_unitario%TYPE;
    BEGIN
        SELECT precio_unitario
        INTO v_precio
        FROM item
        WHERE id_item = p_id_item;
        RETURN v_precio;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20005, 'Item no encontrado: ' || p_id_item);
    END fn_precio_item;

    -- F8: Total de proveedores activos (id_estado = 1 = activo)
    FUNCTION fn_total_proveedores_activos RETURN NUMBER IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_total
        FROM proveedor p
        JOIN estado e ON p.id_estado = e.id_estado
        WHERE UPPER(e.nombre) = 'ACTIVO';
        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20006, 'Error contando proveedores activos.');
    END fn_total_proveedores_activos;


    -- -------------------------------------------------------
    -- PROCEDIMIENTOS
    -- -------------------------------------------------------

    -- SP1: Insertar estado
    PROCEDURE sp_insertar_estado(p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2) IS
        v_existe NUMBER;
        ex_ya_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM estado
        WHERE UPPER(nombre) = UPPER(p_nombre);

        IF v_existe > 0 THEN
            RAISE ex_ya_existe;
        END IF;

        INSERT INTO estado(nombre, descripcion)
        VALUES (p_nombre, p_descripcion);
        COMMIT;
    EXCEPTION
        WHEN ex_ya_existe THEN
            RAISE_APPLICATION_ERROR(-20010, 'El estado ya existe: ' || p_nombre);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20011, 'Error insertando estado: ' || SQLERRM);
    END sp_insertar_estado;

    -- SP2: Actualizar estado
    PROCEDURE sp_actualizar_estado(p_id_estado IN INTEGER, p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM estado
        WHERE id_estado = p_id_estado;
        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        UPDATE estado
        SET nombre = p_nombre,
            descripcion = p_descripcion
        WHERE id_estado = p_id_estado;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20012, 'Estado no encontrado: ' || p_id_estado);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20013, 'Error actualizando estado: ' || SQLERRM);
    END sp_actualizar_estado;

    -- SP3: Insertar cliente
    PROCEDURE sp_insertar_cliente(p_nombre IN VARCHAR2, p_identificacion IN VARCHAR2, p_telefono IN VARCHAR2, p_correo IN VARCHAR2, p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
        v_existe NUMBER;
        ex_identificacion_duplicada EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM cliente
        WHERE identificacion = p_identificacion;
        IF v_existe > 0 THEN
            RAISE ex_identificacion_duplicada;
        END IF;

        INSERT INTO cliente(nombre, identificacion, telefono, correo, id_estado)
        VALUES (p_nombre, p_identificacion, p_telefono, p_correo, p_id_estado)
        RETURNING id_cliente INTO p_id_nuevo;
        COMMIT;
    EXCEPTION
        WHEN ex_identificacion_duplicada THEN
            RAISE_APPLICATION_ERROR(-20020, 'Cliente ya registrado con identificacion: ' || p_identificacion);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20021, 'Error insertando cliente: ' || SQLERRM);
    END sp_insertar_cliente;

    -- SP4: Actualizar cliente
    PROCEDURE sp_actualizar_cliente(p_id_cliente IN INTEGER, p_nombre IN VARCHAR2,  p_telefono IN VARCHAR2, p_correo IN VARCHAR2) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM cliente
        WHERE id_cliente = p_id_cliente;
        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        UPDATE cliente
        SET nombre   = p_nombre,
            telefono = p_telefono,
            correo   = p_correo
        WHERE id_cliente = p_id_cliente;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20022, 'Cliente no encontrado: ' || p_id_cliente);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20023, 'Error actualizando cliente: ' || SQLERRM);
    END sp_actualizar_cliente;

    -- SP5: Eliminar cliente (cambio de estado lógico)
    PROCEDURE sp_eliminar_cliente(p_id_cliente IN INTEGER) IS
        v_existe   NUMBER;
        v_id_inactivo estado.id_estado%TYPE;
        ex_no_existe   EXCEPTION;
        ex_sin_estado  EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM cliente
        WHERE id_cliente = p_id_cliente;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        SELECT id_estado
        INTO v_id_inactivo
        FROM estado
        WHERE UPPER(nombre) = 'INACTIVO'
        AND ROWNUM = 1;

        UPDATE cliente
        SET id_estado = v_id_inactivo
        WHERE id_cliente = p_id_cliente;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20024, 'Cliente no encontrado: ' || p_id_cliente);
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20025, 'Estado inactivo no encontrado en tabla estado.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20026, 'Error eliminando cliente: ' || SQLERRM);
    END sp_eliminar_cliente;

    -- SP6: Insertar item (producto terminado)
    PROCEDURE sp_insertar_item(p_nombre IN VARCHAR2, p_descripcion IN CLOB,p_unidad IN VARCHAR2, p_precio IN NUMBER,p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
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

    -- SP7: Actualizar item
    PROCEDURE sp_actualizar_item(p_id_item IN INTEGER, p_nombre IN VARCHAR2,p_precio IN NUMBER) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM item
        WHERE id_item = p_id_item;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        UPDATE item
        SET nombre          = p_nombre,
            precio_unitario = p_precio
        WHERE id_item = p_id_item;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20032, 'Item no encontrado: ' || p_id_item);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20033, 'Error actualizando item: ' || SQLERRM);
    END sp_actualizar_item;

    -- SP8: Eliminar item (lógico)
    PROCEDURE sp_eliminar_item(p_id_item IN INTEGER) IS
        v_id_inactivo estado.id_estado%TYPE;
    BEGIN
        SELECT id_estado
        INTO v_id_inactivo
        FROM estado
        WHERE UPPER(nombre) = 'INACTIVO'
        AND ROWNUM = 1;
        UPDATE item
        SET id_estado = v_id_inactivo
        WHERE id_item = p_id_item;
        COMMIT;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20034, 'Item no encontrado o estado INACTIVO inexistente.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20035, 'Error eliminando item: ' || SQLERRM);
    END sp_eliminar_item;

    -- SP9: Insertar proveedor
    PROCEDURE sp_insertar_proveedor(p_nombre IN VARCHAR2, p_contacto IN VARCHAR2, p_telefono IN VARCHAR2, p_correo IN VARCHAR2, p_id_estado IN INTEGER, p_id_nuevo OUT INTEGER) IS
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

    -- SP10: Crear orden
    PROCEDURE sp_crear_orden(p_id_cliente IN INTEGER, p_id_usuario IN INTEGER,
                              p_id_estado IN INTEGER, p_id_nueva OUT INTEGER) IS
        v_existe_cliente NUMBER;
        ex_cliente_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe_cliente
        FROM cliente
        WHERE id_cliente = p_id_cliente;

        IF v_existe_cliente = 0 THEN
            RAISE ex_cliente_no_existe;
        END IF;

        INSERT INTO orden(fecha, id_cliente, id_usuario, id_estado)
        VALUES (SYSTIMESTAMP, p_id_cliente, p_id_usuario, p_id_estado)
        RETURNING id_orden INTO p_id_nueva;
        COMMIT;
    EXCEPTION
        WHEN ex_cliente_no_existe THEN
            RAISE_APPLICATION_ERROR(-20050, 'Cliente no encontrado: ' || p_id_cliente);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20051, 'Error creando orden: ' || SQLERRM);
    END sp_crear_orden;

    -- SP11: Agregar linea de detalle a una orden
    PROCEDURE sp_agregar_detalle_orden(p_id_orden IN INTEGER, p_id_item IN INTEGER,
                                        p_cantidad IN NUMBER) IS
        ex_sin_stock EXCEPTION;
    BEGIN
        IF NOT fn_hay_stock(p_id_item, p_cantidad) THEN
            RAISE ex_sin_stock;
        END IF;

        INSERT INTO detalle_orden(id_orden, id_item, cantidad)
        VALUES (p_id_orden, p_id_item, p_cantidad);
        COMMIT;
    EXCEPTION
        WHEN ex_sin_stock THEN
            RAISE_APPLICATION_ERROR(-20052, 'Stock insuficiente para el item: ' || p_id_item);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20053, 'Error agregando detalle de orden: ' || SQLERRM);
    END sp_agregar_detalle_orden;

    -- SP12: Cancelar orden (cambio de estado)
    PROCEDURE sp_cancelar_orden(p_id_orden IN INTEGER) IS
        v_id_cancelado estado.id_estado%TYPE;
        v_existe       NUMBER;
        ex_no_existe   EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM orden
        WHERE id_orden = p_id_orden;

        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        SELECT id_estado
        INTO v_id_cancelado
        FROM estado
        WHERE UPPER(nombre) = 'CANCELADO'
        AND ROWNUM = 1;

        UPDATE orden
        SET id_estado = v_id_cancelado
        WHERE id_orden = p_id_orden;
        COMMIT;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20054, 'Orden no encontrada: ' || p_id_orden);
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20055, 'Estado CANCELADO no existe en tabla estado.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20056, 'Error cancelando orden: ' || SQLERRM);
    END sp_cancelar_orden;

    -- SP13: Ingresar lote al inventario
    PROCEDURE sp_ingresar_lote(p_id_item IN INTEGER, p_cantidad IN NUMBER,
                                p_fecha_venc IN TIMESTAMP) IS
        v_existe NUMBER;
        ex_item_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM item
        WHERE id_item = p_id_item;

        IF v_existe = 0 THEN
            RAISE ex_item_no_existe;
        END IF;

        IF p_cantidad <= 0 THEN
            RAISE_APPLICATION_ERROR(-20060, 'La cantidad del lote debe ser mayor a cero.');
        END IF;

        INSERT INTO inventario_de_items(id_item, cantidad, fecha_ingreso, fecha_vencimiento)
        VALUES (p_id_item, p_cantidad, SYSTIMESTAMP, p_fecha_venc);
        COMMIT;
    EXCEPTION
        WHEN ex_item_no_existe THEN
            RAISE_APPLICATION_ERROR(-20061, 'Item no encontrado: ' || p_id_item);
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20062, 'Error ingresando lote: ' || SQLERRM);
    END sp_ingresar_lote;

    -- SP con SYS_REFCURSOR: Reporte de órdenes de un cliente
    PROCEDURE sp_reporte_ordenes_cliente(p_id_cliente IN INTEGER,p_cursor OUT SYS_REFCURSOR) IS
        v_existe NUMBER;
        ex_no_existe EXCEPTION;
    BEGIN
        SELECT COUNT(*)
        INTO v_existe
        FROM cliente
        WHERE id_cliente = p_id_cliente;
        IF v_existe = 0 THEN
            RAISE ex_no_existe;
        END IF;

        OPEN p_cursor FOR
            SELECT o.id_orden,
                   o.fecha,
                   e.nombre          AS estado,
                   pkg_fabrica.fn_total_orden(o.id_orden) AS total
            FROM orden o
            JOIN estado e ON o.id_estado = e.id_estado
            WHERE o.id_cliente = p_id_cliente
            ORDER BY o.fecha DESC;
    EXCEPTION
        WHEN ex_no_existe THEN
            RAISE_APPLICATION_ERROR(-20070, 'xliente no encontrado: ' || p_id_cliente);
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20071, 'Error generando reporte: ' || SQLERRM);
    END sp_reporte_ordenes_cliente;

END pkg_fabrica;
/


-- ============================================================
-- SECCIÓN 4: CURSORES
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
