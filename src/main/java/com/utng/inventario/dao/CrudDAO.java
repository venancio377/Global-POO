package com.utng.inventario.dao;

import java.util.List;

/** Contrato genérico de operaciones CRUD (Create, Read, Update, Delete) del patrón DAO. */
public interface CrudDAO<T, ID> {
    void crear(T entidad) throws DAOException;

    T leerPorId(ID id) throws DAOException;

    List<T> leerTodos() throws DAOException;

    void actualizar(T entidad) throws DAOException;

    void eliminar(ID id) throws DAOException;
}
