package org.lasalle.controller;

import org.lasalle.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ControllerUsuario {

    public List<Usuario> getUsuarios() throws SQLException{
        List<Usuario> lista = new ArrayList<>();

        String sqlQuery = "SELECT * FROM usuario";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        try (conn;
             PreparedStatement pst = conn.prepareStatement(sqlQuery);
             ResultSet rs = pst.executeQuery()) {

            while(rs.next()){
                lista.add(fillUsuario(rs));
            }
        }
        return lista;
    }

    public Usuario getUsuarioById(int idUsuario) throws SQLException {
        String sqlQuery = "SELECT * FROM usuario WHERE idUsuario = ?";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        try (conn;
             PreparedStatement pst = conn.prepareStatement(sqlQuery)) {

            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return fillUsuario(rs);
                }
            }
        }
        return null;
    }

    public int insertarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, primerApellido, segundoApellido, email) "
                   + "VALUES (?, ?, ?, ?)";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        try (conn;
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, usuario.getNombre());
            pst.setString(2, usuario.getPrimerApellido());
            pst.setString(3, usuario.getSegundoApellido());
            pst.setString(4, usuario.getEmail());

            int filas = pst.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se insertó ningún registro");
            }

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        }
    }

    public Usuario fillUsuario(ResultSet rs) throws SQLException {
        Usuario user = new Usuario();
        user.setIdUsuario(rs.getInt("idUsuario"));
        user.setNombre(rs.getString("nombre"));
        user.setPrimerApellido(rs.getString("primerApellido"));
        user.setSegundoApellido(rs.getString("segundoApellido"));
        user.setEmail(rs.getString("email"));
        return user;
    }

}
