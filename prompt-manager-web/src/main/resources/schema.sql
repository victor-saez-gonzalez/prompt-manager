DROP TABLE IF EXISTS category;

CREATE TABLE category (
  id VARCHAR(36) PRIMARY KEY,  -- UUID almacenado como VARCHAR(36)
  name VARCHAR(255),
  description VARCHAR(255)
);

DROP TABLE IF EXISTS users;
-- Crear la tabla de usuarios (si es que aún no la tienes)
CREATE TABLE users (
  id VARCHAR(36) PRIMARY KEY,  -- UUID almacenado como VARCHAR(36)
  email VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255),
  password VARCHAR(255),  -- Contraseña (asegurarse de almacenar un hash de la contraseña)
  provider VARCHAR(50) NOT NULL,  -- Proveedor (por ejemplo, 'LOCAL')
  provider_id VARCHAR(255)
);
