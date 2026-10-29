# Global POO · Sistema de Inventario UTNG

Aplicación de escritorio en **JavaFX** con base de datos **SQL Server**, hecha con programación orientada a objetos y arquitectura **MVC + DAO**. Controla el equipo de cómputo y los materiales consumibles de las áreas de la Universidad Tecnológica del Norte de Guanajuato: quién los tiene, cuántos hay, cuándo se prestan y cuándo hay que reabastecer.

Proyecto de la evaluación global de *Programación Orientada a Objetos* (TSU en Desarrollo de Software Multiplataforma).

## Qué hace

| Módulo | Funciones |
|---|---|
| Inicio de sesión | Usuarios guardados en la base de datos, contraseña con hash SHA-256, dos roles (`ADMIN` y `TECNICO`). |
| Catálogo | Alta, consulta, búsqueda, edición y baja de artículos. Solo el administrador puede eliminar. |
| Préstamos | Registro y devolución; cada operación descuenta o regresa existencia dentro de una transacción. |
| Usuarios | Solo el administrador agrega técnicos, cambia contraseñas y elimina técnicos. Hay un único administrador. |
| Reportes | Indicadores del inventario, consumibles por reabastecer y exportación a CSV (se abre con Excel). |

## POO aplicada

- **Abstracción y herencia:** `Articulo` (abstracta) → `EquipoComputo`, `MaterialConsumible`; `Persona` (abstracta) → `Responsable`.
- **Polimorfismo:** `getTipo()` y `getEstadoStock()` se comportan distinto en cada subclase.
- **Encapsulamiento:** atributos privados; los setters y constructores validan los datos (`IllegalArgumentException`).
- **Patrones:** Singleton (`ConexionBD`), DAO (`CrudDAO`, `ArticuloDAO`…), Factory (`ArticuloFactory`), MVC (FXML + controladores).

## Estructura

```
Global POO/
├── pom.xml
├── sql/
│   ├── InventarioUTNG.sql          crea la base, las tablas y los datos de prueba
│   ├── 02_usuario_aplicacion.sql   (opcional) crea el login inventario_app
│   └── 03_un_solo_admin.sql        (base ya creada) agrega la regla de un solo administrador
├── docs/
│   ├── Documentacion_Inventario_UTNG.docx   documentación completa
│   ├── capturas/                   pantallas de la aplicación
│   └── diagramas/                  casos de uso, clases, entidad-relación, arquitectura
└── src
    ├── main/java/com/utng/inventario
    │   ├── app          MainApp, Navegador, Sesion, Alertas
    │   ├── modelo       Articulo, EquipoComputo, MaterialConsumible, TipoArticulo, Persona, Responsable, Area, Usuario, Prestamo
    │   ├── db           ConexionBD (Singleton JDBC)
    │   ├── dao          interfaces e implementaciones con PreparedStatement
    │   ├── controlador  ControladorBase, Login, Principal, Catalogo, Prestamos, Reportes, Usuarios
    │   └── util         Formato, ExportadorCSV
    ├── main/resources   db.properties, vista/*.fxml, css/estilos.css
    └── test/java        pruebas unitarias (JUnit 5)
```

## Cómo ejecutarlo

1. Abre `sql/InventarioUTNG.sql` en **SQL Server Management Studio** y ejecútalo completo (F5). Crea la base `InventarioUTNG` con datos de prueba. Si ya existía, borra y vuelve a crear las tablas.
2. En **SQL Server Configuration Manager** habilita **TCP/IP** y, en las propiedades del servidor, la autenticación mixta (SQL Server y Windows). Reinicia el servicio.
3. Usa un inicio de sesión de SQL Server con acceso a la base (la cuenta `sa` suele venir deshabilitada). Si no tienes uno, edita la contraseña de `sql/02_usuario_aplicacion.sql` y ejecútalo.
4. Escribe el usuario y la contraseña en `src/main/resources/db.properties`. Para una instancia con nombre, ahí mismo hay un ejemplo de URL.
5. Con JDK 17 o superior y Maven, desde esta carpeta:
   ```
   mvn clean javafx:run
   ```
   También puedes abrir la carpeta en NetBeans, IntelliJ o VS Code y ejecutar `MainApp`.

Para correr las pruebas: `mvn test`.

**Usuarios de prueba:** `admin` / `admin123` (puede eliminar artículos y administrar usuarios) y `tecnico` / `tecnico123`.

**Administración de usuarios:** hay un solo administrador y los técnicos que hagan falta. El administrador entra a *Usuarios y accesos* desde el menú para agregar técnicos, cambiar contraseñas y eliminar técnicos. La base lo refuerza con el índice `UQ_Usuario_UnSoloAdmin`; si ya tienes la base creada, ejecuta `sql/03_un_solo_admin.sql` sin perder datos.

## Subir a GitHub

```
git add .
git commit -m "Mensaje que diga qué cambió"
git push
```

No subas tu contraseña real de SQL Server: deja `db.properties` con el valor de ejemplo antes de hacer commit.
