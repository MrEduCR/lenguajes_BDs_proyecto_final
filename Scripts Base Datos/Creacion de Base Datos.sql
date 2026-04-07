-- ============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - AVANCE #2 
-- ============================================

-- ============================================
-- CREACION PARA TRABAJAR CON ORACLE LOCAL
-- ============================================
/*CREATE TABLESPACE ts_proyecto
  DATAFILE 'ts_proyecto.dbf' SIZE 100M AUTOEXTEND ON NEXT 10M MAXSIZE 1G;

CREATE ROLE rol_proyecto_admin;
GRANT DBA TO rol_proyecto_admin;

CREATE USER proyecto_user IDENTIFIED BY "AdminTesting+123"
  DEFAULT TABLESPACE ts_proyecto
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON ts_proyecto;

GRANT rol_proyecto_admin TO proyecto_user;
GRANT CREATE SESSION TO proyecto_user;*/

-- ============================================
-- CREACION PARA TRABAJAR CON ORACLE CLOUD
-- ============================================
CREATE USER proyecto_user IDENTIFIED BY "AdminTesting+123";

-- Permisos básicos
GRANT CREATE SESSION TO proyecto_user;

-- Objetos
GRANT CREATE TABLE TO proyecto_user;
GRANT CREATE VIEW TO proyecto_user;
GRANT CREATE MATERIALIZED VIEW TO proyecto_user;
GRANT CREATE TRIGGER TO proyecto_user;
GRANT CREATE PROCEDURE TO proyecto_user;
GRANT CREATE SEQUENCE TO proyecto_user;

-- Espacio
GRANT UNLIMITED TABLESPACE TO proyecto_user;

-- ============================================
-- CREACION DE TABLAS
-- ============================================
CREATE TABLE estado (
    id_estado   INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR2(255),
    descripcion VARCHAR2(255)
);

CREATE TABLE rol (
    id_rol      INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR2(255),
    descripcion VARCHAR2(255),
    id_estado   INTEGER,
    CONSTRAINT fk_rol_estado FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);


CREATE TABLE usuario (
    id_usuario  INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR2(255),
    correo      VARCHAR2(255),
    contrasena  VARCHAR2(255),
    id_rol      INTEGER,
    id_estado   INTEGER,
    CONSTRAINT fk_usuario_rol    FOREIGN KEY (id_rol)    REFERENCES rol(id_rol),
    CONSTRAINT fk_usuario_estado FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);


CREATE TABLE cliente (
    id_cliente      INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR2(255),
    identificacion  VARCHAR2(255),
    telefono        VARCHAR2(50),
    correo          VARCHAR2(255),
    id_estado       INTEGER,
    CONSTRAINT fk_cliente_estado FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);

CREATE TABLE proveedor (
    id_proveedor    INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR2(255),
    contacto        VARCHAR2(255),
    telefono        VARCHAR2(50),
    correo          VARCHAR2(255),
    id_estado       INTEGER,
    CONSTRAINT fk_proveedor_estado FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);

CREATE TABLE item (
    id_item         INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre          VARCHAR2(255),
    descripcion     CLOB,
    unidad_medida   VARCHAR2(100),
    precio_unitario NUMBER(18,4),
    id_estado       INTEGER,
    CONSTRAINT fk_item_estado FOREIGN KEY (id_estado) REFERENCES estado(id_estado)
);

CREATE TABLE materia_prima (
    id_materia_prima     INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_proveedor         INTEGER,
    precio_referencia    NUMBER(18,4),
    nombre_materia_prima VARCHAR2(255),
    CONSTRAINT fk_materia_prima_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
);

CREATE TABLE pre_producto_item (
    id_pre_producto      INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_item              INTEGER,
    id_materia_prima     INTEGER,
    medida_materia_prima NUMBER(18,4),
    unidad_medida        VARCHAR2(100),
    CONSTRAINT fk_preproducto_item    FOREIGN KEY (id_item)          REFERENCES item(id_item),
    CONSTRAINT fk_preproducto_materia FOREIGN KEY (id_materia_prima) REFERENCES materia_prima(id_materia_prima)
);

CREATE TABLE inventario_de_items (
    id_lote           INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_item           INTEGER,
    cantidad          NUMBER(18,4),
    fecha_ingreso     TIMESTAMP,
    fecha_vencimiento TIMESTAMP,
    CONSTRAINT fk_inventario_item FOREIGN KEY (id_item) REFERENCES item(id_item)
);

CREATE TABLE orden (
    id_orden    INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fecha       TIMESTAMP,
    id_cliente  INTEGER,
    id_usuario  INTEGER,
    id_estado   INTEGER,
    CONSTRAINT fk_orden_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_orden_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_orden_estado  FOREIGN KEY (id_estado)  REFERENCES estado(id_estado)
);

