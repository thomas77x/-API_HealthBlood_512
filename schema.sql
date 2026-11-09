-- Esquema de la base de datos gluco_api (MySQL 8)
-- En una base de datos hospedada (TiDB Cloud, Aiven...) la base ya existe:
-- omite las dos primeras sentencias y ejecuta el resto sobre ella.
CREATE DATABASE IF NOT EXISTS gluco_api CHARACTER SET utf8mb4;
USE gluco_api;

CREATE TABLE IF NOT EXISTS usuario (
    idUsuario       INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(40)  NOT NULL,
    primerApellido  VARCHAR(40)  NOT NULL,
    segundoApellido VARCHAR(40)  NULL,
    email           VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS medida (
    idMedida   INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario  INT          NOT NULL,
    glucosa    INT          NOT NULL,
    fechaHora  DATETIME     NOT NULL,
    contexto   VARCHAR(40)  NULL,
    notas      VARCHAR(200) NULL,
    CONSTRAINT fk_medida_usuario FOREIGN KEY (idUsuario) REFERENCES usuario (idUsuario)
);
