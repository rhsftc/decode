package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "kVTuner", group = "test")
public class kVTuner extends OpMode {
    private FlyWheel flyWheel;
    private double targetRPM = 3000.0;
    public double kV = .01;
    public double kS = 0.0435;
    public double goalRPM = 3000;
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIndex = 4; //start at .01

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
            kV += currentStep;
        }

        if (gamepad1.dpadDownWasPressed()) {
            kV -= currentStep;
        }

        if (gamepad1.a) {
            goalRPM = 3000;
        } else if (gamepad1.b) {
            goalRPM = 1500;
        }

        double power = kV * goalRPM + kS;

        flyWheel.setMotorPower(power);

        telemetry.addData("step", "%.6f", currentStep);
        telemetry.addData("increment", "%.6f", kV);
        telemetry.addData("kV", "%.6f", kV);
        telemetry.addData("Target RPM", goalRPM);
        telemetry.addData("RPM", flyWheel.getRPM());
        telemetry.update();
    }
}