CREATE TABLE detalle_orden (
    id_detalle  INTEGER         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_orden    INTEGER,
    id_item     INTEGER,
    cantidad    NUMBER(18,4),
    CONSTRAINT fk_detalle_orden FOREIGN KEY (id_orden) REFERENCES orden(id_orden),
    CONSTRAINT fk_detalle_item  FOREIGN KEY (id_item)  REFERENCES item(id_item)
);

CREATE TABLE auditoria (
    id_auditoria    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tabla_afectada  VARCHAR2(100),  
    operacion       VARCHAR2(10),         
    valor_anterior  CLOB,           
    valor_nuevo     CLOB,          
    usuario_bd      VARCHAR2(100),  
    fecha           TIMESTAMP     
);
-- ============================================
-- ELIMINACION DE TABLAS
-- ============================================
/*DROP TABLE detalle_orden CASCADE CONSTRAINTS;
DROP TABLE orden CASCADE CONSTRAINTS;
DROP TABLE inventario_de_items CASCADE CONSTRAINTS;
DROP TABLE pre_producto_item CASCADE CONSTRAINTS;
DROP TABLE materia_prima CASCADE CONSTRAINTS;
DROP TABLE item CASCADE CONSTRAINTS;
DROP TABLE proveedor CASCADE CONSTRAINTS;
DROP TABLE cliente CASCADE CONSTRAINTS;
DROP TABLE usuario CASCADE CONSTRAINTS;
DROP TABLE rol CASCADE CONSTRAINTS;
DROP TABLE estado CASCADE CONSTRAINTS;
DROP TABLE estado CASCADE CONSTRAINTS;*/


-- ============================================
-- INSERTS DE PRUEBA
-- ============================================
-- TABLA: estado
INSERT INTO estado (nombre, descripcion) VALUES ('Activo', 'Registro operativo y disponible');
INSERT INTO estado (nombre, descripcion) VALUES ('Inactivo', 'Registro deshabilitado temporalmente');
INSERT INTO estado (nombre, descripcion) VALUES ('Pendiente', 'Registro en espera de aprobación');

-- TABLA: rol (Depende de estado)
INSERT INTO rol (nombre, descripcion, id_estado) VALUES ('Administrador', 'Acceso total al sistema', 1);
INSERT INTO rol (nombre, descripcion, id_estado) VALUES ('Vendedor', 'Acceso a ventas y clientes', 1);
INSERT INTO rol (nombre, descripcion, id_estado) VALUES ('Cajero', 'Acceso limitado a facturación', 1);

-- TABLA: proveedor (Depende de estado)
INSERT INTO proveedor (nombre, contacto, telefono, correo, id_estado) VALUES ('Distribuidora Global', 'Juan Pérez', '555-0101', 'ventas@pali.com', 1);
INSERT INTO proveedor (nombre, contacto, telefono, correo, id_estado) VALUES ('Suministros Industriales', 'Ana López', '555-0202', 'info@sumindustria.com', 1);
INSERT INTO proveedor (nombre, contacto, telefono, correo, id_estado) VALUES ('Materia Prima Express', 'Carlos Ruiz', '555-0303', 'contacto@mpx.com', 2);

-- TABLA: usuario (Depende de rol y estado)
INSERT INTO usuario (nombre, correo, contrasena, id_rol, id_estado) VALUES ('Admin Principal', 'admin@suproli.com', 'hash_secure_123', 1, 1);
INSERT INTO usuario (nombre, correo, contrasena, id_rol, id_estado) VALUES ('Pedro Ventas', 'pedro@suproli.com', 'hash_sales_456', 2, 1);
INSERT INTO usuario (nombre, correo, contrasena, id_rol, id_estado) VALUES ('Maria Cajera', 'maria@suproli.com', 'hash_cash_789', 3, 1);

-- TABLA: cliente (Depende de estado)
INSERT INTO cliente (nombre, identificacion, telefono, correo, id_estado) VALUES ('Juan Cliente', '1-1111-1111', '8888-1111', 'juan@gmail.com', 1);
INSERT INTO cliente (nombre, identificacion, telefono, correo, id_estado) VALUES ('Empresa ABC', '3-101-222222', '4000-2222', 'compras@abc.com', 1);
INSERT INTO cliente (nombre, identificacion, telefono, correo, id_estado) VALUES ('Laura Rodríguez', '2-2222-2222', '7777-3333', 'laura@hotmail.com', 1);

