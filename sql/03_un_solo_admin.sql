-- Para una base InventarioUTNG que YA existe (sin volver a crearla).
-- Agrega la regla "un solo administrador" y no borra datos.
USE InventarioUTNG;
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Usuario_UnSoloAdmin')
    CREATE UNIQUE INDEX UQ_Usuario_UnSoloAdmin ON dbo.Usuario (Rol) WHERE Rol = 'ADMIN';
GO
-- Prueba: este INSERT debe fallar con el error 2601 (ya hay un administrador).
-- INSERT INTO dbo.Usuario (NombreUsuario, NombreCompleto, PasswordHash, Rol)
-- VALUES (N'otro', N'Otro Admin', HASHBYTES('SHA2_256', N'clave123'), 'ADMIN');
