-- 1. Tabla de Servicios (Configuración maestra)
CREATE TABLE IF NOT EXISTS servicios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    max_perfiles_base INT NOT NULL,
    max_perfiles_extra INT NOT NULL,
    valor_base DECIMAL(10, 2) NOT NULL,
    valor_perfil DECIMAL(10, 2) NOT NULL
    );

-- 2. Tabla de Cuentas
CREATE TABLE IF NOT EXISTS cuentas (
    id BIGSERIAL PRIMARY KEY,
    servicio_id BIGSERIAL REFERENCES servicios(id) ON DELETE CASCADE,
    clave VARCHAR(100),
    correo_principal VARCHAR(255) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    cupos_extra_contratados INT DEFAULT 0
    );

-- 3. Tabla de Perfiles/Usuarios (Relación Muchos a Uno con Cuenta)
CREATE TABLE IF NOT EXISTS perfiles (
    id_perfil BIGSERIAL PRIMARY KEY,
    cuenta_id BIGSERIAL  NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE, -- ← CAMBIO CLAVE
    nombre_cliente VARCHAR(255) NULL,
    telefono_cliente VARCHAR(20) NULL,
    fecha_inicio DATE NULL,
    fecha_fin DATE NULL,
    estado VARCHAR(20) NULL
    );

CREATE TABLE IF NOT EXISTS clientes (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    apellido    VARCHAR(150) NOT NULL,
    telefono    VARCHAR(20)  NOT NULL,
    CONSTRAINT uq_clientes_telefono UNIQUE (telefono)
    );
-- Agregar referencia opcional en perfiles
ALTER TABLE perfiles
    ADD COLUMN IF NOT EXISTS cliente_id BIGINT NULL REFERENCES clientes(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_perfiles_cliente_id ON perfiles(cliente_id);
-- Crear índices para performance
CREATE INDEX IF NOT EXISTS idx_perfiles_cuenta_id ON perfiles(cuenta_id);
CREATE INDEX IF NOT EXISTS idx_perfiles_estado ON perfiles(estado);
CREATE INDEX IF NOT EXISTS idx_perfiles_nombre_cliente ON perfiles(LOWER(nombre_cliente));