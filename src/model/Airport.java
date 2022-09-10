package model;

public class Airport {
    private String iata, name, city, state;
    private double latitude, longitude;

    //custom constructor
    public Airport(String iata, String name, String city, String state, double latitude, double longitude) {
        this.iata = iata;
        this.name = name;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //function to convert degrees to radians
    public double degToRad(double deg) {
        return deg*Math.PI/180;
    }

    public double distance(Airport airport) {
        double dLat = degToRad(airport.latitude - this.latitude);
        double dLon = degToRad(airport.longitude - this.longitude);
        double r = 6378;

        //haversine formula
        double h = Math.pow(Math.sin(dLat/2), 2) + Math.pow(Math.sin(dLon/2), 2)*Math.cos(degToRad(this.latitude))*Math.cos(degToRad(airport.latitude));

        return 2*r*Math.asin(Math.sqrt(h));
    }
}
