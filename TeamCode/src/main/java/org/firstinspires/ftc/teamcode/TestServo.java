package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@TeleOp(name = "Test Servo", group = "test")
//@Disabled
public class TestServo extends OpMode {
    static final double INCREMENT = 0.1;
    // amount to slew servo each button press.
    private Servo servo;
    private double position = 0;

    @Override
    public void init() {
        // Make the name match your config file and robot.
        // servo = hardwareMap.get(ServoImplEx.class, "servo1");
        servo = hardwareMap.get(ServoImplEx.class, "servo1");
        showConfigTelemetry();
        telemetry.update();
    }

    @Override
    public void init_loop() {
        if (gamepad1.aWasReleased()) {

        }
    }

    @Override
    public void start() {
        showTelemetry();
        telemetry.update();
        servo.setPosition(0);
    }

    @Override
    public void loop() {
        if (gamepad1.leftBumperWasReleased()) {
            position = 0;
        }

        if (gamepad1.rightBumperWasReleased()) {
            position = 1;
        }

        if (gamepad1.yWasReleased()) {
            position = .5;
        }

        if (gamepad1.dpadUpWasReleased()) {
            position += INCREMENT;
        }

        if (gamepad1.dpadDownWasReleased()) {
            position -= INCREMENT;
        }

        if (gamepad1.backWasReleased()) {
            ((PwmControl) servo).setPwmDisable();
        }

        servo.setPosition(position);
        showTelemetry();
    }

    private void showConfigTelemetry() {
        telemetry.addLine("a = Servo");
        telemetry.addLine("b = CRServo");
    }

    private void showTelemetry() {
        telemetry.addLine("Disable Servo = back");
        telemetry.addLine("Left bumper = 0");
        telemetry.addLine("Right bumper = 1");
        telemetry.addLine("Y = .5 (middle)");
        telemetry.addLine("Dpad up: Increase position");
        telemetry.addLine("Dpad down: Decrease position");
        telemetry.addData("PWM Range", ((PwmControl) servo).getPwmRange());
        telemetry.addData("Position", position);
    }
}
