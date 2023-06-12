package utility;

import java.awt.Point;

/**
 * This class represents a 2-dimensional vector. It provides way to combine points in space as well as manipulate Point objects.
 * @author Andrew Chu
 * @version June 2023
 */
public class Vector {
    private double x;
    private double y;

    /**
     * This constructs a new vector at (0, 0).
     */
    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * This constructs a new vector.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This constructs a new vector from a Point object.
     * @param point The point to construct the vector from.
     */
    public Vector(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    /**
     * This gets the x-coordinate of this vector.
     * @return The x-coordinate.
     */
    public double getX() {
        return this.x;
    }

    /**
     * This gets the y-coordinate of this vector.
     * @return The y-coordinate.
     */
    public double getY() {
        return this.y;
    }

    /**
     * This sets the x-coordinate of this vector.
     * @param x The new x-coordinate.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * This sets the y-coordinate of this vector.
     * @param y The new y-coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * This adds the x and y coordinates of another vector to this vector.
     * @param vector The vector to add to this one.
     */
    public void add(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
    }

    /**
     * This subtracts the x and y coordinates of another vector from this vector.
     * @param vector The vector to add to this one.
     */
    public void sub(Vector vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
    }

    /**
     * This scales the x and y coordinates of this vector.
     * @param scalar The value to scale by.
     */
    public void scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    /**
     * This combines the individual x and y coordinates into a new vector.
     * @param vector1 The first vector to sum (a, b).
     * @param vector2 The second vector to sum (c, d).
     * @return The vector containing the sum of both vectors (a+c, b+d).
     */
    public static Vector sum(Vector vector1, Vector vector2) {
        Vector sumVector = vector1.clone();
        sumVector.add(vector2);
        return sumVector;
    }

    /**
     * This creates a scaled version of a vector.
     * @param vector The vector to scale.
     * @param scalar The value to scale the vector by.
     * @return A new scaled vector.
     */
    public static Vector scaled(Vector vector, double scalar) {
        Vector scaledVector = vector.clone();
        scaledVector.scale(scalar);
        return scaledVector;
    }

    /**
     * This creates a vector containing the difference of two vector's x and y coodinates.
     * @param vector1 The first vector (a, b).
     * @param vector2 The second vector (c, d).
     * @return The vector containing the two vector's differences (a-c, b-d).
     */
    public static Vector difference(Vector vector1, Vector vector2) {
        Vector differenceVector = vector1.clone();
        differenceVector.sub(vector2);
        return differenceVector;
    }

    /**
     * This gets the Manhattan distance between two vectors. Or, the sum of the difference in their x and y coordinates.
     * @param vector1 The first vector (a, b)
     * @param vector2 The second vector (c, d)
     * @return The distance between them |a-c| + |b-d|
     */
    public static double getManhattanDistance(Vector vector1, Vector vector2) {
        Vector differenceVector = Vector.difference(vector1, vector2);
        return Math.abs(differenceVector.getX()) + Math.abs(differenceVector.getY());
    }

    /**
     * This converts this vector into a Point object.
     * @return A new point object with the same coordinates as this vector.
     */
    public Point toPoint() {
        return new Point((int) this.x, (int) this.y);
    }

    /**
     * This creates a copy of this vector.
     * @return The copied vector.
     */
    @Override
    public Vector clone() {
        return new Vector(this.x, this.y);
    }
}
