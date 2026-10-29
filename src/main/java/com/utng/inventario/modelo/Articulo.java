package com.utng.inventario.modelo;

/**
 * Clase abstracta que representa cualquier artículo del inventario universitario.
 * <p>
 * Aplica <b>abstracción</b> (define lo común y deja a las subclases lo que cambia) y
 * <b>encapsulamiento</b> (atributos privados; los setters validan los datos para que un
 * objeto nunca quede en un estado inválido).
 * Las subclases {@link EquipoComputo} y {@link MaterialConsumible} implementan
 * {@link #getTipo()} y {@link #getEstadoStock()} de forma distinta (<b>polimorfismo</b>).
 */
public abstract class Articulo {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private int stockMinimo;
    private double precioUnitario;
    private Area area;
    private Responsable responsable;

    /**
     * @param id             identificador en la base de datos (0 si aún no se guarda)
     * @param codigo         código único del artículo, por ejemplo {@code EQ-001}
     * @param nombre         nombre corto
     * @param descripcion    detalle opcional
     * @param cantidad       existencia actual (no negativa)
     * @param stockMinimo    nivel a partir del cual un consumible debe reabastecerse
     * @param precioUnitario precio por pieza (no negativo)
     * @param area           área donde se encuentra
     * @param responsable    persona a cargo
     * @throws IllegalArgumentException si algún dato no cumple las reglas de negocio
     */
    protected Articulo(int id, String codigo, String nombre, String descripcion, int cantidad,
                       int stockMinimo, double precioUnitario, Area area, Responsable responsable) {
        this.id = id;
        this.codigo = validarTexto(codigo, "El código");
        this.nombre = validarTexto(nombre, "El nombre");
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.cantidad = validarNoNegativo(cantidad, "La cantidad");
        this.stockMinimo = validarNoNegativo(stockMinimo, "El stock mínimo");
        this.precioUnitario = validarPrecio(precioUnitario);
        this.area = validarNoNulo(area, "El área");
        this.responsable = validarNoNulo(responsable, "El responsable");
    }

    /** Tipo del artículo; es el valor que se guarda en la columna Tipo de la base de datos. */
    public abstract TipoArticulo getTipo();

    /** Estado del stock; cada tipo de artículo lo evalúa con su propia regla (polimorfismo). */
    public abstract String getEstadoStock();

    /** @return existencia multiplicada por el precio unitario */
    public double calcularValorTotal() {
        return cantidad * precioUnitario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = validarTexto(codigo, "El código"); }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = validarTexto(nombre, "El nombre"); }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion == null ? "" : descripcion.trim(); }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = validarNoNegativo(cantidad, "La cantidad"); }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = validarNoNegativo(stockMinimo, "El stock mínimo"); }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = validarPrecio(precioUnitario); }
    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = validarNoNulo(area, "El área"); }
    public Responsable getResponsable() { return responsable; }
    public void setResponsable(Responsable responsable) { this.responsable = validarNoNulo(responsable, "El responsable"); }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        return valor.trim();
    }

    private static int validarNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo");
        }
        return valor;
    }

    private static double validarPrecio(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new IllegalArgumentException("El precio debe ser un número mayor o igual a cero");
        }
        return valor;
    }

    private static <T> T validarNoNulo(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        return valor;
    }
}
