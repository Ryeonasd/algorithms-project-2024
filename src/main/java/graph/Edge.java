package main.java.graph;

public class Edge {
    private final Vertex destination;
    private final double weight;

    public Edge(Vertex destination, double weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge to " + destination.getId() + ": " + weight;
    }
}
