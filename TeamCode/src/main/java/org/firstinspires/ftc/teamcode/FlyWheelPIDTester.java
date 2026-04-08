package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "FlyWheelPIDTester", group = "test")
public class FlyWheelPIDTester extends OpMode {
    private FlyWheel flyWheel = new FlyWheel();
    private double targetRPM = 400.0;

    @Override
    public void init() {
        flyWheel.init(hardwareMap);
    }

    @Override
    public void loop() {

        flyWheel.setRPM(targetRPM);

        telemetry.addData("Target RPM", targetRPM);
        telemetry.addData("RPM", flyWheel.getRPM());
        telemetry.update();
    }
}

