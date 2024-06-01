package main.java.algoritms;

import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Greedy implements RoutingStrategy {
    @Override
    public List<Vertex> route(Graph graph) {
        long startTime = System.currentTimeMillis();
        long endTime;
        List<Vertex> stopovers = graph.getStopovers();
        List<Vertex> route = new ArrayList<>();
        Vertex start = stopovers.removeFirst(); // 시작점
        Vertex end = stopovers.removeLast();    // 끝점
        Vertex standard = start;

        while(!stopovers.isEmpty()){
            standard = getNearby(standard, stopovers, route);
        }
        route.addAll(AStar(standard, end));
        endTime = System.currentTimeMillis() - startTime;
        System.out.println("Time: " + endTime + "ms");
        return route;
    }

    public Vertex getNearby(Vertex standard,List<Vertex> stopovers, List<Vertex> route) {
        Vertex near;
        double minDistance = Double.MAX_VALUE;
        double compareDistance;
        List<Vertex> single;
        int index = 0;

        for(int i = 0; i < stopovers.size(); ++i) {
            near = stopovers.get(i);
            single = AStar(standard, near);
            compareDistance = RoutingStrategy.getRouteCost(single);
            if(minDistance > compareDistance){
                index = i;
                minDistance = compareDistance;
            }
        }
        near = stopovers.get(index);
        single = AStar(standard, near);
        route.addAll(single);
        stopovers.remove(near);
        return near;
    }

    public List<Vertex> AStar(Vertex start, Vertex goal) {
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
