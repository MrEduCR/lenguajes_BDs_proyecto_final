-- ============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - AVANCE #2 
-- ============================================

CREATE TABLESPACE ts_proyecto
  DATAFILE 'ts_proyecto.dbf' SIZE 100M AUTOEXTEND ON NEXT 10M MAXSIZE 1G;

CREATE ROLE rol_proyecto_admin;
GRANT DBA TO rol_proyecto_admin;

CREATE USER proyecto_user IDENTIFIED BY "AdminTesting+123"
  DEFAULT TABLESPACE ts_proyecto
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON ts_proyecto;

GRANT rol_proyecto_admin TO proyecto_user;
GRANT CREATE SESSION TO proyecto_user;


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


-- Drops
DROP TABLE detalle_orden CASCADE CONSTRAINTS;
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