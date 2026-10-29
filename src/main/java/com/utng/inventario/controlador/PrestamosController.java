package com.utng.inventario.controlador;

import com.utng.inventario.app.Alertas;
import com.utng.inventario.dao.ArticuloDAO;
import com.utng.inventario.dao.ArticuloDAOImpl;
import com.utng.inventario.dao.DAOException;
import com.utng.inventario.dao.PrestamoDAO;
import com.utng.inventario.dao.ResponsableDAOImpl;
import com.utng.inventario.modelo.Articulo;
import com.utng.inventario.modelo.Prestamo;
import com.utng.inventario.modelo.Responsable;
import com.utng.inventario.util.Formato;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** Controlador de Prestamos.fxml: registra préstamos y devoluciones. */
public class PrestamosController extends ControladorBase {

    @FXML private ComboBox<Articulo> cmbArticulo;
    @FXML private ComboBox<Responsable> cmbResponsable;
    @FXML private TextField txtCantidad;
    @FXML private TableView<Prestamo> tabla;
    @FXML private TableColumn<Prestamo, Integer> colId;
    @FXML private TableColumn<Prestamo, String> colArticulo;
    @FXML private TableColumn<Prestamo, String> colResponsable;
    @FXML private TableColumn<Prestamo, Integer> colCantidad;
    @FXML private TableColumn<Prestamo, String> colPrestamo;
    @FXML private TableColumn<Prestamo, String> colDevolucion;
    @FXML private TableColumn<Prestamo, String> colEstado;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colArticulo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getArticuloNombre()));
        colResponsable.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getResponsableNombre()));
        colCantidad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidad()));
        colPrestamo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Formato.fechaHora(c.getValue().getFechaPrestamo())));
        colDevolucion.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Formato.fechaHora(c.getValue().getFechaDevolucion())));
        colEstado.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getEstado()));
        try {
            cmbResponsable.setItems(FXCollections.observableArrayList(new ResponsableDAOImpl().leerTodos()));
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
        recargar();
    }

    @FXML
    private void accionPrestar() {
        Articulo articulo = cmbArticulo.getValue();
        Responsable responsable = cmbResponsable.getValue();
        if (articulo == null || responsable == null) {
            Alertas.error("Selecciona el artículo y el responsable");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            Alertas.error("La cantidad debe ser un número entero");
            return;
        }
        if (cantidad <= 0) {
            Alertas.error("La cantidad debe ser mayor a cero");
            return;
        }
        try {
            prestamoDAO.prestar(articulo.getId(), responsable.getId(), cantidad);
            Alertas.info("Préstamo registrado");
            txtCantidad.clear();
            recargar();
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionDevolver() {
        Prestamo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.error("Selecciona un préstamo de la tabla");
            return;
        }
        if (!seleccionado.estaActivo()) {
            Alertas.error("Ese préstamo ya fue devuelto");
            return;
        }
        try {
            prestamoDAO.devolver(seleccionado.getId());
            Alertas.info("Devolución registrada");
            recargar();
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionVolver() {
        volverAlPanel(tabla);
    }

    private void recargar() {
        try {
            cmbArticulo.setItems(FXCollections.observableArrayList(articuloDAO.leerTodos()));
            tabla.setItems(FXCollections.observableArrayList(prestamoDAO.leerTodos()));
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }
}
