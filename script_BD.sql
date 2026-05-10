-- =====================================================================
-- Script de Base de Datos - Sumativa 3 (DSY2201)
-- Maria Ovalle
--
-- Microservicios: ms-ordenes (puerto 8080) y ms-citas (puerto 8081)
-- Base de datos: Oracle Autonomous Database (Always Free) - sumativa3db
-- Esquema: ADMIN
--
-- NOTA: En tiempo de ejecucion las tablas y secuencias son creadas
-- automaticamente por Hibernate (spring.jpa.hibernate.ddl-auto=update)
-- a partir de las anotaciones @Entity. Este script representa el DDL
-- equivalente para fines de documentacion y portabilidad.
-- =====================================================================

-- ---------------------------------------------------------------------
-- MICROSERVICIO 1: ms-ordenes (Gestion de Ordenes de Compra)
-- ---------------------------------------------------------------------

-- Secuencia para el ID de la orden
CREATE SEQUENCE ORDEN_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Tabla principal de ordenes
CREATE TABLE ORDENES_COMPRA (
    id              NUMBER(19,0)        NOT NULL,
    cliente         VARCHAR2(255 CHAR)  NOT NULL,
    mascota         VARCHAR2(255 CHAR)  NOT NULL,
    tipo_mascota    VARCHAR2(255 CHAR)  NOT NULL,
    total           NUMBER(19,2)        NOT NULL,
    estado          VARCHAR2(255 CHAR)  NOT NULL,
    fecha_creacion  TIMESTAMP(6),
    CONSTRAINT pk_ordenes_compra PRIMARY KEY (id),
    CONSTRAINT chk_estado_orden CHECK (
        estado IN ('PENDIENTE', 'EN_PROCESO', 'COMPLETADA', 'CANCELADA')
    )
);

