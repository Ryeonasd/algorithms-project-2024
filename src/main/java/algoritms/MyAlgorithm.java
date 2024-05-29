package main.java.algoritms;

import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import java.util.*;

public class MyAlgorithm implements RoutingStrategy {

    @Override
    public List<Vertex> route(Graph graph) {
        System.out.println("MyAlgorithm: Starting route");
        long startTime = System.currentTimeMillis();

        List<Vertex> stopovers = graph.getStopovers();

        List<Vertex> route = new ArrayList<>();

        // 계산
//        Map<Vertex, Map<Vertex, List<Vertex>>> sToSPath = new HashMap<>();
//        for (Vertex start : stopovers) {
//            Map<Vertex, List<Vertex>> toSPath = new HashMap<>();
//            for (Vertex goal : stopovers) {
//                System.out.println("Finding path from " + start + " to " + goal);
//                long time = System.currentTimeMillis();
//                List<Vertex> path = AStar(start, goal);
//                if (path != null) {
//                    System.out.println("Path found. Time: " + (System.currentTimeMillis() - time));
//                }
//                else {
//                    System.out.println("Path not found. Time: " + (System.currentTimeMillis() - time));
//                }
//                toSPath.put(goal, path);
//            }
//            sToSPath.put(start, toSPath);
//        }

        Vertex start = stopovers.removeFirst();
        Vertex goal = stopovers.removeLast();

        Vertex v1 = start;
        while (!stopovers.isEmpty()) {
            double minDistance = Double.MAX_VALUE;
            Vertex v2 = null;

            for (Vertex stopover : stopovers) {
                double distance = Math.sqrt(Math.pow(v1.getLongitude() - stopover.getLongitude(), 2)
                        + Math.pow(v1.getLatitude() - stopover.getLatitude(), 2))
                        * 6378135;
                if (distance < minDistance) {
                    minDistance = distance;
                    v2 = stopover;
                }
            }

            route.addAll(AStar(v1, v2));
            stopovers.remove(v2);
            v1 = v2;
        }
        route.addAll(AStar(v1, goal));

        System.out.println("MyAlgorithm: Finished route. time: " + (System.currentTimeMillis() - startTime) + "ms");
        return route;
    }

    private List<Vertex> AStar(Vertex start, Vertex goal) {
        if (start == goal) return null;

        PriorityQueue<Entry> open = new PriorityQueue<>();
        List<Entry> closed = new LinkedList<>();
        open.add(new Entry(start));

        while (!open.isEmpty()) {
            Entry parent = open.poll();
            if (parent.vertex == goal) return parent.track();

            for (Edge edge : parent.vertex.getEdges()) {
                Entry successor = new Entry(edge, parent, goal);

                boolean skipFlag = false;
                for (Entry e : open) {
                    if (e.vertex == successor.vertex && e.f < successor.f) {
                        skipFlag = true;
                        break;
                    }
                }
                if (skipFlag) continue;
                for (Entry e : closed) {
                    if (e.vertex == successor.vertex && e.f < successor.f) {
                        skipFlag = true;
                        break;
                    }
                }
                if (skipFlag) continue;
                open.add(successor);
            }
            closed.add(parent);
        }
        return new ArrayList<>();
    }
}

class Entry implements Comparable<Entry> {
    Entry parent;
    Vertex vertex;
    double g;
    double f;

    public Entry(Vertex vertex) {
        this.vertex = vertex;
        this.g = 0;
        this.f = 0;
    }

    public Entry(Edge edge, Entry parent, Vertex goal) {
        this.vertex = edge.getDestination();
        this.parent = parent;
        this.g = parent.g + edge.getWeight();
        this.f = g + h(goal);
    }

    private double h(Vertex goal) {
        return Math.sqrt(Math.pow(vertex.getLongitude() - goal.getLongitude(), 2)
                        + Math.pow(vertex.getLatitude() - goal.getLatitude(), 2))
                * 6378135 / 110;
    }

    public List<Vertex> track() {
        List<Vertex> reversePath = new ArrayList<>();
        Entry currentEntry = this;

        while (currentEntry.parent != null) {
            reversePath.add(currentEntry.vertex);
            currentEntry = currentEntry.parent;
        }
        reversePath.add(currentEntry.vertex);

        return reversePath.reversed();
    }

    @Override
    public int compareTo(Entry o) {
        return Double.compare(f, o.f);
    }
}