package org.lasalle.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Thomas
 */
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionMysql {
    Connection conn;

    private static String env(String name, String porDefecto){
        String valor = System.getenv(name);
        return (valor == null || valor.isBlank()) ? porDefecto : valor;
    }

    public Connection open(){
        String host = env("DB_HOST", "127.0.0.1");
        String port = env("DB_PORT", "3306");
        String db_name = env("DB_NAME", "gluco_api");
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "root");
        boolean ssl = Boolean.parseBoolean(env("DB_SSL", "false"));

        String url = "jdbc:mysql://" + host + ":" + port + "/" + db_name;
        String parametros = "?useSSL=" + ssl
                + "&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf-8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url+parametros,user,password);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
