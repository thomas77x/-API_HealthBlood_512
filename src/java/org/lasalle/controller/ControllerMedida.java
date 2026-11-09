package org.lasalle.controller;

import org.lasalle.model.Medida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ControllerMedida {

    public List<Medida> getMedidas() throws SQLException {
        List<Medida> lista = new ArrayList<>();

        String sqlQuery = "SELECT * FROM medida";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        try (conn;
             PreparedStatement pst = conn.prepareStatement(sqlQuery);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lista.add(fillMedida(rs));
            }
        }
        return lista;
    }

    public List<Medida> getMedidasByUsuario(int idUsuario) throws SQLException {
        List<Medida> lista = new ArrayList<>();

        String sqlQuery = "SELECT * FROM medida WHERE idUsuario = ? ORDER BY fechaHora DESC";
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        try (conn;
             PreparedStatement pst = conn.prepareStatement(sqlQuery)) {

            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(fillMedida(rs));
                }
            }
        }
        return lista;
    }

    public int insertarMedida(Medida medida) throws SQLException {
        String sql = "INSERT INTO medida (idUsuario, glucosa, fechaHora, contexto, notas) "
                   + "VALUES (?, ?, ?, ?, ?)";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No se pudo conectar a la base de datos");
        }

        try (conn;
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp fecha = (medida.getFechaHora() == null || medida.getFechaHora().isBlank())
                    ? new Timestamp(System.currentTimeMillis())
                    : Timestamp.valueOf(medida.getFechaHora());

            pst.setInt(1, medida.getIdUsuario());
            pst.setInt(2, medida.getGlucosa());
            pst.setTimestamp(3, fecha);
            pst.setString(4, medida.getContexto());
            pst.setString(5, medida.getNotas());

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

    public Medida fillMedida(ResultSet rs) throws SQLException {
        Medida medida = new Medida();
        medida.setIdMedida(rs.getInt("idMedida"));
        medida.setIdUsuario(rs.getInt("idUsuario"));
        medida.setGlucosa(rs.getInt("glucosa"));
        medida.setFechaHora(rs.getString("fechaHora"));
        medida.setContexto(rs.getString("contexto"));
        medida.setNotas(rs.getString("notas"));
        return medida;
    }

}
