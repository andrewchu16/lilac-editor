public class RelativeVector extends Vector {
    private Vector base;
    private Vector relative; 

    public RelativeVector() {
        this.base = new Vector();
        this.relative = new Vector();
    }

    public RelativeVector(double x, double y) {
        this.base = new Vector();
        this.relative = new Vector(x, y);
    }

    public RelativeVector(Vector base, Vector relative) {
        this.base = base;
        this.relative = relative;
    }

    @Override
    public double getX() {
        return this.getBaseX() + this.getRelX();
    }

    @Override
    public double getY() {
        return this.getBaseY() + this.getRelY();
    }

    @Override
    public void setX(double x) {
        this.relative.setX(this.getRelX() + x - this.getX());
    }

    @Override
    public void setY(double y) {
        this.relative.setY(this.getRelY() + y - this.getY());
    }

    @Override
    public void add(Vector vector) {
        this.relative.add(vector);
    }

    @Override
    public void scale(double scalar) {
        this.relative.scale(scalar);
    }

    public double getBaseX() {
        return this.base.getX();
    }

    public double getBaseY() {
        return this.base.getY();
    }

    public double getRelX() {
        return this.relative.getX();
    }

    public double getRelY() {
        return this.relative.getY();
    }

    public void setBase(Vector base) {
        this.base = base;
    }

    public void setRelative(Vector relative) {
        this.relative = relative;
    }

    public Vector getBase() {
        return this.base;
    }

    public Vector getRelative() {
        return this.relative;
    }

    public Vector getVector() {
        return Vector.sum(this.base, this.relative);
    }

    @Override
    public RelativeVector clone() {
        return new RelativeVector(this.base, this.relative);
    }
}
