-- =====================================================================
-- Sistema de Inventario UTNG  |  SQL Server (SSMS)
-- Ejecutar completo en SQL Server Management Studio (F5)
-- =====================================================================
IF DB_ID('InventarioUTNG') IS NULL
    CREATE DATABASE InventarioUTNG;
GO

USE InventarioUTNG;
GO

IF OBJECT_ID('dbo.vw_Inventario', 'V') IS NOT NULL DROP VIEW dbo.vw_Inventario;
IF OBJECT_ID('dbo.Prestamo', 'U') IS NOT NULL DROP TABLE dbo.Prestamo;
IF OBJECT_ID('dbo.Usuario', 'U') IS NOT NULL DROP TABLE dbo.Usuario;
IF OBJECT_ID('dbo.Articulo', 'U') IS NOT NULL DROP TABLE dbo.Articulo;
IF OBJECT_ID('dbo.Responsable', 'U') IS NOT NULL DROP TABLE dbo.Responsable;
IF OBJECT_ID('dbo.Area', 'U') IS NOT NULL DROP TABLE dbo.Area;
GO

CREATE TABLE dbo.Area (
    IdArea     INT IDENTITY(1,1) CONSTRAINT PK_Area PRIMARY KEY,
    Nombre     NVARCHAR(80)  NOT NULL CONSTRAINT UQ_Area_Nombre UNIQUE,
    Ubicacion  NVARCHAR(100) NOT NULL
);

CREATE TABLE dbo.Responsable (
    IdResponsable INT IDENTITY(1,1) CONSTRAINT PK_Responsable PRIMARY KEY,
    Nombre        NVARCHAR(100) NOT NULL,
    Correo        NVARCHAR(100) NOT NULL,
    Telefono      NVARCHAR(20)  NULL
);

CREATE TABLE dbo.Articulo (
    IdArticulo     INT IDENTITY(1,1) CONSTRAINT PK_Articulo PRIMARY KEY,
    Codigo         NVARCHAR(20)  NOT NULL CONSTRAINT UQ_Articulo_Codigo UNIQUE,
    Nombre         NVARCHAR(100) NOT NULL,
    Descripcion    NVARCHAR(250) NULL,
    Tipo           NVARCHAR(20)  NOT NULL
                   CONSTRAINT CK_Articulo_Tipo CHECK (Tipo IN ('EQUIPO', 'CONSUMIBLE')),
    Cantidad       INT           NOT NULL CONSTRAINT CK_Articulo_Cantidad CHECK (Cantidad >= 0),
    StockMinimo    INT           NOT NULL CONSTRAINT DF_Articulo_Min DEFAULT 0
                   CONSTRAINT CK_Articulo_Min CHECK (StockMinimo >= 0),
    PrecioUnitario DECIMAL(10,2) NOT NULL CONSTRAINT CK_Articulo_Precio CHECK (PrecioUnitario >= 0),
    IdArea         INT           NOT NULL
                   CONSTRAINT FK_Articulo_Area FOREIGN KEY REFERENCES dbo.Area(IdArea),
    IdResponsable  INT           NOT NULL
                   CONSTRAINT FK_Articulo_Responsable FOREIGN KEY REFERENCES dbo.Responsable(IdResponsable),
    FechaRegistro  DATETIME      NOT NULL CONSTRAINT DF_Articulo_Fecha DEFAULT GETDATE()
);

CREATE TABLE dbo.Usuario (
    IdUsuario      INT IDENTITY(1,1) CONSTRAINT PK_Usuario PRIMARY KEY,
    NombreUsuario  NVARCHAR(50)  NOT NULL CONSTRAINT UQ_Usuario_Nombre UNIQUE,
    NombreCompleto NVARCHAR(100) NOT NULL,
    PasswordHash   VARBINARY(32) NOT NULL,
    Rol            NVARCHAR(20)  NOT NULL CONSTRAINT CK_Usuario_Rol CHECK (Rol IN ('ADMIN', 'TECNICO'))
);

-- Regla de negocio: solo puede existir un administrador; técnicos, los que hagan falta.
CREATE UNIQUE INDEX UQ_Usuario_UnSoloAdmin ON dbo.Usuario (Rol) WHERE Rol = 'ADMIN';

