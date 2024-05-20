import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private Map<Vertex, List<Edge>> adjList;

    public Graph() {
        adjList = new HashMap<>();
    }

    public void addVertex(Vertex v) {
        adjList.putIfAbsent(v, new ArrayList<>());
    }

    public void removeVertex(Vertex v) {}

    public void addEdge(Vertex source, Vertex destination, double weight) {
        List<Edge> edges = adjList.get(source);
        edges.add(new Edge(destination, weight));
    }

    public void removeEdge(Edge e) {}

    public void loadFromFiles(String filepath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath + "/nodes.csv"))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
