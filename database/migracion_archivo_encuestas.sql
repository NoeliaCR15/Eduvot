USE eduvot;

CREATE TABLE IF NOT EXISTS encuestas_archivadas (
    id_archivo INT AUTO_INCREMENT PRIMARY KEY,
    id_encuesta INT NOT NULL UNIQUE,
    fecha_archivo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(150),
    archivada_por INT,
    FOREIGN KEY (id_encuesta) REFERENCES encuestas(id_encuesta),
    FOREIGN KEY (archivada_por) REFERENCES usuarios(id_usuario)
);

-- Ejecutar una sola vez. Si ya hay votos duplicados para la misma encuesta
-- y usuario, hay que revisarlos antes de crear este indice.
CREATE UNIQUE INDEX uq_voto_usuario_encuesta
ON votos (id_encuesta, id_usuario);
