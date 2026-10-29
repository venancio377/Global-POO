package com.utng.inventario.util;

import com.utng.inventario.modelo.Articulo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/** Genera un archivo CSV del inventario que Excel abre directamente (UTF-8 con BOM). */
public final class ExportadorCSV {

    private ExportadorCSV() {
    }

    public static void exportar(List<Articulo> articulos, Path destino) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            w.write('﻿');
            w.write("Código,Nombre,Tipo,Cantidad,Stock mínimo,Precio unitario,Valor total,Área,Responsable,Estado");
            w.newLine();
            for (Articulo a : articulos) {
                w.write(String.join(",",
                        celda(a.getCodigo()), celda(a.getNombre()), celda(a.getTipo().getEtiqueta()),
                        String.valueOf(a.getCantidad()), String.valueOf(a.getStockMinimo()),
                        String.format(Locale.US, "%.2f", a.getPrecioUnitario()),
                        String.format(Locale.US, "%.2f", a.calcularValorTotal()),
                        celda(a.getArea().getNombre()), celda(a.getResponsable().getNombre()),
                        celda(a.getEstadoStock())));
                w.newLine();
            }
        }
    }

    /** Escapa comillas y comas, y neutraliza fórmulas ({@code =}, {@code +}, {@code -}, {@code @}). */
    static String celda(String texto) {
        String t = texto == null ? "" : texto;
        if (!t.isEmpty() && "=+-@".indexOf(t.charAt(0)) >= 0) {
            t = "'" + t;
        }
        return "\"" + t.replace("\"", "\"\"") + "\"";
    }
}
