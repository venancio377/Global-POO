package com.utng.inventario.modelo;

/**
 * Material que se gasta con el uso (cables, hojas, tóner). Su estado depende del stock mínimo:
 * Agotado, Reabastecer o Suficiente.
 */
public class MaterialConsumible extends Articulo {

    public MaterialConsumible(int id, String codigo, String nombre, String descripcion, int cantidad,
                              int stockMinimo, double precioUnitario, Area area, Responsable responsable) {
        super(id, codigo, nombre, descripcion, cantidad, stockMinimo, precioUnitario, area, responsable);
    }

    @Override
    public TipoArticulo getTipo() {
        return TipoArticulo.CONSUMIBLE;
    }

    @Override
    public String getEstadoStock() {
        if (getCantidad() == 0) {
            return "Agotado";
        }
        return getCantidad() <= getStockMinimo() ? "Reabastecer" : "Suficiente";
    }
}
