package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "kPTuner", group = "test")
public class kPTuner extends OpMode {
    private FlyWheel flyWheel;
    private double targetRPM = 3000.0;
    public double kV = 01;
    public double kS = 0.17;
    public double kP = 0.1;
    public double goalRPM = 3000;
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIndex = 2; //start at .0001

    @Override
    public void init() {
        flyWheel = new FlyWheel();
        flyWheel.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.dpadLeftWasPressed() && incrementIndex < 4) {
            incrementIndex++;
        } else if (gamepad1.dpadRightWasPressed() && incrementIndex > 0) {
            incrementIndex--;
        }

        double currentStep = increments[incrementIndex];

        if (gamepad1.dpadUpWasPressed()) {
            kP += currentStep;
        }

        if (gamepad1.dpadDownWasPressed()) {
            kP -= currentStep;
        }

        if (gamepad1.a) {
            goalRPM = 3000;
        } else if (gamepad1.b) {
            goalRPM = 1500;
        }

        double feedForward = kP * goalRPM + kS;
        double error = goalRPM - flyWheel.getRPM();
        double feedBack = kP * error;

        flyWheel.setMotorPower(feedForward + feedBack);

        telemetry.addData("step", "%.6f", currentStep);
        telemetry.addData("increment", "%.6f", kP);
        telemetry.addData("Error", error);
        telemetry.addData("kP", "%.6f", kP);
        telemetry.addData("Target RPM", goalRPM);
        telemetry.addData("RPM", flyWheel.getRPM());
        telemetry.update();
    }
}
