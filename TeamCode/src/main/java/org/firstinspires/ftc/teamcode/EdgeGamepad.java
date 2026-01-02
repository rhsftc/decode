package org.firstinspires.ftc.teamcode;

/*
 * Edge detection for gamepad buttons and triggers.
 * The SDK and SolversLib gamepad edge detection only work in linear opmodes.
 * This class works in iterative opmodes.
 * Only rising edge detection is currrently implemented.
 */
import com.qualcomm.robotcore.hardware.Gamepad;

public class EdgeGamepad {

    // --- BUTTONS ---
    private boolean lastA, lastB, lastX, lastY;
    private boolean lastLB, lastRB;
    private boolean lastBack, lastStart;
    private boolean lastLS, lastRS;

    // --- DPAD ---
    private boolean lastUp, lastDown, lastLeft, lastRight;

    // --- TRIGGERS ---
    private double triggerThreshold = 0.5;
    private boolean lastLT, lastRT;

    public EdgeGamepad() {
    }

    // Constructor with custom trigger threshold
    public EdgeGamepad(double triggerThreshold) {
        this.triggerThreshold = triggerThreshold;
    }

    // --- UPDATE ALL STATES ---
    public void update(Gamepad gp) {
        // Buttons
        lastA = updateEdge(gp.a, lastA);
        lastB = updateEdge(gp.b, lastB);
        lastX = updateEdge(gp.x, lastX);
        lastY = updateEdge(gp.y, lastY);

        lastLB = updateEdge(gp.left_bumper, lastLB);
        lastRB = updateEdge(gp.right_bumper, lastRB);

        lastBack = updateEdge(gp.back, lastBack);
        lastStart = updateEdge(gp.start, lastStart);

        lastLS = updateEdge(gp.left_stick_button, lastLS);
        lastRS = updateEdge(gp.right_stick_button, lastRS);

        // D-pad
        lastUp = updateEdge(gp.dpad_up, lastUp);
        lastDown = updateEdge(gp.dpad_down, lastDown);
        lastLeft = updateEdge(gp.dpad_left, lastLeft);
        lastRight = updateEdge(gp.dpad_right, lastRight);

        // Triggers (convert analog → digital)
        lastLT = updateEdge(gp.left_trigger > triggerThreshold, lastLT);
        lastRT = updateEdge(gp.right_trigger > triggerThreshold, lastRT);
    }

    // --- EDGE DETECTOR ---
    private boolean updateEdge(boolean current, boolean last) {
        boolean pressed = current && !last;
        // update last AFTER checking
        return current;
    }

    // --- PUBLIC ACCESSORS (rising edges) ---
    public boolean aPressed() {
        return lastA;
    }

    public boolean bPressed() {
        return lastB;
    }

    public boolean xPressed() {
        return lastX;
    }

    public boolean yPressed() {
        return lastY;
    }

    public boolean leftBumperPressed() {
        return lastLB;
    }

    public boolean rightBumperPressed() {
        return lastRB;
    }

    public boolean backPressed() {
        return lastBack;
    }

    public boolean startPressed() {
        return lastStart;
    }

    public boolean leftStickPressed() {
        return lastLS;
    }

    public boolean rightStickPressed() {
        return lastRS;
    }

    public boolean dpadUpPressed() {
        return lastUp;
    }

    public boolean dpadDownPressed() {
        return lastDown;
    }

    public boolean dpadLeftPressed() {
        return lastLeft;
    }

    public boolean dpadRightPressed() {
        return lastRight;
    }

    public boolean leftTriggerPressed() {
        return lastLT;
    }

    public boolean rightTriggerPressed() {
        return lastRT;
    }
}
