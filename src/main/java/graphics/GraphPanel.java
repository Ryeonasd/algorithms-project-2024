package main.java.graphics;

import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GraphPanel extends JPanel {
    private List<Vertex> vertices;
    private List<Vertex> stopovers;
    private List<Vertex> route;
    private double radius;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale_factor;

    public GraphPanel(Graph graph, List<Vertex> route) {
        this.vertices = graph.getVertices();
        this.stopovers = graph.getStopovers();
        this.route = route;
        this.radius = 3;

        minX = vertices.stream().map(Vertex::getLongitude).min(Comparator.naturalOrder()).orElse(0d);
        maxX = vertices.stream().map(Vertex::getLongitude).max(Comparator.naturalOrder()).orElse(0d);
        minY = vertices.stream().map(Vertex::getLatitude).min(Comparator.naturalOrder()).orElse(0d);
        maxY = vertices.stream().map(Vertex::getLatitude).max(Comparator.naturalOrder()).orElse(0d);

        scale_factor = 5000;

        MouseAdapter ma = new MouseAdapter() {
            Point origin;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scale_factor -= 100 * e.getPreciseWheelRotation();
                repaint();
            }
        };

        addMouseWheelListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.GRAY);
        for (Vertex vertex : vertices) {
            drawVertex(vertex, g2d);

            for (Edge edge : vertex.getEdges()) {
                drawLine(vertex, edge.getDestination(), g2d);
            }
        }

        if (route.isEmpty()) { return; }
        g2d.setStroke(new BasicStroke(2));

        ArrayList<Vertex> stopovers = new ArrayList<>(this.stopovers);

        g2d.setColor(Color.ORANGE);
        Vertex currentVertex = route.get(0);
        for (int i = 1; i < route.size(); i++) {
            Vertex nextVertex = route.get(i);

            for (Edge edge : currentVertex.getEdges()) {
                if (edge.getDestination().equals(nextVertex)) {
                    drawLine(currentVertex, edge.getDestination(), g2d);
                    break;
                }
            }

            currentVertex = nextVertex;
        }

        g2d.setColor(Color.GREEN);
        fillVertex(stopovers.removeFirst(), g2d);
        g2d.setColor(Color.RED);
        fillVertex(stopovers.removeLast(), g2d);
        g2d.setColor(Color.BLUE);
        for (Vertex vertex : stopovers) {
            fillVertex(vertex, g2d);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) ((maxX - minX)*scale_factor), (int) ((maxY - minY)*scale_factor));
    }

    private void drawVertex(Vertex v, Graphics2D g2d) {
        g2d.draw(new Ellipse2D.Double(getCenterX(v) - radius, getCenterY(v) - radius, radius*2, radius*2));
    }

    private void fillVertex(Vertex v, Graphics2D g2d) {
        g2d.fill(new Ellipse2D.Double(getCenterX(v) - radius, getCenterY(v) - radius, radius*2, radius*2));
    }

    private void drawLine(Vertex v1, Vertex v2, Graphics2D g2d) {
        double v1x = getCenterX(v1);
        double v1y = getCenterY(v1);
        double v2x = getCenterX(v2);
        double v2y = getCenterY(v2);
        double slope = (v2y - v1y) / (v2x - v1x);
        double denominator = Math.sqrt(1 + slope * slope);
        double offsetX = radius/3 * slope / denominator;
        double offsetY = -radius/3 / denominator;
        if (v1x < v2x) {
            offsetX = -offsetX;
            offsetY = -offsetY;
        }

        g2d.draw(new Line2D.Double(v1x + offsetX, v1y + offsetY, v2x + offsetX, v2y + offsetY));
    }

    private double getCenterX(Vertex v) {
        return (v.getLongitude() - minX) * scale_factor;
    }

    private double getCenterY(Vertex v) {
        return (maxY - v.getLatitude()) * scale_factor;
    }
}
