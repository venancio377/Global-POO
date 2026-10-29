package com.utng.inventario.dao;

import com.utng.inventario.db.ConexionBD;
import com.utng.inventario.modelo.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Consulta, registra y cierra préstamos; las operaciones que cambian existencia usan transacciones. */
public class PrestamoDAO implements ListadoDAO<Prestamo> {

    @Override
    public List<Prestamo> leerTodos() throws DAOException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.IdPrestamo, p.IdArticulo, a.Nombre AS ArticuloNombre, r.Nombre AS RespNombre, "
                + "p.Cantidad, p.FechaPrestamo, p.FechaDevolucion "
                + "FROM Prestamo p JOIN Articulo a ON a.IdArticulo = p.IdArticulo "
                + "JOIN Responsable r ON r.IdResponsable = p.IdResponsable ORDER BY p.IdPrestamo DESC";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp dev = rs.getTimestamp("FechaDevolucion");
                lista.add(new Prestamo(rs.getInt("IdPrestamo"), rs.getInt("IdArticulo"),
                        rs.getString("ArticuloNombre"), rs.getString("RespNombre"), rs.getInt("Cantidad"),
                        rs.getTimestamp("FechaPrestamo").toLocalDateTime(),
                        dev == null ? null : dev.toLocalDateTime()));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudieron consultar los préstamos: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Registra el préstamo y descuenta la existencia en una sola transacción. */
    public void prestar(int idArticulo, int idResponsable, int cantidad) throws DAOException {
        Connection c = null;
        boolean autoCommit = true;
        try {
            c = ConexionBD.getInstancia().getConexion();
            autoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            int existencia;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT Cantidad FROM Articulo WITH (UPDLOCK) WHERE IdArticulo = ?")) {
                ps.setInt(1, idArticulo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DAOException("El artículo no existe");
                    }
                    existencia = rs.getInt(1);
                }
            }
            if (existencia < cantidad) {
                throw new DAOException("Existencia insuficiente: solo hay " + existencia + " disponible(s)");
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE Articulo SET Cantidad = Cantidad - ? WHERE IdArticulo = ?")) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idArticulo);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Prestamo (IdArticulo, IdResponsable, Cantidad) VALUES (?, ?, ?)")) {
                ps.setInt(1, idArticulo);
                ps.setInt(2, idResponsable);
                ps.setInt(3, cantidad);
                ps.executeUpdate();
            }
            c.commit();
        } catch (SQLException e) {
            revertir(c);
            throw new DAOException("No se pudo registrar el préstamo: " + e.getMessage(), e);
        } catch (DAOException e) {
            revertir(c);
            throw e;
        } finally {
            restaurar(c, autoCommit);
        }
    }

    /** Marca el préstamo como devuelto y regresa la existencia al inventario. */
    public void devolver(int idPrestamo) throws DAOException {
        Connection c = null;
        boolean autoCommit = true;
        try {
            c = ConexionBD.getInstancia().getConexion();
            autoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            int idArticulo;
            int cantidad;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT IdArticulo, Cantidad, FechaDevolucion FROM Prestamo WITH (UPDLOCK) WHERE IdPrestamo = ?")) {
                ps.setInt(1, idPrestamo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DAOException("El préstamo no existe");
                    }
                    if (rs.getTimestamp("FechaDevolucion") != null) {
                        throw new DAOException("Este préstamo ya fue devuelto");
                    }
                    idArticulo = rs.getInt("IdArticulo");
                    cantidad = rs.getInt("Cantidad");
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE Prestamo SET FechaDevolucion = GETDATE() WHERE IdPrestamo = ?")) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE Articulo SET Cantidad = Cantidad + ? WHERE IdArticulo = ?")) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idArticulo);
                ps.executeUpdate();
            }
            c.commit();
        } catch (SQLException e) {
            revertir(c);
            throw new DAOException("No se pudo registrar la devolución: " + e.getMessage(), e);
        } catch (DAOException e) {
            revertir(c);
            throw e;
        } finally {
            restaurar(c, autoCommit);
        }
    }

    private void revertir(Connection c) {
        try {
            if (c != null) {
                c.rollback();
            }
        } catch (SQLException ignorada) {
            System.err.println("Error al revertir la transacción: " + ignorada.getMessage());
        }
    }

    private void restaurar(Connection c, boolean autoCommit) {
        try {
            if (c != null) {
                c.setAutoCommit(autoCommit);
            }
        } catch (SQLException ignorada) {
            System.err.println("Error al restaurar la conexión: " + ignorada.getMessage());
        }
    }
}
