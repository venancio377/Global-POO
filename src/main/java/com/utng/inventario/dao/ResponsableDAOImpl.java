package com.utng.inventario.dao;

import com.utng.inventario.db.ConexionBD;
import com.utng.inventario.modelo.Responsable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Consulta el catálogo de responsables. */
public class ResponsableDAOImpl implements ListadoDAO<Responsable> {

    @Override
    public List<Responsable> leerTodos() throws DAOException {
        List<Responsable> lista = new ArrayList<>();
        String sql = "SELECT IdResponsable, Nombre, Correo, Telefono FROM Responsable ORDER BY Nombre";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Responsable(rs.getInt("IdResponsable"), rs.getString("Nombre"),
                        rs.getString("Correo"), rs.getString("Telefono")));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudieron consultar los responsables: " + e.getMessage(), e);
        }
        return lista;
    }
}
