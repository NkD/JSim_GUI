package org.simbicon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * @author Stel-l
 */
public class Ground {
    public float[] gX = new float[1000];
    public float[] gY = new float[1000];
    public static boolean drawTics = true;

    int npts;

    public Ground() {
        getFlatGround();
//        getComplexTerrain();
    }

    void getFlatGround() {
        npts  = 2;
        gX[0] = -100;
        gX[1] = 100;
        gY[0] = 0;
        gY[1] = 0;
    }

    void getComplexTerrain() {
        npts  = 15;
        gX[0] = -10;
        gY[0] = 0;
        gX[1] = 0;
        gY[1] = 0;

        gX[2]  = 1.79f;
        gY[2]  = 0;
        gX[3]  = 1.8f;
        gY[3]  = -0.2f;
        gX[4]  = 3.0f;
        gY[4]  = -0.2f;
        gX[5]  = 3.01f;
        gY[5]  = -0.4f;
        gX[6]  = 5.2f;
        gY[6]  = -0.4f;
        gX[7]  = 5.21f;
        gY[7]  = -0.6f;
        gX[8]  = 7;
        gY[8]  = -0.6f;
        gX[9]  = 7.01f;
        gY[9]  = -0.6f;
        gX[10] = 9;
        gY[10] = -0.8f;
        gX[11] = 11;
        gY[11] = -0.59f;
        gX[12] = 12;
        gY[12] = -0.59f;
        gX[13] = 14;
        gY[13] = -0.8f;
        gX[14] = 1000;
        gY[14] = -0.8f;

    }

    float gndHeight(float x) {
        int   n;
        float x1, x2, y1, y2;

        for (n = 0; n < npts - 1; n++) {
            if (gX[n + 1] >= x) {
                break;
            }
        }
        x1 = gX[n];
        x2 = gX[n + 1];
        y1 = gY[n];
        y2 = gY[n + 1];
        if (npts == 0 || n == (npts - 1)) {
            return 0;
        }
        float y = y1 + (x - x1) * (y2 - y1) / (x2 - x1);   // interpolate
        return y;
    }

    void drawTics(Graphics g, Matrix3x3 transform, float ticSpacing, int nTics, float ticLength, int ticLW) {
        Point2D p1 = new Point2D();
        Point2D p2 = new Point2D();
        for (int n = 0; n < nTics; n++) {
            float x1 = n * ticSpacing;
            float y1 = gndHeight(x1);
            float x2 = -1 * x1;
            float y2 = gndHeight(x2);
            p1 = transform.multiplyBy(new Point2D(x1, y1));
            p2 = transform.multiplyBy(new Point2D(x1, y1 - ticLength));
            g.drawLine((int) p1.x, -(int) p1.y, (int) p2.x, -(int) p2.y);
            p1 = transform.multiplyBy(new Point2D(x2, y2));
            p2 = transform.multiplyBy(new Point2D(x2, y2 - ticLength));
            g.drawLine((int) p1.x, -(int) p1.y, (int) p2.x, -(int) p2.y);
        }
    }

    void draw(Graphics g, Matrix3x3 transform) {
        // draw ground
        Point2D p1 = new Point2D();
        Point2D p2 = new Point2D();

        // draw the left upper leg
        g.setColor(new Color(40, 40, 40));
        Graphics2D  g2 = (Graphics2D) g;
        BasicStroke bs = new BasicStroke(3);
        g2.setStroke(bs);
        p1 = transform.multiplyBy(new Point2D(gX[0], gY[0]));

        for (int n = 1; n < npts; n++) {
            p2 = transform.multiplyBy(new Point2D(gX[n], gY[n]));
            g.drawLine((int) p1.x, -(int) p1.y, (int) p2.x, -(int) p2.y);
            p1 = p2;
        }

        if (drawTics) {
            drawTics(g, transform, 1.0f, 300, 0.08f, 3);     // draw major tics
        }

        BasicStroke bs2 = new BasicStroke(1);
        g2.setStroke(bs2);
    }

}
