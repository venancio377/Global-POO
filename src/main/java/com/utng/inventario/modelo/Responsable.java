package com.utng.inventario.modelo;

/** Persona encargada de custodiar artículos y de recibir préstamos. Hereda de {@link Persona}. */
public class Responsable extends Persona {
    private String correo;

    public Responsable(int id, String nombre, String correo, String telefono) {
        super(id, nombre, telefono);
        this.correo = correo;
    }

    @Override
    public String getRol() {
        return "Responsable de inventario";
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
