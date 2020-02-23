package org.simbicon;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Stel-l
 */
public class Simbicon extends JFrame {

    public static void main(String[] args) {
        Simbicon simbicon = new Simbicon();
        simbicon.setTitle("Simbicon");
        simbicon.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simbicon.initSwing();
        simbicon.init();
        simbicon.setVisible(true);

        simbicon.runLoop();
    }

    private final JPanel topPanel = new JPanel();
    private final JLabel lblSpeed = new JLabel("Speed: ");
    private final JSlider sliderSpeed = new JSlider(1, 100);
    private final JButton btnReset = new JButton("Reset");
    private final Canvas canvas = new Canvas();
    private final BufferedImage imgBuffer = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);

    private final Controller con = new Controller();
    private final Bip7 bip7 = new Bip7();
    private final Ground ground = new Ground();
    private float time = 0;
    private float deltaTime = 0.00005f;
    private float deltaTimeRender = 0.0012f;

    private final float desVel = 0f;

    private void initSwing() {
        sliderSpeed.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent evt) {
                //adjust the speed of the animation:
                float slow  = 0.0001f;
                float fast  = 0.02f;
                float range = fast - slow;
                deltaTimeRender = slow + range * sliderSpeed.getValue() / 100.0f;
            }
        });
        btnReset.addActionListener(evt -> resetSimulation());
        topPanel.setLayout(new FlowLayout());
        topPanel.add(lblSpeed);
        topPanel.add(sliderSpeed);
        topPanel.add(btnReset);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        canvas.setSize(imgBuffer.getWidth(), imgBuffer.getHeight());
        pack();

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    bip7.PushTime  = 0.2f;
                    bip7.PushForce = -60;
                    return true;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    bip7.PushTime  = 0.2f;
                    bip7.PushForce = 60;
                    return true;
                }
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
                if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                    con.desiredGroupNumber = 1;
                }
                if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W') {
                    con.desiredGroupNumber = 0;
                }
                if (e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
                    con.desiredGroupNumber = 2;
                }
                if (e.getKeyChar() == '1') {
                    ground.getFlatGround();
                    resetSimulation();
                }
                if (e.getKeyChar() == '2') {
                    ground.getComplexTerrain();
                    resetSimulation();
                }
            }
            return false;
        });
    }

    public void init() {
        float torso0    = 0;
        float torso1    = 0;
        float torso2    = 0;
        float rhip0     = 0;
        float rhip1     = 0;
        float rhip2     = 0;
        float rknee0    = 0;
        float rknee1    = 0;
        float rknee2    = 0;
        float lhip0     = 0;
        float lhip1     = 0;
        float lhip2     = 0;
        float lknee0    = 0;
        float lknee1    = 0;
        float lknee2    = 0;
        float rankle0   = 0;
        float rankle1   = 0;
        float rankle2   = 0;
        float lankle0   = 0;
        float lankle1   = 0;
        float lankle2   = 0;
        float transTime = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Simbicon.class.getResourceAsStream("/run_params_1-beta-025-2.txt")))) {
            torso0    = new Float(br.readLine());
            torso1    = new Float(br.readLine());
            torso2    = new Float(br.readLine());
            rhip0     = new Float(br.readLine());
            rhip1     = new Float(br.readLine());
            rhip2     = new Float(br.readLine());
            rknee0    = new Float(br.readLine());
            rknee1    = new Float(br.readLine());
            rknee2    = new Float(br.readLine());
            lhip0     = 0;
            lhip1     = 0;
            lhip2     = 0;
            lknee0    = new Float(br.readLine());
            lknee1    = new Float(br.readLine());
            lknee2    = new Float(br.readLine());
            rankle0   = new Float(br.readLine());
            rankle1   = new Float(br.readLine());
            rankle2   = new Float(br.readLine());
            lankle0   = new Float(br.readLine());
            lankle1   = new Float(br.readLine());
            lankle2   = new Float(br.readLine());
            transTime = new Float(br.readLine());
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        //initialize the biped to a valid state:
        float[] state = {0.463f, 0.98f, 0.898f, -0.229f, 0.051f, 0.276f, -0.221f, -1.430f, -0.217f, 0.086f, 0.298f, -3.268f, -0.601f, 3.167f, 0.360f, 0.697f, 0.241f, 3.532f};
        bip7.setState(state);

        con.addWalkingController();
        con.addRunningController();
        con.addCrouchWalkController();

        // manually set controller params
        con.state[4].transTime = transTime;
        con.state[4].setThThDThDD(0, torso0, torso1, torso2);        // torso
        con.state[4].setThThDThDD(1, rhip0, rhip1, rhip2);        // rhip
        con.state[4].setThThDThDD(2, rknee0, rknee1, rknee2);        // rknee
        con.state[4].setThThDThDD(3, lhip0, lhip1, lhip2);        // lhip
        con.state[4].setThThDThDD(4, lknee0, lknee1, lknee2);        // lknee
        con.state[4].setThThDThDD(5, rankle0, rankle1, rankle2);        // rankle
        con.state[4].setThThDThDD(6, lankle0, lankle1, lankle2);        // lankle

        // switch legs
        con.state[6].transTime = transTime;
        con.state[6].setThThDThDD(0, torso0, torso1, torso2);        // torso
        con.state[6].setThThDThDD(1, lhip0, lhip1, lhip2);        // rhip
        con.state[6].setThThDThDD(2, lknee0, lknee1, lknee2);        // rknee
        con.state[6].setThThDThDD(3, rhip0, rhip1, rhip2);        // lhip
        con.state[6].setThThDThDD(4, rknee0, rknee1, rknee2);        // lknee
        con.state[6].setThThDThDD(5, lankle0, lankle1, lankle2);        // rankle
        con.state[6].setThThDThDD(6, rankle0, rankle1, rankle2);        // lankle

        con.desiredGroupNumber = 1;
    }

    public void runLoop() {
        while (isDisplayable()) {
            bip7.computeGroundForces(ground);
            bip7Control(bip7.t);
            bip7.runSimulationStep(deltaTime);

            time += deltaTime;
            if (time > deltaTimeRender) {
                update(getGraphics());
                time = 0;
            }
            Thread.yield();
        }
    }

    public void resetSimulation() {
        bip7.resetBiped();
        con.stateTime          = 0;
        con.fsmState           = 0;
        con.currentGroupNumber = 0;
        con.desiredGroupNumber = 1;
    }

    //////////////////////////////////////////////////////////
    //  PROC: wPDtorq()
    //  DOES: computes requires torque to move a joint wrt world frame
    //////////////////////////////////////////////////////////
    public void wPDtorq(float[] torq, int joint, float dposn, float kp, float kd, boolean world) {
        float joint_posn = bip7.state[4 + joint * 2];
        float joint_vel  = bip7.state[4 + joint * 2 + 1];
        if (world) {                   // control wrt world frame? (virtual)
            joint_posn += bip7.state[4];    // add body tilt
            joint_vel += bip7.state[5];    // add body angular velocity
        }
        torq[joint] = kp * (dposn - joint_posn) - kd * joint_vel;
    }

    //////////////////////////////////////////////////////////
    // PROC:  jointLimit()
    // DOES:  enforces joint limits
    //////////////////////////////////////////////////////////
    public float jointLimit(float torq, int joint) {
        float kpL       = 800;
        float kdL       = 80;
        float minAngle  = con.jointLimit[0][joint];
        float maxAngle  = con.jointLimit[1][joint];
        float currAngle = bip7.state[4 + joint * 2];
        float currOmega = bip7.state[4 + joint * 2 + 1];

        if (currAngle < minAngle) {
            torq = kpL * (minAngle - currAngle) - kdL * currOmega;
        } else if (currAngle > maxAngle) {
            torq = kpL * (maxAngle - currAngle) - kdL * currOmega;
        }
        return torq;
    }

    //////////////////////////////////////////////////////////
    //	PROC:	bip7WalkFsm(torq)
    //	DOES:	walking control FSM
    //////////////////////////////////////////////////////////
    public void bip7WalkFsm(float[] torq) {
        int torsoIndex  = 0;
        int rhipIndex   = 1;
        int rkneeIndex  = 2;
        int lhipIndex   = 3;
        int lkneeIndex  = 4;
        int rankleIndex = 5;
        int lankleIndex = 6;
        boolean[] worldFrame = {false,  // torso
                true,   // rhip
                false,  // rknee
                true,   // lhip
                false,  // lknee
                false,  // rankle
                false   // lankle
        };

        con.stateTime += deltaTime;
        ConState s = con.state[con.fsmState];

        float stanceFootX = bip7.getStanceFootXPos(con);
        float mdd         = bip7.state[1] - desVel;          // center-of-mass velocity error
        float md = bip7.state[0] - stanceFootX;

        for (int n = 0; n < 7; n++) {         // compute target angles for each joint
            float target = s.th[n] + md * s.thd[n] + mdd * s.thdd[n];         // target state + fb actions
            target = clamp(target, con.targetLimit[0][n], con.targetLimit[1][n]);    // limit range of target angle
            wPDtorq(torq, n, target, con.kp[n], con.kd[n], worldFrame[n]);  // compute torques
        }

        con.advance(bip7);    // advance FSM to next state if needed
    }

    //////////////////////////////////////////////////////////
    //	PROC:	bip7Control(torq)
    //	DOES:	calculates some primitive controlling torques
    //////////////////////////////////////////////////////////
    public void bip7Control(float[] torq) {
        int   body      = 0, stanceHip, swingHip;
        float fallAngle = 60;

        for (int n = 0; n < 7; n++) {
            torq[n] = 0;
        }

        // The following applies the control FSM.
        // As part of this, it computes the virtual fb torque for the
        // body, as implemented by a simple PD controller wrt to the world up vector
        if (!bip7.lostControl) {
            bip7WalkFsm(torq);
        }

        // now change torq[body], which is virtual, 
        // to include a FEL feed-forward component

        // compute stance leg torque based upon body and swing leg
        if (con.state[con.fsmState].leftStance) {
            stanceHip = 3;   // left hip
            swingHip  = 1;   // right hip
        } else {
            stanceHip = 1;   // right hip
            swingHip  = 3;   // left hip
        }

        if (!con.state[con.fsmState].poseStance) {
            torq[stanceHip] = -torq[body] - torq[swingHip];
        }
        torq[0] = 0;         // no external torque allowed !

        for (int n = 1; n < 7; n++) {
            torq[n] = clamp(torq[n], con.torqueLimit[0][n], con.torqueLimit[1][n]);   // torq limits
            jointLimit(torq[n], n);                                             // apply joint limits
        }
    }

    public void update(Graphics g) {
        Graphics gBuffer = imgBuffer.getGraphics();
        gBuffer.setColor(new Color(255, 255, 255));
        gBuffer.fillRect(0, 0, imgBuffer.getWidth(), imgBuffer.getHeight());

        Matrix3x3 m = Matrix3x3.getTranslationMatrix(0, -300);
        m = m.multiplyBy(Matrix3x3.getScalingMatrix((float) 100));

        float panX = bip7.state[0];
        float panY = 0; //bip7.state[2];
        m = m.multiplyBy(Matrix3x3.getTranslationMatrix(-panX + 1.5f, -panY + 0.5f));

        bip7.drawBiped(gBuffer, m);
        ground.draw(gBuffer, m);
        canvas.getGraphics().drawImage(imgBuffer, 0, 0, this);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
