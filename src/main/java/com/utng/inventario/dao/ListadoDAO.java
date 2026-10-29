package com.utng.inventario.dao;

import java.util.List;

/** Contrato para catálogos de solo lectura (áreas, responsables, préstamos). */
public interface ListadoDAO<T> {
    List<T> leerTodos() throws DAOException;
}
