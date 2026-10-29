package com.utng.inventario.modelo;

/**
 * Clasificación de los artículos del inventario.
 * <p>
 * El nombre de la constante ({@link #name()}) es el valor que se guarda en la
 * columna {@code Tipo} de la base de datos; la etiqueta es el texto que ve el usuario.
 */
public enum TipoArticulo {
    EQUIPO("Equipo de cómputo"),
    CONSUMIBLE("Material consumible");

    private final String etiqueta;

    TipoArticulo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
