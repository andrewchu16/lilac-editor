package diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import utility.Vector;

public class Arrow extends JComponent {
    private Diagram startDiagram;
    private Diagram endDiagram;
    private ArrayList<Point> points;
    private Point pos;
    private Dimension size;

    public static final int DEFAULT_LINE_THICKNESS = 2;

    public Arrow(Diagram startDiagram, Diagram endDiagram) {
        this.startDiagram = startDiagram;
        this.endDiagram = endDiagram;
        this.points = new ArrayList<Point>();
        this.pos = new Point();
        this.size = new Dimension();
        this.setLayout(null);

        this.calculatePoints();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.points.size() == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setStroke(new BasicStroke(DEFAULT_LINE_THICKNESS));

        for (int i = 1; i < this.points.size(); i++) {
            Point startPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(i - 1), this);
            Point endPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(i), this);

            g2.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }

        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        for (int i = 1; i < this.points.size(); i++) {
            Point startPoint = this.points.get(i - 1);
            Point endPoint = this.points.get(i);

            boolean withinX = false;
            boolean withinY = false;
            if (startPoint.x == endPoint.x) {
                // Vertical line segment.
                withinX = Math.abs(x - startPoint.x) <= DEFAULT_LINE_THICKNESS;
                withinY = (startPoint.y <= y && y <= endPoint.y) ||
                        (endPoint.y <= y && y <= startPoint.y);
            } else {
                // Horizontal line segment.
                withinX = (startPoint.x <= x && x <= endPoint.x) ||
                        (endPoint.x <= x && x <= startPoint.x);
                withinY = Math.abs(y - startPoint.y) <= DEFAULT_LINE_THICKNESS;
            }

            if (withinX && withinY) {
                return true;
            }
        }

        return false;
    }

    public void calculatePoints() {
        // Determine the starting and ending point of the arrow.
        Point[] startMountPoints = this.startDiagram.getArrowMountPoints();
        Point[] endMountPoints = this.endDiagram.getArrowMountPoints();

        int bestStartPointIndex = 0;
        int bestEndPointIndex = 0;
        int bestDistance = Integer.MAX_VALUE;
        for (int startPointIndex = 0; startPointIndex < startMountPoints.length; startPointIndex++) {
            for (int endPointIndex = 0; endPointIndex < endMountPoints.length; endPointIndex++) {
                int curDistance = getDistance(startMountPoints[startPointIndex], endMountPoints[endPointIndex]);
                
                if (curDistance <= bestDistance) {
                    bestStartPointIndex = startPointIndex;
                    bestEndPointIndex = endPointIndex;
                    bestDistance = curDistance;
                }
            }
        }
        
        Point startMountPoint = startMountPoints[bestStartPointIndex];
        Point endMountPoint = endMountPoints[bestEndPointIndex];

        this.points.add(startMountPoint);
        // Create intermediate points.
        if (bestStartPointIndex % 2 == bestEndPointIndex % 2) {
            // Create a 2-point Z-shaped line when going to opposite sides.
            if (bestStartPointIndex == 0 || bestEndPointIndex == 0) {
                // top to bottom line.
                int y = (startMountPoint.y + endMountPoint.y) / 2;
                this.points.add(new Point(startMountPoint.x, y));
                this.points.add(new Point(endMountPoint.x, y));
            } else {
                // left to right line.
                int x = (startMountPoint.x + endMountPoint.x) / 2;
                this.points.add(new Point(x, startMountPoint.y));
                this.points.add(new Point(x, endMountPoint.y));
            }
        } else {
            // Create a 1-point L-shaped line when going to adjacent sides.
            if (bestStartPointIndex % 2 == 0) {
                // Vertical to horizontal.
                this.points.add(new Point(startMountPoint.x, endMountPoint.y));
            } else {
                // Horizontal to vertical.
                this.points.add(new Point(endMountPoint.x, startMountPoint.y));
            }
        }
        this.points.add(endMountPoint);

        this.calculateBounds();
    }

    private void calculateBounds() {
        // Update position and size.
        this.pos = (Point) this.points.get(0).clone();
        Point corner = (Point) this.pos.clone();

        for (int i = 1; i < this.points.size(); i++) {
            Point curPoint = this.points.get(i);
            this.pos.x = Math.min(this.pos.x, curPoint.x);
            this.pos.y = Math.min(this.pos.y, curPoint.y);
            corner.x = Math.max(corner.x, curPoint.x);
            corner.y = Math.max(corner.y, curPoint.y);
        }

        this.size.setSize(corner.x - this.pos.x, corner.y - this.pos.y);
        this.setSize(this.size);
        this.setPreferredSize(this.size);
        this.setBounds(this.pos.x, this.pos.y, (int) this.size.getWidth(), (int) this.size.getHeight());
    }

    private static int getDistance(Point point1, Point point2) {
        return (int) Vector.getManhattanDistance(new Vector(point1), new Vector(point2));
    }

    public Diagram getStartDiagram() {
        return this.startDiagram;
    }

    public Diagram getEndDiagram() {
        return this.endDiagram;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Arrow) {
            return ((Arrow) other).getStartDiagram() == this.startDiagram &&
                    ((Arrow) other).getEndDiagram() == this.endDiagram;
        }

        return false;
    }
}
