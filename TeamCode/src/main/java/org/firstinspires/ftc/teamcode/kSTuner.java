package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "kSTuner", group = "test")
public class kSTuner extends OpMode {
    private FlyWheel flyWheel;
    private double targetRPM = 400.0;
    public double kS = 0;
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIndex = 4; //start at .01

    @Override
    public void init() {
        flyWheel = new FlyWheel(FlyWheel.PIDFType.SDK_DEFAULT);
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
            kS += currentStep;
        }

        if (gamepad1.dpadDownWasPressed()) {
            kS -= currentStep;
        }

        flyWheel.setMotorPower(kS);

        telemetry.addData("step", "%.6f", currentStep);
        telemetry.addData("increment", "%.6f", kS);
        telemetry.addData("kS", "%.6f", kS);
        telemetry.addData("RPM", flyWheel.getRPM());
        telemetry.addData("Ticks per second", flyWheel.getTicksPerSecond());
        telemetry.addData("Target RPM", targetRPM);
        telemetry.update();
    }

}
