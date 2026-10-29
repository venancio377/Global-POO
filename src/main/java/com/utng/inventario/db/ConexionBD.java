package com.utng.inventario.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Patrón <b>Singleton</b>: una sola instancia administra la conexión a SQL Server.
 * Lee {@code db.properties}; las variables de entorno {@code DB_URL}, {@code DB_USUARIO} y
 * {@code DB_PASSWORD} tienen prioridad si existen.
 */
public final class ConexionBD {
    private static ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String password;
    private Connection conexion;

    private ConexionBD() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new SQLException("No se encontro el archivo db.properties");
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new SQLException("No se pudo leer db.properties: " + e.getMessage(), e);
        }
        this.url = valor("DB_URL", props.getProperty("db.url"));
        this.usuario = valor("DB_USUARIO", props.getProperty("db.usuario"));
        this.password = valor("DB_PASSWORD", props.getProperty("db.password"));
        abrir();
    }

    public static synchronized ConexionBD getInstancia() throws SQLException {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
            abrir();
        }
        return conexion;
    }

    public synchronized void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }

    private static String valor(String variableEntorno, String porDefecto) {
        String v = System.getenv(variableEntorno);
        return v == null || v.isBlank() ? porDefecto : v;
    }

    private void abrir() throws SQLException {
        conexion = DriverManager.getConnection(url, usuario, password);
    }
}
