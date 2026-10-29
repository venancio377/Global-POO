package com.utng.inventario.controlador;

import com.utng.inventario.app.Alertas;
import com.utng.inventario.app.Navegador;
import javafx.scene.Node;

import java.io.IOException;

/**
 * Comportamiento común de los controladores (herencia): evita repetir en cada uno el
 * código para cambiar de pantalla y mostrar el error si la vista no carga.
 */
public abstract class ControladorBase {

    /** Cambia a otra vista; si falla, avisa al usuario en lugar de cerrar la aplicación. */
    protected void navegar(Node origen, String vista, String titulo) {
        try {
            Navegador.ir(origen, vista, "Inventario UTNG - " + titulo);
        } catch (IOException e) {
            Alertas.error("No se pudo abrir la pantalla " + titulo + ": " + e.getMessage());
        }
    }

    /** Regresa al menú principal. */
    protected void volverAlPanel(Node origen) {
        navegar(origen, "Principal", "Panel principal");
    }
}
