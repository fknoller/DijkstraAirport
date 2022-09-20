package view;

import model.Airport;
import model.AirportDAO;
import model.WeightedGraph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws SQLException {
        //**import data from SQL and create graph in memory**
        AirportDAO DAO = new AirportDAO();
        ResultSet table = DAO.getAllAirports();
        WeightedGraph graph = new WeightedGraph();
        while(table.next()) {
            Airport airport = new Airport(table.getString("iata"), table.getString("name"),
                    table.getString("city"), table.getString("state"),
                    table.getDouble("latitude"), table.getDouble("longitude"));
            graph.addAirport(airport);
        }
        for(Airport src : graph.getAirports()) {
            for(Airport dest : graph.getAirports()) {
                if(src.distance(dest) != 0)
                    graph.addPath(src,dest, src.distance(dest));
            }
        }
        //**import data from SQL and create graph in memory**

        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to do an airport search? (Y/N)");
        String city, state, iata1, iata2;
        while(sc.nextLine().equalsIgnoreCase("Y")) {
            //prints states list
            HashMap<String, Integer> hash = new HashMap<>();
            for(Airport airport : graph.getAirports()) {
                if(!hash.containsKey(airport.getState())) {
                    System.out.println(airport.getState());
                    hash.put(airport.getState(), 1);
                }
            }
            System.out.print("Please select the state from the list above: ");
            state = sc.nextLine();
            //prints cities list
            for(Airport airport : graph.getAirports()) {
                if(airport.getState().equalsIgnoreCase(state) && !hash.containsKey(airport.getCity())) {
                    System.out.println(airport.getCity());
                    hash.put(airport.getCity(), 1);
                }
            }
            System.out.print("Now, please select the city from the list above: ");
            city = sc.nextLine();
            //prints airport list
            for(Airport airport : graph.getAirports()) {
                if(airport.getCity().equalsIgnoreCase(city) && airport.getState().equalsIgnoreCase(state))
                    System.out.println(airport.getIata() + " - " + airport.getName());
            }
            System.out.println();
            System.out.println("Would you like to do another search? (Y/N)");
        }

        System.out.println();
        System.out.print("Please enter the IATA code (3 letters) of the source airport: ");
        iata1 = sc.nextLine();
        System.out.print("Please enter the IATA code (3 letters) of the destination airport: ");
        iata2 = sc.nextLine();

    }
}