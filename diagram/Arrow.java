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
    private Stroke stroke;
    private int endStyle;

    public static final int DEFAULT_LINE_THICKNESS = 3;
    public static final Stroke SOLID = new BasicStroke(DEFAULT_LINE_THICKNESS);
    public static final Stroke DASHED = new BasicStroke(DEFAULT_LINE_THICKNESS, 
        BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0);

    public static final int ARROW_END = 1;
    public static final int TRIANGLE_END = 2;
    public static final int FILL_DIAMOND_END = 3;
    public static final int LINE_DIAMOND_END = 4;

    public Arrow(Diagram startDiagram, Diagram endDiagram, Stroke stroke, int endStyle) {
        super();
        this.startDiagram = startDiagram;
        this.endDiagram = endDiagram;
        this.stroke = stroke;
        this.endStyle = endStyle;
        this.points = new ArrayList<Point>();
        this.pos = new Point();
        this.size = new Dimension();
        this.setLayout(null);
        this.calculatePoints();
    }

    public Arrow(Diagram startDiagram, Diagram endDiagram) {
        this(startDiagram, endDiagram, SOLID, ARROW_END);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.points.size() == 0) {
            return;
        }

        // Draw the line.
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setStroke(this.stroke);
        for (int i = 1; i < this.points.size(); i++) {
            Point startPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(i - 1), this);
            Point endPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(i), this);
            g2.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }

        this.drawEndArrow(g2);

        g2.dispose();
    }

    public void drawEndArrow(Graphics2D g2) {
        // Get the points of the end arrow.
        Point[] arrowPoints = new Point[0];
        switch (this.endStyle) {
            case TRIANGLE_END:
                arrowPoints = new Point[3];
                arrowPoints[0] = new Point(-12, -8);
                arrowPoints[1] = new Point(0, 0);
                arrowPoints[2] = new Point(-12, 8);
                break;
            case FILL_DIAMOND_END:
                arrowPoints = new Point[4];
                arrowPoints[0] = new Point(-12, -8);
                arrowPoints[1] = new Point(0, 0);
                arrowPoints[2] = new Point(-12, 8);
                arrowPoints[3] = new Point(-12, 0);
            case LINE_DIAMOND_END:
                break;
            case ARROW_END:
            default:
                arrowPoints = new Point[3];
                arrowPoints[0] = new Point(-12, -8);
                arrowPoints[1] = new Point(0, 0);
                arrowPoints[2] = new Point(-12, 8);
                break;
        }

        // Rotate the end arrow.
        Point beforeEndPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(this.points.size() - 2), this);
        Point endPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(this.points.size() - 1), this);
        double offs = -Math.PI;
        double angle = Math.atan2(endPoint.y - beforeEndPoint.y, endPoint.x - beforeEndPoint.x);
        
        for (int i = 0; i < arrowPoints.length; i++) {
            double length = Math.hypot(arrowPoints[i].x, arrowPoints[i].y) + 90;
            arrowPoints[i].x = (int) (length * Math.cos(angle - offs));
            arrowPoints[i].y = (int) (length * Math.sin(angle - offs));
        }

        // Draw the end arrow.
        g2.setStroke(SOLID);
        int[] xPoints = new int[arrowPoints.length];
        int[] yPoints = new int[arrowPoints.length];
        for (int i = 0; i < arrowPoints.length; i++) {
            xPoints[i] = endPoint.x + arrowPoints[i].x;
            yPoints[i] = endPoint.y + arrowPoints[i].y;
        }

        if (this.endStyle == ARROW_END) {
            g2.drawPolyline(xPoints, yPoints, arrowPoints.length);
        } else if (this.endStyle == TRIANGLE_END || this.endStyle == LINE_DIAMOND_END) {
            g2.drawPolygon(xPoints, yPoints, arrowPoints.length);
        } else {
            g2.fillPolygon(xPoints, yPoints, arrowPoints.length);
        }
    }

    @Override
    public boolean contains(int x, int y) {
        final int THRESHOLD = 40;

        for (int i = 1; i < this.points.size(); i++) {
            Point startPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(i - 1), this);
            Point endPoint = SwingUtilities.convertPoint(this.getParent(), this.points.get(i), this);

            boolean withinX = false;
            boolean withinY = false;
            if (startPoint.x == endPoint.x) {
                // Vertical line segment.
                withinX = Math.abs(x - startPoint.x) <= DEFAULT_LINE_THICKNESS + THRESHOLD;
                withinY = (startPoint.y - THRESHOLD <= y && y <= endPoint.y + THRESHOLD) ||
                        (endPoint.y - THRESHOLD <= y && y <= startPoint.y + THRESHOLD);
            } else {
                // Horizontal line segment.
                withinX = (startPoint.x - THRESHOLD <= x && x <= endPoint.x + THRESHOLD) ||
                        (endPoint.x - THRESHOLD <= x && x <= startPoint.x + THRESHOLD);
                withinY = Math.abs(y - startPoint.y) <= DEFAULT_LINE_THICKNESS + THRESHOLD;
            }

            if (withinX && withinY) {
                return true;
            }
        }

        return false;
    }

    public void calculatePoints() {
        // Determine the starting and ending point of the arrow that gives the smallest distance.
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

        this.points.clear();
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
