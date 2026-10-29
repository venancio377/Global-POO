package com.utng.inventario.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.utng.inventario.modelo.Area;
import com.utng.inventario.modelo.Articulo;
import com.utng.inventario.modelo.ArticuloFactory;
import com.utng.inventario.modelo.Responsable;
import com.utng.inventario.modelo.TipoArticulo;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExportadorCSVTest {

    @Test
    void escapaComillasYNeutralizaFormulas() {
        assertEquals("\"Caja \"\"grande\"\"\"", ExportadorCSV.celda("Caja \"grande\""));
        assertEquals("\"'=SUMA(A1)\"", ExportadorCSV.celda("=SUMA(A1)"));
    }

    @Test
    void generaEncabezadoYUnaFilaPorArticulo(@TempDir Path carpeta) throws Exception {
        Articulo a = ArticuloFactory.crear(TipoArticulo.EQUIPO, 1, "EQ-001", "Computadora", "", 2, 0, 14500,
                new Area(1, "Laboratorio", "B"), new Responsable(1, "Ana", "a@utng.edu.mx", ""));
        Path archivo = carpeta.resolve("inv.csv");
        ExportadorCSV.exportar(List.of(a), archivo);
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        assertEquals(2, lineas.size());
        assertTrue(lineas.get(1).contains("\"EQ-001\""));
        assertTrue(lineas.get(1).contains("29000.00"));
    }
}
