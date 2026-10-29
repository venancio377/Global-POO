package com.utng.inventario.app;

import com.utng.inventario.db.ConexionBD;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/** Punto de entrada de la aplicación JavaFX; abre la pantalla de inicio de sesión. */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Inventario UTNG - Iniciar sesión");
        stage.setScene(Navegador.crearEscena("Login"));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        try {
            ConexionBD.getInstancia().cerrar();
        } catch (Exception ignorada) {
            System.err.println("No se pudo cerrar la conexión: " + ignorada.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
