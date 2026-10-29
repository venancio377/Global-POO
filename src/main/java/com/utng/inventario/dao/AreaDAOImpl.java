package com.utng.inventario.dao;

import com.utng.inventario.db.ConexionBD;
import com.utng.inventario.modelo.Area;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Consulta el catálogo de áreas. */
public class AreaDAOImpl implements ListadoDAO<Area> {

    @Override
    public List<Area> leerTodos() throws DAOException {
        List<Area> lista = new ArrayList<>();
        String sql = "SELECT IdArea, Nombre, Ubicacion FROM Area ORDER BY Nombre";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Area(rs.getInt("IdArea"), rs.getString("Nombre"), rs.getString("Ubicacion")));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudieron consultar las áreas: " + e.getMessage(), e);
        }
        return lista;
    }
}
