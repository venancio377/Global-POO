package com.utng.inventario.app;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/** Cuadros de diálogo reutilizables (información, error y confirmación). */
public final class Alertas {

    /** Botones propios para que el idioma no dependa de la configuración regional del equipo. */
    public static final ButtonType ACEPTAR = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType SI = new ButtonType("Sí", ButtonBar.ButtonData.YES);
    public static final ButtonType NO = new ButtonType("No", ButtonBar.ButtonData.NO);

    private Alertas() {
    }

    public static void info(String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, "Información", mensaje);
    }

    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "Error", mensaje);
    }

    public static boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, SI, NO);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText(null);
        return alerta.showAndWait().filter(b -> b == SI).isPresent();
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ACEPTAR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
