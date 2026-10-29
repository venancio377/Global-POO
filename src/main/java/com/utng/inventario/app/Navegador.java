package com.utng.inventario.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Carga las vistas FXML y cambia de pantalla dentro de la misma ventana. */
public final class Navegador {

    private Navegador() {
    }

    public static Scene crearEscena(String vista) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navegador.class.getResource("/vista/" + vista + ".fxml"));
        Parent raiz = loader.load();
        Scene escena = new Scene(raiz);
        escena.getStylesheets().add(Navegador.class.getResource("/css/estilos.css").toExternalForm());
        return escena;
    }

    public static void ir(Node origen, String vista, String titulo) throws IOException {
        Stage stage = (Stage) origen.getScene().getWindow();
        stage.setScene(crearEscena(vista));
        stage.setTitle(titulo);
        stage.setResizable(!"Login".equals(vista));
        stage.sizeToScene();
        stage.centerOnScreen();
    }
}
