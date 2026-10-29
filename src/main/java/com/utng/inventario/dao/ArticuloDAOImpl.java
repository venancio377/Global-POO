package com.utng.inventario.dao;

import com.utng.inventario.db.ConexionBD;
import com.utng.inventario.modelo.Area;
import com.utng.inventario.modelo.Articulo;
import com.utng.inventario.modelo.ArticuloFactory;
import com.utng.inventario.modelo.Responsable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC del CRUD de artículos; usa {@code PreparedStatement} contra inyección SQL. */
public class ArticuloDAOImpl implements ArticuloDAO {

    private static final String SELECT_BASE =
            "SELECT a.IdArticulo, a.Codigo, a.Nombre, a.Descripcion, a.Tipo, a.Cantidad, a.StockMinimo, "
                    + "a.PrecioUnitario, a.IdArea, ar.Nombre AS AreaNombre, ar.Ubicacion, "
                    + "a.IdResponsable, r.Nombre AS RespNombre, r.Correo, r.Telefono "
                    + "FROM Articulo a "
                    + "JOIN Area ar ON ar.IdArea = a.IdArea "
                    + "JOIN Responsable r ON r.IdResponsable = a.IdResponsable ";

    @Override
    public void crear(Articulo a) throws DAOException {
        String sql = "INSERT INTO Articulo (Codigo, Nombre, Descripcion, Tipo, Cantidad, StockMinimo, "
                + "PrecioUnitario, IdArea, IdResponsable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(ps, a);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    a.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw traducir("No se pudo registrar el artículo", e);
        }
    }

    @Override
    public Articulo leerPorId(Integer id) throws DAOException {
        try (PreparedStatement ps = conexion().prepareStatement(SELECT_BASE + "WHERE a.IdArticulo = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw traducir("No se pudo consultar el artículo", e);
        }
    }

    @Override
    public List<Articulo> leerTodos() throws DAOException {
        return consultar(SELECT_BASE + "ORDER BY a.Codigo", null);
    }

    @Override
    public List<Articulo> buscar(String texto) throws DAOException {
        String patron = "%" + texto.trim() + "%";
        return consultar(SELECT_BASE
                + "WHERE a.Codigo LIKE ? OR a.Nombre LIKE ? OR ar.Nombre LIKE ? OR r.Nombre LIKE ? "
                + "ORDER BY a.Codigo", patron);
    }

    @Override
    public List<Articulo> leerStockBajo() throws DAOException {
        return consultar(SELECT_BASE
                + "WHERE a.Tipo = 'CONSUMIBLE' AND a.Cantidad <= a.StockMinimo ORDER BY a.Cantidad", null);
    }

    @Override
    public void actualizar(Articulo a) throws DAOException {
        String sql = "UPDATE Articulo SET Codigo = ?, Nombre = ?, Descripcion = ?, Tipo = ?, Cantidad = ?, "
                + "StockMinimo = ?, PrecioUnitario = ?, IdArea = ?, IdResponsable = ? WHERE IdArticulo = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            asignarParametros(ps, a);
            ps.setInt(10, a.getId());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("El artículo ya no existe en la base de datos");
            }
        } catch (SQLException e) {
            throw traducir("No se pudo actualizar el artículo", e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM Articulo WHERE IdArticulo = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new DAOException("El artículo ya no existe en la base de datos");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new DAOException("No se puede eliminar: el artículo tiene préstamos registrados", e);
            }
            throw traducir("No se pudo eliminar el artículo", e);
        }
    }

    private List<Articulo> consultar(String sql, String patron) throws DAOException {
        List<Articulo> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            if (patron != null) {
                for (int i = 1; i <= 4; i++) {
                    ps.setString(i, patron);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw traducir("No se pudo consultar el inventario", e);
        }
        return lista;
    }

    private void asignarParametros(PreparedStatement ps, Articulo a) throws SQLException {
        ps.setString(1, a.getCodigo());
        ps.setString(2, a.getNombre());
        ps.setString(3, a.getDescripcion());
        ps.setString(4, a.getTipo().name());
        ps.setInt(5, a.getCantidad());
        ps.setInt(6, a.getStockMinimo());
        ps.setBigDecimal(7, java.math.BigDecimal.valueOf(a.getPrecioUnitario()));
        ps.setInt(8, a.getArea().getId());
        ps.setInt(9, a.getResponsable().getId());
    }

    private Articulo mapear(ResultSet rs) throws SQLException {
        Area area = new Area(rs.getInt("IdArea"), rs.getString("AreaNombre"), rs.getString("Ubicacion"));
        Responsable resp = new Responsable(rs.getInt("IdResponsable"), rs.getString("RespNombre"),
                rs.getString("Correo"), rs.getString("Telefono"));
        return ArticuloFactory.crear(rs.getString("Tipo"), rs.getInt("IdArticulo"), rs.getString("Codigo"),
                rs.getString("Nombre"), rs.getString("Descripcion"), rs.getInt("Cantidad"),
                rs.getInt("StockMinimo"), rs.getBigDecimal("PrecioUnitario").doubleValue(), area, resp);
    }

    private Connection conexion() throws SQLException {
        return ConexionBD.getInstancia().getConexion();
    }

    private DAOException traducir(String mensaje, SQLException e) {
        if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
            return new DAOException("Ya existe un artículo con ese código", e);
        }
        return new DAOException(mensaje + ": " + e.getMessage(), e);
    }
}
