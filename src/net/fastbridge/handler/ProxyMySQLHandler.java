package net.fastbridge.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProxyMySQLHandler {

    public static Connection connection;
    static ProxyMySQLHandler instance;

    public static ProxyMySQLHandler getInstance() {
        if (instance == null) instance = new ProxyMySQLHandler();
        return instance;
    }

    public void connect() {
        if (connection != null) return;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + "IP" + ":" + "3306" + "/" + "Proxy" + "?autoReconnect=true", "user", "pw");
            System.out.println("Die Verbindung mit der MySQL Datenbank wurde erfolgreich hergestellt");
        } catch (SQLException e) {
            System.out.println("Die Verbindung mit der MySQL Datenbank ist fehlgeschlagen du hurensohn: ");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}