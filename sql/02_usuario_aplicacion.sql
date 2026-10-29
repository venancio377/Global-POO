-- =====================================================================
-- OPCIONAL: crea el inicio de sesión que usa la aplicación Java.
-- Ejecutar en SSMS con una cuenta administradora, DESPUÉS de InventarioUTNG.sql.
-- Cambia la contraseña antes de ejecutar y escríbela también en db.properties.
-- Requiere autenticación mixta (SQL Server y Windows) y TCP/IP habilitado.
-- =====================================================================
USE master;
GO
IF NOT EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = N'inventario_app')
    CREATE LOGIN inventario_app WITH PASSWORD = N'CambiaEstaContrasena#2026', CHECK_POLICY = OFF;
GO
USE InventarioUTNG;
GO
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'inventario_app')
    CREATE USER inventario_app FOR LOGIN inventario_app;
ALTER ROLE db_datareader ADD MEMBER inventario_app;
ALTER ROLE db_datawriter ADD MEMBER inventario_app;
GO
