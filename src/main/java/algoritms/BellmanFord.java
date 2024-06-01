package main.java.algoritms;

import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import java.util.*;

public class BellmanFord implements RoutingStrategy {
    @Override
    public List<Vertex> route(Graph graph) {
        long endTime, duration, startTime = System.currentTimeMillis();
        List<Vertex> stopovers = graph.getStopovers(); // 그래프에서 경유지 목록을 가져옴
        List<Vertex> route = new ArrayList<>(); // 최종 경로를 저장할 리스트
        Vertex start = stopovers.getFirst(); // 시작점
        Vertex end = stopovers.getLast(); // 끝점

        route.add(start); // 경로에 시작점을 추가
        Set<Vertex> visited = new HashSet<>(); // 방문한 정점을 추적할 집합
        visited.add(start); // 시작점을 방문했다고 표시

        Vertex current = start;
        while (visited.size() < stopovers.size() - 1) { // 모든 경유지를 방문할 때까지 반복 (끝점 제외)
            // 다음 방문할 경유지를 찾음
            Vertex next = getNextStopover(current, stopovers, visited); // 다음 경유지
            if (next == null) {
                throw new IllegalStateException("No valid next stopover found");
            }
            List<Vertex> shortestPath = bellmanFord(graph, current, next); // 현재 위치에서 다음 경유지까지의 최단 경로를 찾음
            if (shortestPath.isEmpty()) {
                throw new IllegalStateException("No path found between " + current + " and " + next);
            }

            route.addAll(shortestPath.subList(1, shortestPath.size())); // 경로에 다음 경유지 추가 (중복 방지를 위해 첫 번째 정점을 제외하고 추가)
            visited.add(next); // 다음 경유지를 방문했다고 표시
            current = next; // 현재 위치를 업데이트
        }
        List<Vertex> shortestPathToEnd = bellmanFord(graph, current, end); // 마지막으로 끝점을 방문
        if (shortestPathToEnd.isEmpty()) {
            throw new IllegalStateException("No path found between " + current + " and " + end);
        }
        route.addAll(shortestPathToEnd.subList(1, shortestPathToEnd.size())); // 경로에 끝점을 추가 (중복 방지를 위해 첫 번째 정점을 제외하고 추가)

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        System.out.println("BellmanFord take " + duration + "ms and total route weight is " + RoutingStrategy.getRouteCost(route));
        return route; // 최종 경로 반환
    }

    // 다음 방문할 경유지를 찾는 메서드
    private Vertex getNextStopover(Vertex current, List<Vertex> stopovers, Set<Vertex> visited) {
        Vertex closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Vertex stopover : stopovers) { // 모든 경유지 중에서 방문하지 않은 정점 찾기 (끝점 제외)
            if (!visited.contains(stopover) && !stopover.equals(stopovers.getLast())) {
                double distance = calculateDistance(current, stopover);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest = stopover;
                }
            }
        }

        return closest; // 가장 가까운 경유지를 반환
    }

    // 두 정점 사이의 유클리드 거리를 계산하는 메서드, 유클리드 거리: 두 점 사이의 직선 거리
    private double calculateDistance(Vertex v1, Vertex v2) {
        double latDiff = v1.getLatitude() - v2.getLatitude();
        double lonDiff = v1.getLongitude() - v2.getLongitude();
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }

    // Bellman-Ford 알고리즘을 사용하여 최단 경로를 찾는 메서드
    private List<Vertex> bellmanFord(Graph graph, Vertex source, Vertex target) {
        Map<Vertex, Double> distances = new HashMap<>(); // 정점까지의 최단 거리를 저장할 맵
        Map<Vertex, Vertex> predecessors = new HashMap<>(); // 최단 경로 트리를 저장할 맵
        // 초기화 단계
        /* 모든 정점의 거리를 무한대로 설정하고, 시작점(source)의 거리를 0으로 설정.
           이 초기화 단계는 벨만-포드 알고리즘의 기본적인 시작 단계임*/
        for (Vertex vertex : graph.getVertices()) {
            distances.put(vertex, Double.MAX_VALUE);
            predecessors.put(vertex, null);
        }
        distances.put(source, 0.0); // 시작점까지의 거리는 0으로 설정
        // 간선 완화 단계
        /* 이 부분은 그래프의 모든 간선을 verticesCount - 1번 반복하며 완화하는 과정
           각 정점에 대해 연결된 간선들을 순회하며 최단 거리를 업데이트
           이 과정이 벨만-포드 알고리즘의 핵심, verticesCount - 1번의 반복을 통해 최단 거리가 안정화되도록 함*/
        int verticesCount = graph.getVertices().size(); // 모든 정점의 개수
        for (int i = 0; i < verticesCount - 1; i++) { // 모든 간선에 대해 반복 (정점 수 - 1 번 반복)
            for (Vertex vertex : graph.getVertices()) {
                Double currentDist = distances.get(vertex);
                if (currentDist == null || currentDist == Double.MAX_VALUE) continue; // 도달할 수 없는 정점은 무시
                for (Edge edge : vertex.getEdges()) { // 모든 간선을 검사하여 최단 거리를 업데이트
                    Vertex destination = edge.getDestination();
                    double newDist = currentDist + edge.getWeight();
                    Double destinationDist = distances.get(destination);
                    if (destinationDist == null || newDist < destinationDist) { // 값이 없거나, 새로운 값이 더 작은 경우
                        distances.put(destination, newDist);    // 갱신
                        predecessors.put(destination, vertex);
                    }
                }
            }
        }
        // 최단 경로 재구성 단계
        /* 타겟 정점에서부터 시작하여 이전 정점을 따라가며 경로를 재구성
           이 과정에서 최단 경로를 올바르게 구성하고, 이를 역순으로 정렬하여 시작점에서 타겟까지의 경로를 반환*/
        List<Vertex> path = new ArrayList<>();
        Vertex step = target;
        if (predecessors.get(step) == null) {
            return path; // 경로가 존재하지 않으면 빈 경로 반환
        }
        path.add(step);
        while ((step = predecessors.get(step)) != null) {
            path.add(step);
        }
        Collections.reverse(path); // 경로를 역순으로 정렬
        return path;
    }
}
