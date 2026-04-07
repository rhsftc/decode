package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheel {
    private DcMotorEx flyWheelMotor;
    private double encoderCPM;
    private double gearRatio = 3.0;
    private double kP, kV, kS;

    void init(HardwareMap hardwareMap) {
        flyWheelMotor = hardwareMap.get(DcMotorEx.class, "motor");
        flyWheelMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setMotorMode(DcMotorEx.RunMode runMode) {
        flyWheelMotor.setMode(runMode);
    }

    public void setMotorPower(double power) {
        flyWheelMotor.setPower(power);
    }

    public double getTicksPerSecond() {
        return flyWheelMotor.getVelocity();
    }

    public double getRPM() {
        return flyWheelMotor.getVelocity() / encoderCPM * 60;
    }
}

