package com.utng.inventario.dao;

import com.utng.inventario.db.ConexionBD;
import com.utng.inventario.modelo.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Autenticación y administración de usuarios (tabla Usuario).
 * Regla de negocio: existe un solo ADMIN, creado por el script SQL; desde la aplicación
 * solo se agregan y se eliminan técnicos. La base lo refuerza con un índice único filtrado.
 */
public class UsuarioDAO {

    /** Devuelve el usuario si las credenciales son correctas; null en caso contrario. */
    public Usuario autenticar(String nombreUsuario, String password) throws DAOException {
        String sql = "SELECT IdUsuario, NombreUsuario, NombreCompleto, Rol FROM Usuario "
                + "WHERE NombreUsuario = ? AND PasswordHash = HASHBYTES('SHA2_256', CAST(? AS NVARCHAR(100)))";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("IdUsuario"), rs.getString("NombreUsuario"),
                            rs.getString("NombreCompleto"), rs.getString("Rol"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo validar el usuario: " + e.getMessage(), e);
        }
    }

    /** Lista todas las cuentas: primero el administrador y luego los técnicos por nombre. */
    public List<Usuario> listar() throws DAOException {
        String sql = "SELECT IdUsuario, NombreUsuario, NombreCompleto, Rol FROM Usuario "
                + "ORDER BY CASE WHEN Rol = 'ADMIN' THEN 0 ELSE 1 END, NombreUsuario";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("IdUsuario"), rs.getString("NombreUsuario"),
                        rs.getString("NombreCompleto"), rs.getString("Rol")));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("No se pudo consultar los usuarios: " + e.getMessage(), e);
        }
    }

    /** Crea un usuario con rol TECNICO. El rol no es parámetro: nadie crea otro administrador. */
    public void crearTecnico(String nombreUsuario, String nombreCompleto, String password) throws DAOException {
        Usuario.validarNuevo(nombreUsuario, nombreCompleto, password);
        String sql = "INSERT INTO Usuario (NombreUsuario, NombreCompleto, PasswordHash, Rol) "
                + "VALUES (?, ?, HASHBYTES('SHA2_256', CAST(? AS NVARCHAR(100))), 'TECNICO')";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, nombreUsuario.trim());
            ps.setString(2, nombreCompleto.trim());
            ps.setString(3, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601 || "23505".equals(e.getSQLState())) {
                throw new DAOException("Ya existe un usuario llamado " + nombreUsuario.trim(), e);
            }
            throw new DAOException("No se pudo crear el usuario: " + e.getMessage(), e);
        }
    }

    /** Cambia la contraseña de cualquier cuenta (la guarda como hash SHA-256). */
    public void cambiarPassword(int idUsuario, String passwordNueva) throws DAOException {
        Usuario.validarPassword(passwordNueva);
        String sql = "UPDATE Usuario SET PasswordHash = HASHBYTES('SHA2_256', CAST(? AS NVARCHAR(100))) "
                + "WHERE IdUsuario = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, passwordNueva);
            ps.setInt(2, idUsuario);
            if (ps.executeUpdate() == 0) {
                throw new DAOException("El usuario ya no existe");
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo cambiar la contraseña: " + e.getMessage(), e);
        }
    }

    /** Elimina un técnico. La condición Rol = 'TECNICO' impide borrar al administrador. */
    public void eliminarTecnico(int idUsuario) throws DAOException {
        String sql = "DELETE FROM Usuario WHERE IdUsuario = ? AND Rol = 'TECNICO'";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            if (ps.executeUpdate() == 0) {
                throw new DAOException("Solo se pueden eliminar técnicos existentes");
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo eliminar el usuario: " + e.getMessage(), e);
        }
    }
}
