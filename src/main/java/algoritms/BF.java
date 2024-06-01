package main.java.algoritms;

import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import java.util.*;

public class BF implements RoutingStrategy {
    private static double minDist = Double.MAX_VALUE;
    private static ArrayList<Integer> bestRoute = new ArrayList<>();
    private static int check1 = 0;
    private static int check2 = 0;  // check2 = (stopovers.size() - 2)!, 2 = 시작점, 끝점

    @Override
    public List<Vertex> route(Graph graph) {
        long endTime, duration, startTime = System.currentTimeMillis();
        List<Vertex> stopovers = graph.getStopovers();
        List<Vertex> route = new ArrayList<>();
        List<Vertex> betweenPath;
        Vertex vertex1, vertex2;
        ArrayList<Integer> vertices = new ArrayList<>();
        ArrayList<Integer> initialRoute = new ArrayList<>();
        double[][] adjMat = setCost(stopovers, stopovers.size()); // 인접 행렬 형태로 만듦
        int idx1, idx2;
        int start = 0;
        int end = stopovers.size() - 1;

        for (int i = 1; i < adjMat.length - 1; i++) { // 시작과 끝 빼고 삽입
            vertices.add(i);
        }
        initialRoute.add(start);
        permute(adjMat, initialRoute, vertices);  // n!
        bestRoute.add(end); // 끝점 추가
        for (int i = 0; i < bestRoute.size() - 1; ++i) {    // 끝점 전까지 반복
            idx1 = bestRoute.get(i);    // 선행 인덱스
            idx2 = bestRoute.get(i + 1);    // 후행 인덱스
            vertex1 = stopovers.get(idx1);   // 선행 경유지
            vertex2 = stopovers.get(idx2);   // 후행 경유지
            betweenPath = AStar(vertex1, vertex2);   // 경유지 사이의 경로
            route.addAll(betweenPath); // 경로를 반환값에 저장
        }
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        System.out.println("BF take " + duration + "ms and total route weight is " + RoutingStrategy.getRouteCost(route));
        return route;
    }

    public double calculateRouteDistance(double[][] adjMat, ArrayList<Integer> currRoute) {   // 경로의 총 거리를 계산
        double distance = 0;
        for (int i = 0; i < currRoute.size() - 1; i++) {
            distance = distance + adjMat[currRoute.get(i)][currRoute.get(i + 1)];
        }
        return distance;
    }

    // 인접 행렬, 경로, 경유지를 전달
    public void permute(double[][] adjMat, ArrayList<Integer> currentRoute, ArrayList<Integer> remainingVertices) {
        if (remainingVertices.isEmpty()) {
            ++check2;
            double distance = calculateRouteDistance(adjMat, currentRoute);
            if (distance < minDist) {   // 현재 거리가 최단 거리보다 작은 경우, 갱신
                minDist = distance; // 최단 거리 갱신
                bestRoute = new ArrayList<>(currentRoute);  // 최단 경로 갱신
                ++check1;
            }
            return;
        }

        for (int i = 0; i < remainingVertices.size(); i++) {
            ArrayList<Integer> newRoute = new ArrayList<>(currentRoute);    // 기존의 경로를 추가하며, 새 목록 생성
            ArrayList<Integer> newRemaining = new ArrayList<>(remainingVertices);   // 기존의 경유지를 추가하며, 새 경유지 목록 생성

            newRoute.add(remainingVertices.get(i)); // 새로운 경유지 삽입
            newRemaining.remove(i); // 삽입된 경유지는 새 경유지 목록에서 제거

            permute(adjMat, newRoute, newRemaining);  // 새 경로, 새 경유지 목록을 사용해 재귀호출
        }
         /*
        재귀적으로 가능한 모든 순열을 생성
        시작점과 끝점을 제외한 모든 경유지의 순열을 만듦
        이 함수는 재귀적으로 호출되며, 각 호출에서는 현재 경로와 남은 도시 목록을 인자로 받음
        더 이상 방문할 도시가 없을 때, 현재 경로의 거리를 계산하고 최단 경로인지 확인한 후, 최단 경로인 경우 업데이트함
         */
    }

    public double[][] setCost(List<Vertex> stopovers, int size) { // 모든 정점에 대한 가중치를 구하고, 인접 행렬 형태로 반환
        List<Vertex> oneRoute;
        double[][] cost = new double[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i == j) {
                    cost[i][j] = 0;
                } else {
                    oneRoute = AStar(stopovers.get(i), stopovers.get(j));
                    cost[i][j] = RoutingStrategy.getRouteCost(oneRoute);
                }
            }
        }
        return cost;
    }

    public List<Vertex> AStar(Vertex start, Vertex goal) {  // A* 알고리즘을 이용해 경유지 사이의 간선들을 구함
        if (start == goal) return null;

        PriorityQueue<Entry2> open = new PriorityQueue<>();
        List<Entry2> closed = new LinkedList<>();
        open.add(new Entry2(start));

        while (!open.isEmpty()) {
            Entry2 parent = open.poll();
            if (parent.vertex == goal) return parent.track();

            for (Edge edge : parent.vertex.getEdges()) {
                Entry2 successor = new Entry2(edge, parent, goal);

                boolean skipFlag = false;
                for (Entry2 e : open) {
                    if (e.vertex == successor.vertex && e.f < successor.f) {
                        skipFlag = true;
                        break;
                    }
                }
                if (skipFlag) continue;
                for (Entry2 e : closed) {
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

class Entry2 implements Comparable<Entry2> {
    Entry2 parent;
    Vertex vertex;
    double g;
    double f;

    public Entry2(Vertex vertex) {
        this.vertex = vertex;
        this.g = 0;
        this.f = 0;
    }

    public Entry2(Edge edge, Entry2 parent, Vertex goal) {
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
        Entry2 currentEntry2 = this;

        while (currentEntry2.parent != null) {
            reversePath.add(currentEntry2.vertex);
            currentEntry2 = currentEntry2.parent;
        }
        reversePath.add(currentEntry2.vertex);

        return reversePath.reversed();
    }

    @Override
    public int compareTo(Entry2 o) {
        return Double.compare(f, o.f);
    }
}