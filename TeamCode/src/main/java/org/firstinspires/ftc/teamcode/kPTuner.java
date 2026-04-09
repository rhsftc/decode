package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "kPTuner", group = "test")
public class kPTuner extends OpMode {
    private FlyWheel flyWheel;
    private double targetRPM = 400.0;
    public double kV = 0.00201;
    public double kS = 0.0435;
    public double kP = 0.0;
    public double goalRPM = 400;
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIndex = 2; //start at .0001

    @Override
    public void init() {
        flyWheel = new FlyWheel(flyWheel.pidfType);
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
            goalRPM = 400;
        } else if (gamepad1.b) {
            goalRPM = 200;
        }

        double feedForward = (kP * goalRPM) + kS;
        double error = goalRPM - flyWheel.getRPM();
        double feedBack = kP * error;

        flyWheel.setMotorPower(feedForward + feedBack);

        telemetry.addData("step", "%.6f", currentStep);
        telemetry.addData("Error", error);
        telemetry.addData("kP", "%.6f", kP);
        telemetry.addData("Target RPM", goalRPM);
        telemetry.addData("RPM", flyWheel.getRPM());
        telemetry.update();
    }
}
