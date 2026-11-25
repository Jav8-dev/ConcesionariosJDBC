-- Script SQL de referencia para MySQL (la aplicación también lo crea desde Java)

CREATE DATABASE IF NOT EXISTS concesionario_db;
USE concesionario_db;

CREATE TABLE IF NOT EXISTS propietarios (
    id_propietario INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS coches (
    matricula VARCHAR(10) PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    extras VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    id_propietario INT NULL,
    FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
);

CREATE TABLE IF NOT EXISTS traspasos (
    id_traspaso INT AUTO_INCREMENT PRIMARY KEY,
    matricula_coche VARCHAR(10) NOT NULL,
    id_vendedor INT NULL,
    id_comprador INT NOT NULL,
    monto_economico DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (matricula_coche) REFERENCES coches(matricula) ON DELETE CASCADE,
    FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario),
    FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)
);

DROP PROCEDURE IF EXISTS sp_coches_por_marca;

DELIMITER //
CREATE PROCEDURE sp_coches_por_marca(IN p_marca VARCHAR(50))
BEGIN
    SELECT matricula, marca, modelo, extras, precio, id_propietario
    FROM coches
    WHERE LOWER(marca) = LOWER(p_marca);
END //
DELIMITER ;
