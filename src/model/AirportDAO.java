package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.Connect;

public class AirportDAO {
    private Connection database;

    public AirportDAO() throws SQLException {
        database = (new Connect(0)).connect();
    }

    public ResultSet getAllAirports() throws SQLException {
        String stmt = "SELECT * FROM airportData ORDER BY 1;";
        PreparedStatement pStmt = database.prepareStatement(stmt);
        return pStmt.executeQuery();
    }

    public ResultSet getAirportsFrom(String city, String state) throws SQLException {
        String stmt = "SELECT iata, name FROM airportData WHERE city = ? AND state = ? ORDER BY 1;";
        PreparedStatement pStmt = database.prepareStatement(stmt);
        pStmt.setString(1, city);
        pStmt.setString(2, state);
        return pStmt.executeQuery();
    }
}