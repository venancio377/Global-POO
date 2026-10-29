package com.utng.inventario.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ArticuloTest {

    private final Area area = new Area(1, "Taller de Redes", "Edificio B");
    private final Responsable resp = new Responsable(1, "José Luis Ramírez", "jl@utng.edu.mx", "4181000002");

    private Articulo consumible(int cantidad, int minimo) {
        return ArticuloFactory.crear(TipoArticulo.CONSUMIBLE, 0, "CO-001", "Cable UTP", "", cantidad, minimo, 12.5, area, resp);
    }

    @Test
    void calculaElValorTotal() {
        assertEquals(125.0, consumible(10, 2).calcularValorTotal(), 0.001);
    }

    @Test
    void polimorfismoEnElEstadoDelStock() {
        assertEquals("Suficiente", consumible(50, 10).getEstadoStock());
        assertEquals("Reabastecer", consumible(10, 10).getEstadoStock());
        assertEquals("Agotado", consumible(0, 10).getEstadoStock());
        Articulo equipo = ArticuloFactory.crear(TipoArticulo.EQUIPO, 0, "EQ-001", "Proyector", "", 0, 0, 9800, area, resp);
        assertEquals("Sin existencia", equipo.getEstadoStock());
    }

    @Test
    void laFabricaDevuelveLaSubclaseCorrecta() {
        assertInstanceOf(EquipoComputo.class,
                ArticuloFactory.crear("EQUIPO", 0, "EQ-002", "Monitor", "", 1, 0, 2800, area, resp));
        assertInstanceOf(MaterialConsumible.class, consumible(1, 1));
    }

    @Test
    void rechazaTiposDesconocidos() {
        assertThrows(IllegalArgumentException.class,
                () -> ArticuloFactory.crear("OTRO", 0, "X-1", "Algo", "", 1, 0, 1, area, resp));
    }

    @Test
    void elEncapsulamientoImpideDatosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> consumible(-1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> ArticuloFactory.crear(TipoArticulo.EQUIPO, 0, " ", "Monitor", "", 1, 0, 1, area, resp));
        Articulo a = consumible(5, 1);
        assertThrows(IllegalArgumentException.class, () -> a.setPrecioUnitario(-3));
        assertThrows(IllegalArgumentException.class, () -> a.setCantidad(-1));
        assertEquals(5, a.getCantidad());
    }
}
