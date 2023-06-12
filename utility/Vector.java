package utility;

import java.awt.Point;

public class Vector {
    private double x;
    private double y;

    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void add(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
    }

    public void sub(Vector vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
    }

    public void scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public static Vector sum(Vector vector1, Vector vector2) {
        Vector sumVector = vector1.clone();
        sumVector.add(vector2);
        return sumVector;
    }

    public static Vector scaled(Vector vector, double scalar) {
        Vector scaledVector = vector.clone();
        scaledVector.scale(scalar);
        return scaledVector;
    }

    public static Vector difference(Vector vector1, Vector vector2) {
        Vector differenceVector = vector1.clone();
        differenceVector.sub(vector2);
        return differenceVector;
    }

    public static double getManhattanDistance(Vector vector1, Vector vector2) {
        Vector differenceVector = Vector.difference(vector1, vector2);
        return Math.abs(differenceVector.getX()) + Math.abs(differenceVector.getY());
    }

    public Point toPoint() {
        return new Point((int) this.x, (int) this.y);
    }

    @Override
    public Vector clone() {
        return new Vector(this.x, this.y);
    }
}
