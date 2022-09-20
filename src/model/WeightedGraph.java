package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class WeightedGraph {
    private HashMap<Airport, LinkedList<Pair>> adj = new HashMap<>();

    static class Pair {
        Airport airport;
        double weight;

        public Pair(Airport airport, double weight) {
            this.airport = airport;
            this.weight = weight;
        }
    }

    public void addAirport(Airport dest) {
        adj.put(dest, new LinkedList<>());
    }

    public void addPath(Airport a, Airport b, double weight) {
        if(!adj.containsKey(a))
            addAirport(a);
        if(!adj.containsKey(b))
            addAirport(b);
        adj.get(a).add(new Pair(b, weight));
        adj.get(b).add(new Pair(a, weight));
    }

    public Set<Airport> getAirports() {
        return adj.keySet();
    }
}