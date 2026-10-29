package com.utng.inventario.dao;

import com.utng.inventario.db.ConexionBD;
import com.utng.inventario.modelo.ResumenInventario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Consultas agregadas para la pantalla de reportes. */
public class ReporteDAO {

    public ResumenInventario obtenerResumen() throws DAOException {
        String sql = "SELECT COUNT(*) AS Total, "
                + "ISNULL(SUM(Cantidad * PrecioUnitario), 0) AS Valor, "
                + "ISNULL(SUM(CASE WHEN Tipo = 'CONSUMIBLE' AND Cantidad <= StockMinimo THEN 1 ELSE 0 END), 0) AS Bajo, "
                + "(SELECT COUNT(*) FROM Prestamo WHERE FechaDevolucion IS NULL) AS Activos "
                + "FROM Articulo";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return new ResumenInventario(rs.getInt("Total"), rs.getBigDecimal("Valor").doubleValue(),
                    rs.getInt("Bajo"), rs.getInt("Activos"));
        } catch (SQLException e) {
            throw new DAOException("No se pudo generar el resumen: " + e.getMessage(), e);
        }
    }
}
