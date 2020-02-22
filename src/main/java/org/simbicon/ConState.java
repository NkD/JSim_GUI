package org.simbicon;

/**
 * @author Stel-l
 */
public class ConState {

    public int num = -1;               // absolute state number
    public int localNum;          // local state number
    public float[] th = new float[Controller.MaxJoints];   // target angles
    public float[] thd = new float[Controller.MaxJoints];  // coeff for d
    public float[] thdd = new float[Controller.MaxJoints]; // coeff for d_dot
    public int next;              // next state
    public boolean timeFlag;         // TRUE for time-based transitions
    public boolean leftStance;       // TRUE if this is a state standing on left foot
    public boolean poseStance;       // TRUE is this is an absolute pose state
    public float transTime;       // transition time
    public int sensorNum;         // transition sensor number

    public void setThThDThDD(int index, float t, float tD, float tDD) {
        th[index]   = t;
        thd[index]  = tD;
        thdd[index] = tDD;
    }
}
