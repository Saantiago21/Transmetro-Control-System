INSERT INTO usuarios (nombre, apellido, correo, password_hash, estado, fecha_creacion, id_rol) 
VALUES ('Carlos', 'Mendoza', 'carlos.mendoza@transmetro.gob.gt', 'hash_pwd_123', 'ACTIVO', NOW(), 1)
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuarios (nombre, apellido, correo, password_hash, estado, fecha_creacion, id_rol) 
VALUES ('Ana', 'García', 'ana.garcia@transmetro.gob.gt', 'hash_pwd_456', 'ACTIVO', NOW(), 2)
ON CONFLICT (correo) DO NOTHING;