-- Tabla hija para la coleccion de productos de cada orden (@ElementCollection)
CREATE TABLE ORDEN_PRODUCTOS (
    orden_id    NUMBER(19,0)        NOT NULL,
    producto    VARCHAR2(255 CHAR),
    CONSTRAINT fk_orden_productos
        FOREIGN KEY (orden_id) REFERENCES ORDENES_COMPRA(id)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- MICROSERVICIO 2: ms-citas (Reserva de Citas Medicas Veterinarias)
-- ---------------------------------------------------------------------

-- Secuencia para el ID de la cita
CREATE SEQUENCE CITA_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Tabla de citas
CREATE TABLE CITAS_MEDICAS (
    id              NUMBER(19,0)        NOT NULL,
    paciente        VARCHAR2(255 CHAR)  NOT NULL,
    mascota         VARCHAR2(255 CHAR)  NOT NULL,
    tipo_mascota    VARCHAR2(255 CHAR)  NOT NULL,
    veterinario     VARCHAR2(255 CHAR)  NOT NULL,
    fecha           DATE                NOT NULL,
    hora            VARCHAR2(255 CHAR)  NOT NULL,
    motivo          VARCHAR2(255 CHAR)  NOT NULL,
    estado          VARCHAR2(255 CHAR)  NOT NULL,
    fecha_registro  TIMESTAMP(6),
    CONSTRAINT pk_citas_medicas PRIMARY KEY (id),
    CONSTRAINT chk_estado_cita CHECK (
        estado IN ('PROGRAMADA', 'CANCELADA', 'COMPLETADA')
    )
);

-- =====================================================================
-- DATOS DE PRUEBA (5 registros por tabla, equivalentes al DataInitializer)
-- =====================================================================

-- ---------------------------------------------------------------------
-- ORDENES_COMPRA
-- ---------------------------------------------------------------------

INSERT INTO ORDENES_COMPRA (id, cliente, mascota, tipo_mascota, total, estado, fecha_creacion)
VALUES (ORDEN_SEQ.NEXTVAL, 'Juan Pérez', 'Firulais', 'Perro', 45000.00, 'PENDIENTE',
        TO_TIMESTAMP('2026-04-30 10:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (1, 'Alimento premium canino 5kg');
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (1, 'Collar antipulgas');

INSERT INTO ORDENES_COMPRA (id, cliente, mascota, tipo_mascota, total, estado, fecha_creacion)
VALUES (ORDEN_SEQ.NEXTVAL, 'María González', 'Michi', 'Gato', 32000.00, 'EN_PROCESO',
        TO_TIMESTAMP('2026-05-01 14:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (2, 'Arena sanitaria 10kg');
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (2, 'Juguete rascador');
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (2, 'Vitaminas felinas');

INSERT INTO ORDENES_COMPRA (id, cliente, mascota, tipo_mascota, total, estado, fecha_creacion)
VALUES (ORDEN_SEQ.NEXTVAL, 'Carlos López', 'Burbuja', 'Pez', 18500.00, 'COMPLETADA',
        TO_TIMESTAMP('2026-05-02 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (3, 'Alimento para peces tropicales');
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (3, 'Filtro de acuario');

INSERT INTO ORDENES_COMPRA (id, cliente, mascota, tipo_mascota, total, estado, fecha_creacion)
VALUES (ORDEN_SEQ.NEXTVAL, 'Sofía Herrera', 'Manchas', 'Gato', 27300.00, 'PENDIENTE',
        TO_TIMESTAMP('2026-05-03 17:20:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (4, 'Paté para gatos 12 unid');
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (4, 'Arenero cubierto');
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (4, 'Desparasitante oral');

INSERT INTO ORDENES_COMPRA (id, cliente, mascota, tipo_mascota, total, estado, fecha_creacion)
VALUES (ORDEN_SEQ.NEXTVAL, 'Rodrigo Fuentes', 'Thor', 'Perro', 34900.00, 'CANCELADA',
        TO_TIMESTAMP('2026-05-03 20:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (5, 'Arnés acolchado talla L');
INSERT INTO ORDEN_PRODUCTOS (orden_id, producto) VALUES (5, 'Correa retráctil 5m');

-- ---------------------------------------------------------------------
-- CITAS_MEDICAS
-- ---------------------------------------------------------------------

INSERT INTO CITAS_MEDICAS (id, paciente, mascota, tipo_mascota, veterinario, fecha, hora, motivo, estado, fecha_registro)
VALUES (CITA_SEQ.NEXTVAL, 'Ana Torres', 'Bobby', 'Perro', 'Dr. Rodríguez',
        TO_DATE('2026-05-05', 'YYYY-MM-DD'), '09:00', 'Vacunación anual', 'PROGRAMADA',
        TO_TIMESTAMP('2026-05-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CITAS_MEDICAS (id, paciente, mascota, tipo_mascota, veterinario, fecha, hora, motivo, estado, fecha_registro)
VALUES (CITA_SEQ.NEXTVAL, 'Pedro Soto', 'Luna', 'Gato', 'Dra. Martínez',
        TO_DATE('2026-05-06', 'YYYY-MM-DD'), '11:00', 'Control de peso y revisión general', 'PROGRAMADA',
        TO_TIMESTAMP('2026-05-04 01:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CITAS_MEDICAS (id, paciente, mascota, tipo_mascota, veterinario, fecha, hora, motivo, estado, fecha_registro)
VALUES (CITA_SEQ.NEXTVAL, 'Lucía Ramírez', 'Coco', 'Conejo', 'Dr. Rodríguez',
        TO_DATE('2026-05-07', 'YYYY-MM-DD'), '15:00', 'Desparasitación', 'PROGRAMADA',
        TO_TIMESTAMP('2026-05-04 03:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CITAS_MEDICAS (id, paciente, mascota, tipo_mascota, veterinario, fecha, hora, motivo, estado, fecha_registro)
VALUES (CITA_SEQ.NEXTVAL, 'Camila Rojas', 'Nala', 'Perro', 'Dra. Martínez',
        TO_DATE('2026-05-08', 'YYYY-MM-DD'), '12:00', 'Revisión post-operatoria', 'PROGRAMADA',
        TO_TIMESTAMP('2026-05-03 23:00:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CITAS_MEDICAS (id, paciente, mascota, tipo_mascota, veterinario, fecha, hora, motivo, estado, fecha_registro)
VALUES (CITA_SEQ.NEXTVAL, 'Tomás Vargas', 'Simba', 'Gato', 'Dr. Rodríguez',
        TO_DATE('2026-05-09', 'YYYY-MM-DD'), '14:00', 'Limpieza dental', 'CANCELADA',
        TO_TIMESTAMP('2026-05-04 02:00:00', 'YYYY-MM-DD HH24:MI:SS'));

COMMIT;

-- =====================================================================
-- CONSULTAS DE VERIFICACION
-- =====================================================================

-- Verificar registros de ordenes con sus productos asociados
-- SELECT o.id, o.cliente, o.mascota, o.estado, o.total, p.producto
-- FROM ORDENES_COMPRA o
-- LEFT JOIN ORDEN_PRODUCTOS p ON p.orden_id = o.id
-- ORDER BY o.id;

-- Verificar registros de citas
-- SELECT id, paciente, mascota, veterinario, fecha, hora, estado FROM CITAS_MEDICAS ORDER BY fecha, hora;

-- =====================================================================
-- LIMPIEZA (opcional - solo si se quiere recrear desde cero)
-- =====================================================================

-- DROP TABLE ORDEN_PRODUCTOS CASCADE CONSTRAINTS;
-- DROP TABLE ORDENES_COMPRA CASCADE CONSTRAINTS;
-- DROP TABLE CITAS_MEDICAS CASCADE CONSTRAINTS;
-- DROP SEQUENCE ORDEN_SEQ;
-- DROP SEQUENCE CITA_SEQ;
