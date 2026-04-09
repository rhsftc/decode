package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheel {
    public DcMotorEx flyWheelMotor;
    private double encoderCPM = 384.5;
    private double gearRatio = 1.0;
    private double kP = 0.0015, kV = 0.00201, kS = .0435;

    void init(HardwareMap hardwareMap) {
        flyWheelMotor = hardwareMap.get(DcMotorEx.class, "motor");
        flyWheelMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        flyWheelMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    public void setMotorMode(DcMotorEx.RunMode runMode) {
        flyWheelMotor.setMode(runMode);
    }

    public void setMotorPower(double power) {
        flyWheelMotor.setPower(power);
    }

    public void stopMotor() {
        flyWheelMotor.setPower(0);
    }

    public void setRPM(double targetRPM) {
        // call every loop
        double error = targetRPM - getRPM();
        double feedBack = error * kP;
        double feedForward = (kV * targetRPM) + kS;
        double power = feedForward + feedBack;
        setMotorPower(power);
    }

    public double getTicksPerSecond() {
        return flyWheelMotor.getVelocity();
    }

    public double getRPM() {
        return (getTicksPerSecond() / encoderCPM * 60) / gearRatio;
    }
}

