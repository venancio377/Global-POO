package com.utng.inventario.controlador;

import com.utng.inventario.app.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/** Controlador del menú principal (Principal.fxml). */
public class PrincipalController extends ControladorBase {

    @FXML private Label lblUsuario;
    @FXML private Button btnUsuarios;

    @FXML
    private void initialize() {
        lblUsuario.setText(Sesion.getUsuario().getNombreCompleto() + "  (" + Sesion.getUsuario().getRol() + ")");
        // Solo el administrador gestiona cuentas: al técnico ni siquiera se le muestra la opción.
        boolean admin = Sesion.getUsuario().esAdministrador();
        btnUsuarios.setVisible(admin);
        btnUsuarios.setManaged(admin);
    }

    @FXML
    private void abrirCatalogo() {
        abrir("Catalogo", "Catálogo de artículos");
    }

    @FXML
    private void abrirPrestamos() {
        abrir("Prestamos", "Préstamos");
    }

    @FXML
    private void abrirReportes() {
        abrir("Reportes", "Reportes");
    }

    @FXML
    private void abrirUsuarios() {
        abrir("Usuarios", "Usuarios y accesos");
    }

    @FXML
    private void cerrarSesion() {
        Sesion.cerrar();
        abrir("Login", "Iniciar sesión");
    }

    private void abrir(String vista, String titulo) {
        navegar(lblUsuario, vista, titulo);
    }
}
