package model;

import java.util.*;

public class WeightedGraph {
    private HashMap<Airport, LinkedList<Pair>> adj = new HashMap<>();

    static class Pair implements Comparable<Pair>{
        Airport airport;
        double weight;

        public Pair(Airport airport, double weight) {
            this.airport = airport;
            this.weight = weight;
        }

        @Override
        public int compareTo(Pair p) {
            if(this.weight < p.weight)
                return -1;
            else if(this.weight > p.weight)
                return 1;
            return 0;
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

    public void dijkstra(Airport src, Airport dest) {
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        HashMap<Airport, Double> distance = new HashMap<>();
        HashMap<Airport, Airport> parent = new HashMap<>();

        for(Airport airport : adj.keySet())
            distance.put(airport, Double.POSITIVE_INFINITY);

        pq.add(new Pair(src, 0));
        distance.put(src, 0.0);

        while(!pq.isEmpty()) {
            Airport currentAirport = pq.peek().airport;
            pq.remove();

            for(Pair pair : adj.get(currentAirport)) {
                double weight = pair.weight;
                Airport child = pair.airport;

                if(child.getIata().equals(dest.getIata()) && currentAirport.getIata().equals(src.getIata()))
                    continue;

                if(distance.get(child) > distance.get(currentAirport) + weight) {
                    distance.put(child, distance.get(currentAirport) + weight);
                    pq.add(new Pair(child, distance.get(child)));
                    parent.put(child, currentAirport);
                }
            }
        }
        System.out.println("Shortest path length: " + distance.get(dest) +
                " with a connecting flight in " + parent.get(dest).getIata());
    }
}