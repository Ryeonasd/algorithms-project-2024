package main.java.algoritms;

import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import java.util.*;

public class DP implements RoutingStrategy {
    @Override
    public List<Vertex> route(Graph graph) {
        List<Vertex> stopovers = graph.getStopovers();
        List<Vertex> route = new ArrayList<>();

        Vertex s = stopovers.removeFirst(); // stopovers에서 가장 처음 -> 출발지
        Vertex e = stopovers.removeLast(); // stopovers에서 가장 마지막 -> 도착지

        Vertex currentVertex = s;
        route.add(currentVertex);

        while (!stopovers.isEmpty()) {
            Node next = SetNext(stopovers, currentVertex); // 다음 노드 설정

            List<Vertex> path = dijkstra(currentVertex, next.vertex); // 다익스트라로 최단 경로 계산
            if (path == null) {
                System.out.println("경로를 찾을 수 없습니다.");
                return Collections.emptyList();
            } else {
//                System.out.println("Path found. " + path);
            }
            route.addAll(path.subList(1, path.size())); // 첫 번째 경유지는 이미 포함되어 있으므로 제외
            stopovers.remove(next.vertex);
            currentVertex = next.vertex; // 현재 위치 업데이트

        }

        List<Vertex> finalPath = dijkstra(currentVertex, e); // 마지막 경유지에서 도착지까지 경로 계산
        if (finalPath == null) {
            System.out.println("경로를 찾을 수 없습니다.");
            return Collections.emptyList();
        }
        route.addAll(finalPath.subList(1, finalPath.size()));
        System.out.println("Full route: ");
        for (Vertex ver : route) {
            System.out.println(ver);
        }
        return route;
    }

    private Node SetNext(List<Vertex> list, Vertex now) { // 인자에 stopovers 경유지.
        PriorityQueue<Node> pq = new PriorityQueue<>(); // pq에 vertex를 저장하고, 뽑아내면서 Node 생성?
        for (Vertex ver : list) {
            double dis = Math.sqrt(Math.pow(ver.getLongitude() - now.getLongitude(), 2) + Math.pow(ver.getLatitude() - now.getLatitude(), 2)) * 6378135;
            Node temp = new Node(ver, dis);
            pq.add(temp);
        }
        return pq.poll();
    }

    private List<Vertex> dijkstra(Vertex start, Vertex goal) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, Vertex> previous = new HashMap<>();
        pq.add(new Node(start));
        distances.put(start, 0.0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            Vertex currentVertex = current.vertex;

            if (currentVertex.equals(goal)) break;

            for (Edge edge : currentVertex.getEdges()) {
                Vertex neighbor = edge.getDestination();
                double newDist = distances.get(currentVertex) + edge.getWeight();

                if (newDist < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentVertex);
                    pq.add(new Node(neighbor, newDist));
                }
            }
        }

        List<Vertex> path = new ArrayList<>();
        for (Vertex at = goal; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.size() == 1 && !path.get(0).equals(goal) ? null : path; // 경로를 찾지 못한 경우 null 반환
    }
}

class Node implements Comparable<Node> {
    Node parent; // 어느 노드에서 왔는지
    Vertex vertex; // 정점
    double distance; // 거리 총합

    public Node (Vertex vertex) { // 시작 정점 초기화. 시작 정점이므로 거리는 당연히 0
        this.vertex = vertex;
        this.distance = 0;
    }

    public Node (Vertex vertex, double distance) {
        this.vertex = vertex;
        this.distance = distance;
    }
    public Node (Edge edge, Node parent, Vertex goal) {
        this.vertex = edge.getDestination(); // 다음 목적지 vertex 반환
        this.parent = parent;
        this.distance = parent.distance + edge.getWeight(); // 거리값은 parent distance에 현재 간선 가중치 더함.
    }

    @Override
    public int compareTo(Node node) {
        return Double.compare(distance, node.distance);
    }
}