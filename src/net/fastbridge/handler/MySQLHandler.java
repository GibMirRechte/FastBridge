package net.fastbridge.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLHandler {

    public static Connection connection;
    static MySQLHandler instance;

    public static MySQLHandler getInstance() {
        if (instance == null) instance = new MySQLHandler();
        return instance;
    }

    public void connect() {
        if (connection != null) return;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + "IP" + ":" + "3306" + "/" + "FastBridge" + "?autoReconnect=true", "user", "pw");
            System.out.println("Die Verbindung mit der MySQL Datenbank wurde erfolgreich hergestellt");
        } catch (SQLException e) {
            System.out.println("Die Verbindung mit der MySQL Datenbank ist fehlgeschlagen: ");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}