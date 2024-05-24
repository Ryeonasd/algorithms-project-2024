package main.java.algoritms;

import main.java.graph.Graph;
import main.java.graph.Vertex;

import java.util.List;

public interface RoutingStrategy {

    List<Vertex> route(Graph graph);
}