-- TABLA: item (Depende de estado)
INSERT INTO item (nombre, descripcion, unidad_medida, precio_unitario, id_estado) VALUES ('Camiseta Algodón', 'Camiseta básica talla M', 'Unidad', 15.50, 1);
INSERT INTO item (nombre, descripcion, unidad_medida, precio_unitario, id_estado) VALUES ('Pantalón Jean', 'Pantalón azul mezclilla', 'Unidad', 45.00, 1);
INSERT INTO item (nombre, descripcion, unidad_medida, precio_unitario, id_estado) VALUES ('Gorra Deportiva', 'Gorra ajustable color negro', 'Unidad', 12.00, 1);

-- TABLA: materia_prima (Depende de proveedor)
INSERT INTO materia_prima (id_proveedor, precio_referencia, nombre_materia_prima) VALUES (1, 5.25, 'Tela Algodón');
INSERT INTO materia_prima (id_proveedor, precio_referencia, nombre_materia_prima) VALUES (2, 2.10, 'Hilo Poliéster');
INSERT INTO materia_prima (id_proveedor, precio_referencia, nombre_materia_prima) VALUES (1, 0.50, 'Botones Plástico');

-- TABLA: pre_producto_item (Relación Item - Materia Prima)
INSERT INTO pre_producto_item (id_item, id_materia_prima, medida_materia_prima, unidad_medida) VALUES (1, 1, 1.5, 'Metros');
INSERT INTO pre_producto_item (id_item, id_materia_prima, medida_materia_prima, unidad_medida) VALUES (1, 2, 10.0, 'Metros');
INSERT INTO pre_producto_item (id_item, id_materia_prima, medida_materia_prima, unidad_medida) VALUES (2, 1, 2.5, 'Metros');

-- TABLA: inventario_de_items (Depende de item)
INSERT INTO inventario_de_items (id_item, cantidad, fecha_ingreso, fecha_vencimiento) VALUES (1, 100, CURRENT_TIMESTAMP, TO_TIMESTAMP('2026-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO inventario_de_items (id_item, cantidad, fecha_ingreso, fecha_vencimiento) VALUES (2, 50, CURRENT_TIMESTAMP, TO_TIMESTAMP('2028-06-15 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO inventario_de_items (id_item, cantidad, fecha_ingreso, fecha_vencimiento) VALUES (3, 200, CURRENT_TIMESTAMP, NULL);

-- TABLA: orden (Depende de cliente, usuario y estado)
INSERT INTO orden (fecha, id_cliente, id_usuario, id_estado) VALUES (CURRENT_TIMESTAMP, 1, 2, 1);
INSERT INTO orden (fecha, id_cliente, id_usuario, id_estado) VALUES (CURRENT_TIMESTAMP, 2, 2, 1);
INSERT INTO orden (fecha, id_cliente, id_usuario, id_estado) VALUES (CURRENT_TIMESTAMP, 3, 3, 3); -- Pendiente

-- TABLA: detalle_orden (Depende de orden e item)
INSERT INTO detalle_orden (id_orden, id_item, cantidad) VALUES (1, 1, 2);
INSERT INTO detalle_orden (id_orden, id_item, cantidad) VALUES (1, 2, 1);
INSERT INTO detalle_orden (id_orden, id_item, cantidad) VALUES (2, 3, 10);

-- TABLA: auditoria (Independiente / Log)
INSERT INTO auditoria (tabla_afectada, operacion, id_registro, valor_anterior, valor_nuevo, usuario_bd, fecha) 
VALUES ('USUARIO', 'INSERT', NULL, 'Nuevo admin creado', USER, CURRENT_TIMESTAMP);
INSERT INTO auditoria (tabla_afectada, operacion, id_registro, valor_anterior, valor_nuevo, usuario_bd, fecha) 
VALUES ('ITEM', 'UPDATE', 'Precio: 15.00', 'Precio: 15.50', USER, CURRENT_TIMESTAMP);
INSERT INTO auditoria (tabla_afectada, operacion, id_registro, valor_anterior, valor_nuevo, usuario_bd, fecha) 
VALUES ('CLIENTE', 'DELETE', 'Cliente Antiguo', NULL, USER, CURRENT_TIMESTAMP);



-- ============================================
-- SELECTS DE PRUEBA
-- ============================================
-- 1. Tablas de Configuración
SELECT * FROM estado;
SELECT * FROM rol;
SELECT * FROM proveedor;

-- 2. Tablas de Personas
SELECT * FROM usuario;
SELECT * FROM cliente;

-- 3. Tablas de Catálogo y Fórmulas
SELECT * FROM item;
SELECT * FROM materia_prima;
SELECT * FROM pre_producto_item;

-- 4. Tablas de Movimientos e Inventario
SELECT * FROM inventario_de_items;
SELECT * FROM orden;
SELECT * FROM detalle_orden;

-- 5. Tabla de Registro de Cambios
SELECT * FROM auditoria;


