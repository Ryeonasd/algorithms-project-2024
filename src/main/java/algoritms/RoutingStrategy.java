package main.java.algoritms;

import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import java.util.List;

public interface RoutingStrategy {

    List<Vertex> route(Graph graph);

    static double getRouteCost(List<Vertex> route) {
        double cost = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            for (Edge edge : route.get(i).getEdges()) {
                if (edge.getDestination() == route.get(i+1)) {
                    cost += edge.getWeight();
                    break;
                }
            }
        }
        return cost;
    }
}
