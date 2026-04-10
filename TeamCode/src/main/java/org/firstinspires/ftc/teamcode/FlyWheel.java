package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class FlyWheel {
    public DcMotorEx flyWheelMotor;
    private double encoderCPM = 384.5;
    private double gearRatio = 1.0;
    private double kP = 0.0013, kV = 0.00201, kS = .0435;

    // variables for SDK PIDF coefficients
    private PIDFCoefficients velocityCoefficients;

    // This allows the flywheel to be tested using different PIDF implementations.
    public enum PIDFType {
        SDK_DEFAULT,
        SDK,
        CUSTOM;

        public PIDFType getNext() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    public PIDFType pidfType = PIDFType.SDK_DEFAULT;

    public FlyWheel(PIDFType pidfType) {
        this.pidfType = pidfType;
    }

    void init(HardwareMap hardwareMap) {
        flyWheelMotor = hardwareMap.get(DcMotorEx.class, "motor");
        flyWheelMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        // Depending on the use of the flywheel on your robot the zero power behavior may need to be changed.
        flyWheelMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        // Get the SDK default PIDF coefficients.
        velocityCoefficients = flyWheelMotor.getPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER);
        switch (pidfType) {
            case SDK_DEFAULT:
                break;
            case SDK:
                velocityCoefficients.p = kV;
                velocityCoefficients.i = 0.0;
                velocityCoefficients.d = 0.0;
                velocityCoefficients.f = 0.0;
                break;
            case CUSTOM:
                break;
            default:
        }
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

    // call every loop to maintain target RPM
    public void setRPM(double targetRPM) {
        double error = targetRPM - getRPM();
        double feedBack = error * kP;
        double feedForward = getFeedForward(targetRPM);
        if (pidfType == PIDFType.SDK || pidfType == PIDFType.SDK_DEFAULT) {
            flyWheelMotor.setVelocityPIDFCoefficients(kP, 0, 0, feedForward);
        }
        double power = feedForward + feedBack;
        setMotorPower(power);
    }

    private double getFeedForward(double targetRPM) {
        return (kV * targetRPM) + kS;
    }

    public double getTicksPerSecond() {
        return flyWheelMotor.getVelocity();
    }

    public double getRPM() {
        return (getTicksPerSecond() / encoderCPM * 60) / gearRatio;
    }
}

