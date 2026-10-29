package com.utng.inventario.modelo;

/** Cuenta que inicia sesión en el sistema. El rol define qué acciones puede realizar. */
public class Usuario {
    private int id;
    private String nombreUsuario;
    private String nombreCompleto;
    private String rol;

    public Usuario(int id, String nombreUsuario, String nombreCompleto, String rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    /** @return {@code true} si el usuario tiene rol ADMIN (puede eliminar artículos) */
    public boolean esAdministrador() {
        return ROL_ADMIN.equals(rol);
    }

    /** Rol del único administrador del sistema. */
    public static final String ROL_ADMIN = "ADMIN";
    /** Rol de los usuarios que operan el inventario; puede haber varios. */
    public static final String ROL_TECNICO = "TECNICO";

    /**
     * Valida los datos de una cuenta nueva.
     *
     * @throws IllegalArgumentException con el motivo, listo para mostrarse al usuario
     */
    public static void validarNuevo(String nombreUsuario, String nombreCompleto, String password) {
        if (nombreUsuario == null || !nombreUsuario.matches("[A-Za-z0-9_.]{3,30}")) {
            throw new IllegalArgumentException(
                    "El usuario debe tener de 3 a 30 caracteres: letras, números, punto o guion bajo");
        }
        if (nombreCompleto == null || nombreCompleto.trim().length() < 3) {
            throw new IllegalArgumentException("Escribe el nombre completo de la persona");
        }
        validarPassword(password);
    }

    /** @throws IllegalArgumentException si la contraseña no cumple la longitud mínima */
    public static void validarPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (password.length() > 50) {
            throw new IllegalArgumentException("La contraseña no puede pasar de 50 caracteres");
        }
    }

    public int getId() { return id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getRol() { return rol; }
}
