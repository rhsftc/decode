package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@TeleOp(name = "Test Servo", group = "test")
//@Disabled
public class TestServo extends OpMode {
    public enum SERVO_TYPE {
        STANDARD,
        CONTINUOUS
    }

    // amount to slew servo each button press.
    static final double INCREMENT = 0.1;
    SERVO_TYPE servoType = SERVO_TYPE.STANDARD;
    private PwmControl.PwmRange pwmRange;
    private Servo servo;
    private CRServo crServo;
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
            if (servoType == SERVO_TYPE.STANDARD) {
                servoType = SERVO_TYPE.CONTINUOUS;
            } else {
                servoType = SERVO_TYPE.STANDARD;
            }
        }

        telemetry.addData("Servo Type", servoType);
        telemetry.update();
    }

    @Override
    public void start() {
        telemetry.clearAll();
        telemetry.update();
        if (servoType == SERVO_TYPE.STANDARD) {
            ((ServoImplEx) servo).setPwmRange(new PwmControl.PwmRange(500, 2500));
            pwmRange = ((ServoImplEx) servo).getPwmRange();
            position = 0;
            servo = hardwareMap.get(ServoImplEx.class, "servo1");
            servo.setPosition(0);
            showServoTelemetry();
        } else {
            crServo = hardwareMap.get(CRServo.class, "servo1");
            crServo.setPower(0);
            showCRServoTelemetry();
        }
    }

    @Override
    public void loop() {
        switch (servoType) {
            case STANDARD:
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
                showServoTelemetry();
                break;
            case CONTINUOUS:
                if (gamepad1.dpadLeftWasReleased()) {
                    crServo.setPower(0);
                }

                if (gamepad1.dpadRightWasReleased()) {
                    crServo.setPower(1);
                }

                showCRServoTelemetry();
                break;
            default:
        }
    }

    private void showConfigTelemetry() {
        telemetry.addLine("a = Toggle servo type");
    }

    private void showServoTelemetry() {
        telemetry.addLine("Disable Servo = back");
        telemetry.addLine("Left bumper = 0");
        telemetry.addLine("Right bumper = 1");
        telemetry.addLine("Y = .5 (middle)");
        telemetry.addLine("Dpad up: Increase position");
        telemetry.addLine("Dpad down: Decrease position");
        telemetry.addData("PWM Lower Range", pwmRange.usPulseLower);
        telemetry.addData("PWM Upper Range", pwmRange.usPulseUpper);
        telemetry.addData("Position", position);
    }

    private void showCRServoTelemetry() {
        telemetry.addLine("Dpad left: Stop");
        telemetry.addLine("Dpad right: Full power");
        telemetry.addData("Power", crServo.getPower());
    }
}
