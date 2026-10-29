package com.utng.inventario.dao;

/** Error de la capa de datos; oculta los detalles de JDBC a los controladores. */
public class DAOException extends Exception {
    private static final long serialVersionUID = 1L;

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public DAOException(String mensaje) {
        super(mensaje);
    }
}