CREATE TABLE dbo.Prestamo (
    IdPrestamo       INT IDENTITY(1,1) CONSTRAINT PK_Prestamo PRIMARY KEY,
    IdArticulo       INT      NOT NULL
                     CONSTRAINT FK_Prestamo_Articulo FOREIGN KEY REFERENCES dbo.Articulo(IdArticulo),
    IdResponsable    INT      NOT NULL
                     CONSTRAINT FK_Prestamo_Responsable FOREIGN KEY REFERENCES dbo.Responsable(IdResponsable),
    Cantidad         INT      NOT NULL CONSTRAINT CK_Prestamo_Cant CHECK (Cantidad > 0),
    FechaPrestamo    DATETIME NOT NULL CONSTRAINT DF_Prestamo_Fecha DEFAULT GETDATE(),
    FechaDevolucion  DATETIME NULL
);
GO

CREATE VIEW dbo.vw_Inventario AS
SELECT a.IdArticulo, a.Codigo, a.Nombre, a.Tipo, a.Cantidad, a.StockMinimo,
       a.PrecioUnitario, a.Cantidad * a.PrecioUnitario AS ValorTotal,
       ar.Nombre AS Area, r.Nombre AS Responsable
FROM dbo.Articulo a
JOIN dbo.Area ar ON ar.IdArea = a.IdArea
JOIN dbo.Responsable r ON r.IdResponsable = a.IdResponsable;
GO

INSERT INTO dbo.Area (Nombre, Ubicacion) VALUES
(N'Laboratorio de Cómputo', N'Edificio B, Planta baja'),
(N'Taller de Redes', N'Edificio B, Planta alta'),
(N'Almacén General', N'Edificio A, Bodega 1'),
(N'Sala de Docentes', N'Edificio C, Planta baja');

INSERT INTO dbo.Responsable (Nombre, Correo, Telefono) VALUES
(N'María Fernanda López Ruiz', N'mflopez@utng.edu.mx', N'4181000001'),
(N'José Luis Ramírez Torres', N'jlramirez@utng.edu.mx', N'4181000002'),
(N'Ana Karen Gómez Pérez', N'akgomez@utng.edu.mx', N'4181000003');

INSERT INTO dbo.Articulo (Codigo, Nombre, Descripcion, Tipo, Cantidad, StockMinimo, PrecioUnitario, IdArea, IdResponsable) VALUES
(N'EQ-001', N'Computadora de escritorio', N'Intel Core i5, 16 GB RAM, SSD 512 GB', N'EQUIPO', 25, 0, 14500.00, 1, 1),
(N'EQ-002', N'Monitor 24 pulgadas', N'Monitor LED Full HD', N'EQUIPO', 25, 0, 2800.00, 1, 1),
(N'EQ-003', N'Proyector', N'Proyector 3500 lúmenes HDMI', N'EQUIPO', 4, 0, 9800.00, 4, 3),
(N'EQ-004', N'Switch 24 puertos', N'Switch administrable Gigabit', N'EQUIPO', 6, 0, 5200.00, 2, 2),
(N'EQ-005', N'Router inalámbrico', N'Router doble banda AC1200', N'EQUIPO', 8, 0, 1450.00, 2, 2),
(N'CO-001', N'Cable UTP Cat6 (metro)', N'Cable de red Cat6 por metro', N'CONSUMIBLE', 300, 100, 12.50, 2, 2),
(N'CO-002', N'Conector RJ45', N'Paquete de conectores RJ45', N'CONSUMIBLE', 40, 50, 180.00, 2, 2),
(N'CO-003', N'Marcadores para pizarrón', N'Caja con 12 marcadores', N'CONSUMIBLE', 18, 10, 145.00, 3, 3),
(N'CO-004', N'Hojas blancas tamaño carta', N'Paquete de 500 hojas', N'CONSUMIBLE', 6, 20, 98.00, 3, 3),
(N'CO-005', N'Toner para impresora láser', N'Toner negro compatible', N'CONSUMIBLE', 3, 2, 850.00, 4, 1);

INSERT INTO dbo.Usuario (NombreUsuario, NombreCompleto, PasswordHash, Rol) VALUES
(N'admin',   N'Administrador del Sistema', HASHBYTES('SHA2_256', N'admin123'), 'ADMIN'),
(N'tecnico', N'Técnico de Laboratorio',    HASHBYTES('SHA2_256', N'tecnico123'), 'TECNICO');

INSERT INTO dbo.Prestamo (IdArticulo, IdResponsable, Cantidad, FechaPrestamo, FechaDevolucion) VALUES
(3, 3, 1, DATEADD(DAY, -3, GETDATE()), NULL),
(5, 2, 2, DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, -7, GETDATE()));
GO

SELECT * FROM dbo.vw_Inventario;
GO
