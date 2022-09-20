package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//****Java class that defines the function responsible for connecting and integrating the software with MySQL****

public class Connect {
    private String server, db, user, password;
    private int port;

    public Connect(int status) {
        if(status == 0) {
            this.server = "127.0.0.1";
            this.db = "airport";
            this.user = "root";
            this.password = "pass123";
            this.port = 3306;
        }
    }

    public Connection connect() throws SQLException {
        String url = "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.db;
        return DriverManager.getConnection(url, this.user, this.password);
    }
}