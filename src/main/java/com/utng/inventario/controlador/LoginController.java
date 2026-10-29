package com.utng.inventario.controlador;

import com.utng.inventario.app.Sesion;
import com.utng.inventario.dao.DAOException;
import com.utng.inventario.dao.UsuarioDAO;
import com.utng.inventario.modelo.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** Controlador de Login.fxml: valida credenciales y abre el panel principal. */
public class LoginController extends ControladorBase {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void accionLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();
        if (usuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Escribe tu usuario y contraseña");
            return;
        }
        try {
            Usuario autenticado = usuarioDAO.autenticar(usuario, password);
            if (autenticado == null) {
                lblMensaje.setText("Usuario o contraseña incorrectos");
                txtPassword.clear();
                return;
            }
            Sesion.iniciar(autenticado);
            volverAlPanel(txtUsuario);
        } catch (DAOException e) {
            lblMensaje.setText("Error de conexión: " + e.getMessage());
        }
    }
}
