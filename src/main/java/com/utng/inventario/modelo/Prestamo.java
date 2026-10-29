package com.utng.inventario.modelo;

import java.time.LocalDateTime;

/** Registro de un artículo entregado a un responsable; sin fecha de devolución significa préstamo activo. */
public class Prestamo {
    private int id;
    private int idArticulo;
    private String articuloNombre;
    private String responsableNombre;
    private int cantidad;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;

    public Prestamo(int id, int idArticulo, String articuloNombre, String responsableNombre,
                    int cantidad, LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucion) {
        this.id = id;
        this.idArticulo = idArticulo;
        this.articuloNombre = articuloNombre;
        this.responsableNombre = responsableNombre;
        this.cantidad = cantidad;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    public String getEstado() {
        return estaActivo() ? "Activo" : "Devuelto";
    }

    public int getId() { return id; }
    public int getIdArticulo() { return idArticulo; }
    public String getArticuloNombre() { return articuloNombre; }
    public String getResponsableNombre() { return responsableNombre; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
}
