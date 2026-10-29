package com.utng.inventario.modelo;

/** Indicadores que muestra la pantalla de reportes (record inmutable). */
public record ResumenInventario(int totalArticulos, double valorTotal, int stockBajo, int prestamosActivos) {
}
