/**
 * @author Stel-l
 */
public class Matrix3x3 {
    public float[][] data = new float[3][3];

    public Matrix3x3() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                data[i][j] = i == j ? 1 : 0;
            }
        }
    }

    public static Matrix3x3 getRotationMatrix(float angle) {
        Matrix3x3 result = new Matrix3x3();
        result.data[0][0] = (float) Math.cos(angle);
        result.data[0][1] = (float) -Math.sin(angle);
        result.data[1][0] = (float) Math.sin(angle);
        result.data[1][1] = (float) Math.cos(angle);
        return result;
    }

    public static Matrix3x3 getTranslationMatrix(float x, float y) {
        Matrix3x3 result = new Matrix3x3();
        result.data[0][2] = x;
        result.data[1][2] = y;
        return result;
    }

    public static Matrix3x3 getScalingMatrix(float scale) {
        Matrix3x3 result = new Matrix3x3();
        result.data[0][0] = scale;
        result.data[1][1] = scale;
        return result;
    }

    public Point2D multiplyBy(Point2D point) {
        Point2D result = new Point2D();
        result.x = data[0][0] * point.x + data[0][1] * point.y + data[0][2];
        result.y = data[1][0] * point.x + data[1][1] * point.y + data[1][2];
        return result;
    }

    public Matrix3x3 multiplyBy(Matrix3x3 matrix) {
        Matrix3x3 result = new Matrix3x3();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.data[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                    result.data[i][j] += this.data[i][k] * matrix.data[k][j];
                }
            }
        }
        return result;
    }
}