package model;

import java.util.*;

public class WeightedGraph {
    private HashMap<Airport, LinkedList<Pair>> adj = new HashMap<>();

    static class Pair {
        Airport airport;
        double weight;

        public Pair(Airport airport, double weight) {
            this.airport = airport;
            this.weight = weight;
        }

        public int compare(Pair p) {
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

    static class Path {
        double dist;
        HashMap<Airport, Airport> path;

        public Path(double dist, HashMap<Airport, Airport> path) {
            this.dist = dist;
            this.path = path;
        }
    }

    public Path dijkstra(Airport src, Airport dest) {
        HashMap<Airport, Airport> path = new HashMap<>();
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        HashMap<Airport, Double> dist = new HashMap<>();

        for(Airport airport : adj.keySet())
            dist.put(airport, Double.POSITIVE_INFINITY);

        pq.add(new Pair(src, 0));
        dist.put(src, 0.0);

        while(!pq.isEmpty()) {
            double u = pq.peek().weight;
            Airport r = pq.peek().airport;
            pq.remove();

            if(dist.get(r) < u)
                continue;
            for(Pair pair : adj.get(r)) {
                double weight = pair.weight;
                Airport child = pair.airport;

                if(dist.get(child) > dist.get(r) + weight) {
                    dist.put(child, dist.get(r) + weight);
                    path.put(child, r);
                    pq.add(new Pair(child, dist.get(child)));
                }
            }
        }

        return new Path(dist.get(dest), path);
    }
}