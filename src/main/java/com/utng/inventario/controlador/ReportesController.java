package com.utng.inventario.controlador;

import com.utng.inventario.app.Alertas;
import com.utng.inventario.dao.ArticuloDAO;
import com.utng.inventario.dao.ArticuloDAOImpl;
import com.utng.inventario.dao.DAOException;
import com.utng.inventario.dao.ReporteDAO;
import com.utng.inventario.modelo.Articulo;
import com.utng.inventario.modelo.ResumenInventario;
import com.utng.inventario.util.ExportadorCSV;
import com.utng.inventario.util.Formato;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/** Controlador de Reportes.fxml: indicadores, consumibles por reabastecer y exportación a CSV. */
public class ReportesController extends ControladorBase {

    @FXML private Label lblTotal;
    @FXML private Label lblValor;
    @FXML private Label lblStockBajo;
    @FXML private Label lblPrestamos;
    @FXML private TableView<Articulo> tabla;
    @FXML private TableColumn<Articulo, String> colCodigo;
    @FXML private TableColumn<Articulo, String> colNombre;
    @FXML private TableColumn<Articulo, Integer> colCantidad;
    @FXML private TableColumn<Articulo, Integer> colMinimo;
    @FXML private TableColumn<Articulo, String> colArea;
    @FXML private TableColumn<Articulo, String> colEstado;

    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final ReporteDAO reporteDAO = new ReporteDAO();

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCodigo()));
        colNombre.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getNombre()));
        colCantidad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidad()));
        colMinimo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStockMinimo()));
        colArea.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getArea().getNombre()));
        colEstado.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getEstadoStock()));
        try {
            ResumenInventario r = reporteDAO.obtenerResumen();
            lblTotal.setText(String.valueOf(r.totalArticulos()));
            lblValor.setText(Formato.dinero(r.valorTotal()));
            lblStockBajo.setText(String.valueOf(r.stockBajo()));
            lblPrestamos.setText(String.valueOf(r.prestamosActivos()));
            tabla.setItems(FXCollections.observableArrayList(articuloDAO.leerStockBajo()));
        } catch (DAOException e) {
            Alertas.error(e.getMessage());
        }
    }

    @FXML
    private void accionVolver() {
        volverAlPanel(tabla);
    }

    /** Guarda todo el inventario en un archivo CSV que se abre con Excel. */
    @FXML
    private void accionExportar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar inventario");
        selector.setInitialFileName("inventario_utng.csv");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        File destino = selector.showSaveDialog(tabla.getScene().getWindow());
        if (destino == null) {
            return;
        }
        try {
            ExportadorCSV.exportar(articuloDAO.leerTodos(), destino.toPath());
            Alertas.info("Inventario exportado en:\n" + destino.getAbsolutePath());
        } catch (DAOException | IOException e) {
            Alertas.error("No se pudo exportar: " + e.getMessage());
        }
    }
}
