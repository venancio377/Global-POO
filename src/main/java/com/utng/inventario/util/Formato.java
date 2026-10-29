package com.utng.inventario.util;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatos de moneda y fecha usados en toda la interfaz (siempre en español de México). */
public final class Formato {
    private static final Locale MX = Locale.forLanguageTag("es-MX");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formato() {
    }

    public static String dinero(double valor) {
        return NumberFormat.getCurrencyInstance(MX).format(valor);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "—" : FECHA_HORA.format(fecha);
    }
}
