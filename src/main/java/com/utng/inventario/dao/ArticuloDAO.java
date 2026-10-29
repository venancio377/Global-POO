package com.utng.inventario.dao;

import com.utng.inventario.modelo.Articulo;

import java.util.List;

/** CRUD de artículos más búsqueda y consulta de stock bajo. */
public interface ArticuloDAO extends CrudDAO<Articulo, Integer> {
    List<Articulo> buscar(String texto) throws DAOException;

    List<Articulo> leerStockBajo() throws DAOException;
}
