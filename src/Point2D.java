/**
 * @author Stel-l
 */
public class Point2D {
    public float x;
    public float y;

    public Point2D() {
        x = 0;
        y = 0;
    }

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }
}
