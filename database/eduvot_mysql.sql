CREATE DATABASE IF NOT EXISTS eduvot
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE eduvot;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre_usuario VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    es_administrador BOOLEAN NOT NULL DEFAULT false,
    activo BOOLEAN NOT NULL DEFAULT true,
    grupo_usuario VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS subcategorias (
    id_subcategoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuario_subcategoria (
    id_usuario INT NOT NULL,
    id_subcategoria INT NOT NULL,
    PRIMARY KEY (id_usuario, id_subcategoria),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_subcategoria) REFERENCES subcategorias(id_subcategoria)
);

CREATE TABLE IF NOT EXISTS usuario_familia (
    id_familiar INT NOT NULL,
    id_alumno INT NOT NULL,
    PRIMARY KEY (id_familiar, id_alumno),
    FOREIGN KEY (id_familiar) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_alumno) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS encuestas (
    id_encuesta INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT true,
    tipo_encuesta ENUM('UNA_OPCION', 'VARIAS_OPCIONES') NOT NULL DEFAULT 'UNA_OPCION',
    creada_por INT,
    FOREIGN KEY (creada_por) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS encuesta_subcategoria (
    id_encuesta INT NOT NULL,
    id_subcategoria INT NOT NULL,
    PRIMARY KEY (id_encuesta, id_subcategoria),
    FOREIGN KEY (id_encuesta) REFERENCES encuestas(id_encuesta),
    FOREIGN KEY (id_subcategoria) REFERENCES subcategorias(id_subcategoria)
);

CREATE TABLE IF NOT EXISTS opciones_voto (
    id_opcion INT AUTO_INCREMENT PRIMARY KEY,
    id_encuesta INT NOT NULL,
    texto_opcion VARCHAR(150) NOT NULL,
    FOREIGN KEY (id_encuesta) REFERENCES encuestas(id_encuesta)
);

CREATE TABLE IF NOT EXISTS votos (
    id_voto INT AUTO_INCREMENT PRIMARY KEY,
    id_encuesta INT NOT NULL,
    id_usuario INT NOT NULL,
    codigo_verificacion VARCHAR(100),
    fecha_voto DATETIME NOT NULL,
    UNIQUE KEY uq_voto_usuario_encuesta (id_encuesta, id_usuario),
    FOREIGN KEY (id_encuesta) REFERENCES encuestas(id_encuesta),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS detalle_voto (
    id_voto INT NOT NULL,
    id_opcion INT NOT NULL,
    PRIMARY KEY (id_voto, id_opcion),
    FOREIGN KEY (id_voto) REFERENCES votos(id_voto),
    FOREIGN KEY (id_opcion) REFERENCES opciones_voto(id_opcion)
);

CREATE TABLE IF NOT EXISTS encuestas_archivadas (
    id_archivo INT AUTO_INCREMENT PRIMARY KEY,
    id_encuesta INT NOT NULL UNIQUE,
    fecha_archivo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(150),
    archivada_por INT,
    FOREIGN KEY (id_encuesta) REFERENCES encuestas(id_encuesta),
    FOREIGN KEY (archivada_por) REFERENCES usuarios(id_usuario)
);

INSERT INTO subcategorias (nombre) VALUES
    ('Alumnado'),
    ('Profesorado'),
    ('Familia o tutor')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre);

INSERT INTO usuarios (
    dni,
    nombre_usuario,
    password,
    es_administrador,
    activo,
    grupo_usuario
) VALUES (
    '00000000A',
    'admin',
    'admin',
    true,
    true,
    NULL
) ON DUPLICATE KEY UPDATE
    nombre_usuario = VALUES(nombre_usuario),
    password = VALUES(password),
    es_administrador = VALUES(es_administrador),
    activo = VALUES(activo),
    grupo_usuario = VALUES(grupo_usuario);
