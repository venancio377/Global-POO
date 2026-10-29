package com.utng.inventario.modelo;

/**
 * Clase abstracta base para cualquier persona del sistema. Cada subclase define su rol
 * ({@link #getRol()}), lo que permite tratarlas de forma uniforme (polimorfismo).
 */
public abstract class Persona {
    private int id;
    private String nombre;
    private String telefono;

    protected Persona(int id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public abstract String getRol();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return nombre;
    }
}
