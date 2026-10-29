package com.utng.inventario.modelo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void aceptaUnTecnicoValido() {
        assertDoesNotThrow(() -> Usuario.validarNuevo("maria.lopez", "María López", "clave123"));
    }

    @Test
    void rechazaUsuarioConEspaciosOMuyCorto() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarNuevo("ab", "María López", "clave123"));
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarNuevo("maria lopez", "María López", "clave123"));
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarNuevo("'; DROP--", "María López", "clave123"));
    }

    @Test
    void rechazaContrasenaCortaONula() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarPassword("12345"));
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarPassword(null));
    }

    @Test
    void soloElRolAdminEsAdministrador() {
        assertTrue(new Usuario(1, "admin", "Admin", Usuario.ROL_ADMIN).esAdministrador());
        assertFalse(new Usuario(2, "tec", "Técnico", Usuario.ROL_TECNICO).esAdministrador());
    }
}
