package main.java;

import main.java.algoritms.MyAlgorithm;
import main.java.graph.Graph;
import main.java.graph.Vertex;
import main.java.graphics.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // 중구
        // Graph graph = new Graph("./src/main/resources", 126.96222222222222, 127.02583333333334, 37.54083333333333, 37.56916666666667);
        // 서울시
        // Graph graph = new Graph("./src/main/resources", 126.734086, 127.269311, 37.413294, 37.715133);
        // 경기도
        Graph graph = new Graph("./src/main/resources", 126.956764, 126.956764 + 0.483654, 37.540705, 37.540705 + 0.551279);
        // 전국
        // Graph graph = new Graph("./src/main/resources");

        Random random = new SecureRandom();
        random.setSeed(1234);
        graph.setStopovers(5, random);
        List<Vertex> route = new MyAlgorithm().route(graph);
        // List<Vertex> route = new ArrayList<>();
        // graph.printToFile("./src/main/resources");

        JFrame frame = new JFrame("Algorithm :: Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        GraphPanel graphPanel = new GraphPanel(graph, route);
        JScrollPane scrollPane = new JScrollPane(graphPanel);
        scrollPane.setViewportView(graphPanel);
        frame.add(scrollPane);
        frame.setVisible(true);
    }
}