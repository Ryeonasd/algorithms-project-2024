package main.java.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Graph {
    private  Map<Long, Vertex> vertexMap;
    private List<Vertex> stopovers;

    public Graph() {
        vertexMap = new HashMap<>();
    }

    public Graph(String filepath) {
        vertexMap = new HashMap<>();
        loadFromFiles(filepath);
    }

    public Graph(String filepath, double minLon, double maxLon, double minLat, double maxLat) {
        vertexMap = new HashMap<>();
        loadFromFiles(filepath, minLon, maxLon, minLat, maxLat);
    }

    public List<Vertex> getVertices() {
        return new ArrayList<>(vertexMap.values());
    }

    public void loadFromFiles(String filepath) {
        loadFromFiles(filepath, 124, 132, 33, 43);
    }

    public void loadFromFiles(String filepath, double minLon, double maxLon, double minLat, double maxLat) {
        System.out.println("Loading graph from " + filepath);
        System.out.println("Loading Vertices");
        try (BufferedReader br = new BufferedReader(new FileReader(filepath + "/nodes.csv"))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                long id = Long.parseLong(tokens[0]);
                double lon = Double.parseDouble(tokens[1]);
                double lat = Double.parseDouble(tokens[2]);
                if (lon < minLon || lat < minLat || lon > maxLon || lat > maxLat) continue;

                Vertex vertex = new Vertex(id, lon, lat);
                vertexMap.put(vertex.getId(), vertex);
            }

            System.out.println("Done. Loaded " + vertexMap.size() + " vertices.");

        } catch (IOException e) {
            // System.err.println("Error reading file: " + filepath + "/nodes.csv");
            e.printStackTrace();
        }

        System.out.println("Loading Edges");
        try (BufferedReader br = new BufferedReader(new FileReader(filepath + "/edges.csv"))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                String[] tokens = line.split(",");

                long sourceId = Long.parseLong(tokens[2]);
                long destId = Long.parseLong(tokens[3]);

                Vertex source = vertexMap.get(sourceId);
                Vertex destination = vertexMap.get(destId);
                if (source == null || destination == null) continue;

                double length = Double.parseDouble(tokens[4]);
                switch (tokens[6]) {
                    case "Motorway" -> source.addEdge(new Edge(destination, length / 100.0));
                    case "Trunk" -> source.addEdge(new Edge(destination, length / 90.0));
                    case "Primary" -> source.addEdge(new Edge(destination, length / 80.0));
                    case "Secondary" -> source.addEdge(new Edge(destination, length / 80.0));
                    case "Tertiary" -> source.addEdge(new Edge(destination, length / 60.0));
                    case "Residential" -> source.addEdge(new Edge(destination, length / 50.0));
                }

                switch (tokens[7]) {
                    case "Motorway" -> destination.addEdge(new Edge(source, length / 100.0));
                    case "Trunk" -> destination.addEdge(new Edge(source, length / 90.0));
                    case "Primary" -> destination.addEdge(new Edge(source, length / 80.0));
                    case "Secondary" -> destination.addEdge(new Edge(source, length / 80.0));
                    case "Tertiary" -> destination.addEdge(new Edge(source, length / 60.0));
                    case "Residential" -> destination.addEdge(new Edge(source, length / 50.0));
                }
            }

            vertexMap.values().removeIf(v -> v.getEdges().isEmpty());
            long count = vertexMap.values().stream().mapToLong(v -> v.getEdges().size()).sum();
            System.out.println("Done. Loaded " + count + " edges.");
            System.out.println("Removed lonely vertices: " + vertexMap.size() + " left.");
        } catch (IOException e) {
            // System.err.println("Error reading file: " + filepath + "/edges.csv");
            e.printStackTrace();
        }

//        System.out.println("Distinguishing subgraphs");
//        List<Vertex> vertices = new LinkedList<>(vertexMap.values());
//        int subgraphNumber = 0;
//        while (!vertices.isEmpty()) {
//            Queue<Vertex> open = new LinkedList<>();
//            open.add(vertices.getFirst());
//
//            while (!open.isEmpty()) {
//                Vertex v1 = open.poll();
//                vertices.remove(v1);
//                v1.setSubgraphID(subgraphNumber);
//
//                for (Edge edge : v1.getEdges()) {
//                    Vertex v2 = edge.getDestination();
//                    if (v2.getSubgraphID() == subgraphNumber) { continue; }
//                    open.add(v2);
//                }
//            }
//
//            subgraphNumber++;
//        }
//        System.out.println("Done. Distinguished " + subgraphNumber + " subgraphs.");
    }

    public void setStopovers(int count, Random randomProvider) {
        List<Vertex> vertexList = new ArrayList<>();
        List<Vertex> graphVertices = getVertices();

        while (vertexList.size() < count) {
            Vertex vertex = graphVertices.get(randomProvider.nextInt(graphVertices.size()));
            if (!vertexList.contains(vertex)) vertexList.add(vertex);
        }

        stopovers = vertexList;
    }

    public List<Vertex> getStopovers() { return new ArrayList<>(stopovers); }

    public void printToFile(String filepath) {
        System.out.println("Printing to file " + filepath);
        try (PrintWriter writer = new PrintWriter(filepath + "/out.txt")) {
            vertexMap.forEach((id, vertex) -> {
                writer.println(vertex.toString());
                vertex.getEdges().forEach(e -> writer.println('\t' + e.toString()));
            });

            System.out.println("Done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
