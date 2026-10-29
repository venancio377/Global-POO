package com.utng.inventario.modelo;

/**
 * Equipo físico (computadora, monitor, switch...). Se considera disponible mientras haya piezas;
 * no usa stock mínimo porque no se consume.
 */
public class EquipoComputo extends Articulo {

    public EquipoComputo(int id, String codigo, String nombre, String descripcion, int cantidad,
                         int stockMinimo, double precioUnitario, Area area, Responsable responsable) {
        super(id, codigo, nombre, descripcion, cantidad, stockMinimo, precioUnitario, area, responsable);
    }

    @Override
    public TipoArticulo getTipo() {
        return TipoArticulo.EQUIPO;
    }

    @Override
    public String getEstadoStock() {
        return getCantidad() > 0 ? "Disponible" : "Sin existencia";
    }
}
