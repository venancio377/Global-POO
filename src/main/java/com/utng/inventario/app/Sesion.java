package com.utng.inventario.app;

import com.utng.inventario.modelo.Usuario;

/** Guarda el usuario que inició sesión mientras la aplicación está abierta. */
public final class Sesion {
    private static Usuario usuario;

    private Sesion() {
    }

    public static Usuario getUsuario() { return usuario; }

    public static void iniciar(Usuario u) { usuario = u; }

    public static void cerrar() { usuario = null; }
}
