package main.java.graph;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    private final long id;
    private int subgraphID;
    private final double longitude;
    private final double latitude;
    private final List<Edge> edges;

    public Vertex(long id, double longitude, double latitude) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.edges = new ArrayList<Edge>();
    }

    public long getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getSubgraphID() {
        return subgraphID;
    }

    public void setSubgraphID(int subgraphID) {
        this.subgraphID = subgraphID;
    }

    @Override
    public String toString() {
        return "Vertex " + id + ": (" + latitude + ", " + longitude + "), in subgraph " + subgraphID;
    }
}
