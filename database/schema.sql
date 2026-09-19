// SCRIPT DE CREACIÓN DE BASE DE DATOS: TRANSMETRO CONTROL SYSTEM

DROP TABLE IF EXISTS Validaciones_Molinete CASCADE;
DROP TABLE IF EXISTS Monitoreo_Frecuencia CASCADE;
DROP TABLE IF EXISTS Asignacion_Turnos CASCADE;
DROP TABLE IF EXISTS Ruta_Estaciones CASCADE;
DROP TABLE IF EXISTS Rutas CASCADE;
DROP TABLE IF EXISTS Estaciones CASCADE;
DROP TABLE IF EXISTS Buses CASCADE;
DROP TABLE IF EXISTS Pilotos CASCADE;
DROP TABLE IF EXISTS Usuarios CASCADE;
DROP TABLE IF EXISTS Roles CASCADE;

//1 Tabla de Roles
CREATE TABLE Roles (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
);

//2 Tabla de Usuarios del Sistema
CREATE TABLE Usuarios (
    id_usuario SERIAL PRIMARY KEY,
    id_rol INT NOT NULL REFERENCES Roles(id_rol) ON DELETE RESTRICT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

//3 Tabla de Pilotos
CREATE TABLE Pilotos (
    id_piloto SERIAL PRIMARY KEY,
    dpi VARCHAR(13) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    licencia VARCHAR(20) NOT NULL,
    telefono VARCHAR(15),
    estado VARCHAR(20) DEFAULT 'Activo' CHECK (estado IN ('Activo', 'Inactivo', 'Suspendido'))
);

//4 Tabla de Unidades (Buses)
CREATE TABLE Buses (
    id_bus SERIAL PRIMARY KEY,
    numero_unidad VARCHAR(20) NOT NULL UNIQUE,
    placa VARCHAR(15) NOT NULL UNIQUE,
    capacidad_pasajeros INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'Operativo' CHECK (estado IN ('Operativo', 'En Mantenimiento', 'Fuera de Servicio'))
);

//5 Tabla de Estaciones
CREATE TABLE Estaciones (
    id_estacion SERIAL PRIMARY KEY,
    nombre_estacion VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(200),
    capacidad_molinetes INT DEFAULT 1
);

//6 Tabla de Rutas
CREATE TABLE Rutas (
    id_ruta SERIAL PRIMARY KEY,
    nombre_ruta VARCHAR(100) NOT NULL,
    estacion_origen_id INT NOT NULL REFERENCES Estaciones(id_estacion),
    estacion_destino_id INT NOT NULL REFERENCES Estaciones(id_estacion),
    descripcion TEXT
);

// 7. Tabla Intermedia Ruta - Estaciones
CREATE TABLE Ruta_Estaciones (
    id_ruta_estacion SERIAL PRIMARY KEY,
    id_ruta INT NOT NULL REFERENCES Rutas(id_ruta) ON DELETE CASCADE,
    id_estacion INT NOT NULL REFERENCES Estaciones(id_estacion) ON DELETE CASCADE,
    orden_estacion INT NOT NULL
);

// 8. Tabla de Asignación de Turnos
CREATE TABLE Asignacion_Turnos (
    id_asignacion SERIAL PRIMARY KEY,
    id_piloto INT NOT NULL REFERENCES Pilotos(id_piloto),
    id_bus INT NOT NULL REFERENCES Buses(id_bus),
    id_ruta INT NOT NULL REFERENCES Rutas(id_ruta),
    fecha_asignacion DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) DEFAULT 'Programado' CHECK (estado IN ('Programado', 'En Curso', 'Finalizado', 'Cancelado'))
);

// 9. Tabla de Monitoreo de Frecuencia
CREATE TABLE Monitoreo_Frecuencia (
    id_monitoreo SERIAL PRIMARY KEY,
    id_asignacion INT NOT NULL REFERENCES Asignacion_Turnos(id_asignacion),
    id_estacion INT NOT NULL REFERENCES Estaciones(id_estacion),
    hora_llegada_programada TIME,
    hora_llegada_real TIME DEFAULT CURRENT_TIME,
    retraso_minutos INT DEFAULT 0
);

// 10. Tabla de Validaciones en Molinetes
CREATE TABLE Validaciones_Molinete (
    id_validacion SERIAL PRIMARY KEY,
    id_estacion INT NOT NULL REFERENCES Estaciones(id_estacion),
    codigo_tarjeta VARCHAR(50) NOT NULL,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    monto_cobrado DECIMAL(5,2) DEFAULT 1.00,
    resultado_acceso VARCHAR(20) NOT NULL CHECK (resultado_acceso IN ('Aprobado', 'Saldo Insuficiente', 'Tarjeta Invalida'))
);

// Insertar Datos (Iniciales)
INSERT INTO Roles (nombre_rol, descripcion) VALUES 
('Administrador', 'Control total del sistema'),
('Operador', 'Gestión de turnos y monitoreo'),
('Supervisor', 'Control de estaciones');

INSERT INTO Estaciones (nombre_estacion, ubicacion) VALUES 
('Plaza El Amate', 'Zona 1, Ciudad de Guatemala'),
('El Obelisco', 'Zona 10, Ciudad de Guatemala'),
('Trebol', 'Zona 11, Ciudad de Guatemala');