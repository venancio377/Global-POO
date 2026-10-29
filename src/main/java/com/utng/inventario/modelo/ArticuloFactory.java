package com.utng.inventario.modelo;

/**
 * Patrón <b>Factory Method</b>: centraliza la creación del tipo correcto de {@link Articulo}
 * para que el DAO y los controladores no dependan de las subclases concretas.
 */
public final class ArticuloFactory {

    private ArticuloFactory() {
    }

    /**
     * Crea el artículo que corresponde al tipo indicado.
     *
     * @throws IllegalArgumentException si el tipo es nulo o algún dato no es válido
     */
    public static Articulo crear(TipoArticulo tipo, int id, String codigo, String nombre, String descripcion,
                                 int cantidad, int stockMinimo, double precio, Area area, Responsable responsable) {
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona el tipo de artículo");
        }
        return switch (tipo) {
            case EQUIPO -> new EquipoComputo(id, codigo, nombre, descripcion, cantidad, stockMinimo, precio, area, responsable);
            case CONSUMIBLE -> new MaterialConsumible(id, codigo, nombre, descripcion, cantidad, stockMinimo, precio, area, responsable);
        };
    }

    /** Variante para valores leídos de la base de datos (columna Tipo). */
    public static Articulo crear(String tipo, int id, String codigo, String nombre, String descripcion,
                                 int cantidad, int stockMinimo, double precio, Area area, Responsable responsable) {
        try {
            return crear(TipoArticulo.valueOf(tipo), id, codigo, nombre, descripcion, cantidad, stockMinimo,
                    precio, area, responsable);
        } catch (IllegalArgumentException | NullPointerException e) {
            if (e.getMessage() != null && e.getMessage().contains("obligatorio")) {
                throw e;
            }
            throw new IllegalArgumentException("Tipo de artículo no válido: " + tipo, e);
        }
    }
}
