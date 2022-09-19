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
}