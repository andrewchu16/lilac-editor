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

    public void scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public static Vector sum(Vector vector1, Vector vector2) {
        Vector sumVector = vector1.clone();
        sumVector.add(vector2);

        return sumVector;
    }

    @Override
    public Vector clone() {
        return new Vector(this.x, this.y);
    }
}
