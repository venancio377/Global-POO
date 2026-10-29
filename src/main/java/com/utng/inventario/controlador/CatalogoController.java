package com.utng.inventario.controlador;

import com.utng.inventario.app.Alertas;
import com.utng.inventario.app.Sesion;
import com.utng.inventario.dao.ArticuloDAO;
import com.utng.inventario.dao.ArticuloDAOImpl;
import com.utng.inventario.dao.AreaDAOImpl;
import com.utng.inventario.dao.DAOException;
import com.utng.inventario.dao.ListadoDAO;
import com.utng.inventario.dao.ResponsableDAOImpl;
import com.utng.inventario.modelo.Area;
import com.utng.inventario.modelo.Articulo;
import com.utng.inventario.modelo.ArticuloFactory;
import com.utng.inventario.modelo.Responsable;
import com.utng.inventario.modelo.TipoArticulo;
import com.utng.inventario.modelo.Usuario;
import com.utng.inventario.util.Formato;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.util.List;
import java.util.Locale;

/** Controlador de Catalogo.fxml: alta, consulta, búsqueda, edición y baja de artículos. */
public class CatalogoController extends ControladorBase {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Articulo> tabla;
    @FXML private TableColumn<Articulo, String> colCodigo;
    @FXML private TableColumn<Articulo, String> colNombre;
    @FXML private TableColumn<Articulo, String> colTipo;
    @FXML private TableColumn<Articulo, Integer> colCantidad;
    @FXML private TableColumn<Articulo, String> colArea;
    @FXML private TableColumn<Articulo, String> colResponsable;
    @FXML private TableColumn<Articulo, String> colPrecio;
    @FXML private TableColumn<Articulo, String> colValor;
    @FXML private TableColumn<Articulo, String> colEstado;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<TipoArticulo> cmbTipo;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<Area> cmbArea;
    @FXML private ComboBox<Responsable> cmbResponsable;
    @FXML private Label lblResumen;
    @FXML private Button btnEliminar;

    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final ListadoDAO<Area> areaDAO = new AreaDAOImpl();
    private final ListadoDAO<Responsable> responsableDAO = new ResponsableDAOImpl();

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCodigo()));
        colNombre.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colTipo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTipo().getEtiqueta()));
        colCantidad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidad()));
        colArea.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getArea().getNombre()));
        colResponsable.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getResponsable().getNombre()));
        colPrecio.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Formato.dinero(c.getValue().getPrecioUnitario())));
        colValor.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(Formato.dinero(c.getValue().calcularValorTotal())));
        colEstado.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getEstadoStock()));

        cmbTipo.setItems(FXCollections.observableArrayList(TipoArticulo.values()));
        try {
            cmbArea.setItems(FXCollections.observableArrayList(areaDAO.leerTodos()));
            cmbResponsable.setItems(FXCollections.observableArrayList(responsableDAO.leerTodos()));
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }

        Usuario sesion = Sesion.getUsuario();
        if (sesion != null && !sesion.esAdministrador()) {
            btnEliminar.setDisable(true);
            btnEliminar.setTooltip(new Tooltip("Solo un administrador puede eliminar artículos"));
        }

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (actual != null) {
                llenarFormulario(actual);
            }
        });
        cargarTabla(null);
    }

    @FXML
    private void accionBuscar() {
        cargarTabla(txtBuscar.getText());
    }

    @FXML
    private void accionGuardar() {
        Articulo nuevo = leerFormulario(0);
        if (nuevo == null) {
            return;
        }
        try {
            articuloDAO.crear(nuevo);
            Alertas.info("Artículo registrado correctamente");
            accionLimpiar();
            cargarTabla(null);
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionActualizar() {
        Articulo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.error("Selecciona un artículo de la tabla para actualizarlo");
            return;
        }
        Articulo editado = leerFormulario(seleccionado.getId());
        if (editado == null) {
            return;
        }
        try {
            articuloDAO.actualizar(editado);
            Alertas.info("Artículo actualizado correctamente");
            accionLimpiar();
            cargarTabla(null);
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionEliminar() {
        Articulo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.error("Selecciona un artículo de la tabla para eliminarlo");
            return;
        }
        if (!Alertas.confirmar("¿Eliminar el artículo " + seleccionado + "?")) {
            return;
        }
        try {
            articuloDAO.eliminar(seleccionado.getId());
            accionLimpiar();
            cargarTabla(null);
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionLimpiar() {
        tabla.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtCantidad.clear();
        txtStockMinimo.clear();
        txtPrecio.clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbArea.getSelectionModel().clearSelection();
        cmbResponsable.getSelectionModel().clearSelection();
    }

    @FXML
    private void accionVolver() {
        volverAlPanel(tabla);
    }

    private void cargarTabla(String filtro) {
        try {
            List<Articulo> datos = (filtro == null || filtro.isBlank())
                    ? articuloDAO.leerTodos() : articuloDAO.buscar(filtro);
            tabla.setItems(FXCollections.observableArrayList(datos));
            double total = datos.stream().mapToDouble(Articulo::calcularValorTotal).sum();
            lblResumen.setText(datos.size() + " artículo(s)  |  Valor del inventario: " + Formato.dinero(total));
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    private void llenarFormulario(Articulo a) {
        txtCodigo.setText(a.getCodigo());
        txtNombre.setText(a.getNombre());
        txtDescripcion.setText(a.getDescripcion());
        cmbTipo.setValue(a.getTipo());
        txtCantidad.setText(String.valueOf(a.getCantidad()));
        txtStockMinimo.setText(String.valueOf(a.getStockMinimo()));
        txtPrecio.setText(String.format(Locale.US, "%.2f", a.getPrecioUnitario()));
        cmbArea.getItems().stream().filter(x -> x.getId() == a.getArea().getId())
                .findFirst().ifPresent(cmbArea::setValue);
        cmbResponsable.getItems().stream().filter(x -> x.getId() == a.getResponsable().getId())
                .findFirst().ifPresent(cmbResponsable::setValue);
    }

    private Articulo leerFormulario(int id) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty()) {
            Alertas.error("El código y el nombre son obligatorios");
            return null;
        }
        if (cmbTipo.getValue() == null || cmbArea.getValue() == null || cmbResponsable.getValue() == null) {
            Alertas.error("Selecciona el tipo, el área y el responsable");
            return null;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            int stockMinimo = txtStockMinimo.getText().isBlank() ? 0 : Integer.parseInt(txtStockMinimo.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
            if (cantidad < 0 || stockMinimo < 0 || precio < 0) {
                Alertas.error("Cantidad, stock mínimo y precio no pueden ser negativos");
                return null;
            }
            return ArticuloFactory.crear(cmbTipo.getValue(), id, codigo, nombre, txtDescripcion.getText().trim(),
                    cantidad, stockMinimo, precio, cmbArea.getValue(), cmbResponsable.getValue());
        } catch (NumberFormatException e) {
            Alertas.error("Cantidad, stock mínimo y precio deben ser números válidos");
            return null;
        } catch (IllegalArgumentException e) {
            Alertas.error(e.getMessage());
            return null;
        }
    }
}
