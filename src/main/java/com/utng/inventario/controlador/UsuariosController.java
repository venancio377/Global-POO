package com.utng.inventario.controlador;

import com.utng.inventario.app.Alertas;
import com.utng.inventario.app.Sesion;
import com.utng.inventario.dao.DAOException;
import com.utng.inventario.dao.UsuarioDAO;
import com.utng.inventario.modelo.Usuario;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controlador de Usuarios.fxml (solo administrador): alta de técnicos, cambio de contraseña
 * y baja. Existe un único administrador, por eso la pantalla no permite crear otro.
 */
public class UsuariosController extends ControladorBase {

    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtNombre;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCambiar;
    @FXML private Label lblSeleccion;

    private final UsuarioDAO dao = new UsuarioDAO();

    @FXML
    private void initialize() {
        // Defensa adicional: aunque el menú oculta el acceso, la pantalla se protege sola.
        if (!Sesion.getUsuario().esAdministrador()) {
            Alertas.error("Solo el administrador puede gestionar usuarios");
            volverAlPanelDiferido();
            return;
        }
        colUsuario.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombreUsuario()));
        colNombre.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombreCompleto()));
        colRol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRol()));
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, u) -> actualizarSeleccion(u));
        actualizarSeleccion(null);
        recargar();
    }

    private void volverAlPanelDiferido() {
        javafx.application.Platform.runLater(() -> volverAlPanel(tabla));
    }

    private void recargar() {
        try {
            tabla.setItems(FXCollections.observableArrayList(dao.listar()));
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void actualizarSeleccion(Usuario u) {
        boolean hay = u != null;
        boolean esTecnico = hay && Usuario.ROL_TECNICO.equals(u.getRol());
        btnEliminar.setDisable(!esTecnico);
        btnCambiar.setDisable(!hay);
        lblSeleccion.setText(!hay ? "Selecciona un usuario para cambiar su contraseña o eliminarlo"
                : "Seleccionado: " + u.getNombreUsuario() + (esTecnico ? "" : " (administrador, no se puede eliminar)"));
    }

    @FXML
    private void accionAgregar() {
        if (!coinciden()) {
            return;
        }
        try {
            dao.crearTecnico(txtUsuario.getText(), txtNombre.getText(), txtPassword.getText());
            Alertas.info("Técnico " + txtUsuario.getText().trim() + " registrado correctamente");
            limpiar();
            recargar();
        } catch (IllegalArgumentException | DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionCambiarPassword() {
        Usuario u = tabla.getSelectionModel().getSelectedItem();
        if (u == null || !coinciden()) {
            return;
        }
        try {
            dao.cambiarPassword(u.getId(), txtPassword.getText());
            Alertas.info("Contraseña de " + u.getNombreUsuario() + " actualizada");
            limpiar();
        } catch (IllegalArgumentException | DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionEliminar() {
        Usuario u = tabla.getSelectionModel().getSelectedItem();
        if (u == null || !Alertas.confirmar("¿Eliminar al técnico " + u.getNombreUsuario() + "?")) {
            return;
        }
        try {
            dao.eliminarTecnico(u.getId());
            recargar();
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionLimpiar() {
        limpiar();
        tabla.getSelectionModel().clearSelection();
    }

    @FXML
    private void accionVolver() {
        volverAlPanel(tabla);
    }

    private boolean coinciden() {
        if (!txtPassword.getText().equals(txtConfirmar.getText())) {
            Alertas.error("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    private void limpiar() {
        txtUsuario.clear();
        txtNombre.clear();
        txtPassword.clear();
        txtConfirmar.clear();
    }
}
