/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/*
 * OpMode to test the effect of  changing PIDF values for motor encoders.
 * Use it to experiment with PIDF values.
 */
//@Config
@TeleOp(name = "PIDF Velocity", group = "test")
//@Disabled
public class PIDFVelocity extends OpMode {
    private VoltageSensor voltageSensor;
    private double controlHubVoltage = 0;
    private final ElapsedTime timer = new ElapsedTime();
    private MotorEx motor;
    private static double achievableTicksPerSecond = 0;
    private SimpleMotorFeedforward feedforward;
    private double calculatedFeedforward = 0;
    private Datalogger datalogger;
    private String datalogFilename = "PIDFDatalog";   // modify name for each run

    private double[] defaultVelocityCoefficients = new double[4];
    private double[] defaultFeedforwardCoefficients = new double[4];

    private double[] velocityCoefficients = new double[4];
    private double[] feedforwardCoefficients = new double[4];
    // Control running and logging of the test.
    private boolean isRunningTest = true;
//    FtcDashboard dashboard;
//    Telemetry telemetry;
    public static double RUN_VELOCITY = 0.5;
    public static double TARGET = RUN_VELOCITY * achievableTicksPerSecond;
    public static double VELOCITY_P = 0.0015;
    public static double VELOCITY_I = 0.0;
    public static double VELOCITY_D = 0.0;
    public static double FF_S = 0.0435;
    public static double FF_V = 0.0201;

    private enum RunPIDFState {
        WAITING_TO_START,
        RUN_ONE,
        QUICK_STOP,
        RUN_TWO,
        DELAYING_AFTER_RUNNING
    }

    RunPIDFState runState = RunPIDFState.WAITING_TO_START;

    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        voltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
//        dashboard = FtcDashboard.getInstance();
//        telemetry = dashboard.getTelemetry();
        datalogger = new Datalogger(datalogFilename);
        initDatalogger();

        motor = new MotorEx(hardwareMap, "motor", Motor.GoBILDA.RPM_435);
        motor.setInverted(false);
        motor.setZeroPowerBehavior(MotorEx.ZeroPowerBehavior.FLOAT);
        motor.setRunMode(MotorEx.RunMode.VelocityControl);
        achievableTicksPerSecond = motor.ACHIEVABLE_MAX_TICKS_PER_SECOND;
        TARGET = RUN_VELOCITY * achievableTicksPerSecond;
        feedforwardCoefficients = motor.getFeedforwardCoefficients();
        feedforward = new SimpleMotorFeedforward(feedforwardCoefficients[0],
                feedforwardCoefficients[1]);

        // Get the default coefficients.
        getPIDFCoefficients();
        // Save the default coefficients.
        defaultVelocityCoefficients = velocityCoefficients;
        defaultFeedforwardCoefficients = feedforwardCoefficients;
        telemetry.addLine("Left bumper: Start test with default PIDF coefficients");
        telemetry.addLine("Right bumper: Start test with custom coefficients");
        showTelemetry();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the init button is pressed and when the play button is pressed (or the
     * OpMode is stopped).
     */
    @Override
    public void init_loop() {
    }

    /**
     * This method will be called once, when the play button is pressed.
     */
    @Override
    public void start() {
        timer.reset();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the play button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
        if (isRunningTest) {
            runPIDFTest();
        }

        showTelemetry();
    }

    /*
     * The main state machine for running the PIDF test.
     * */
    private void runPIDFTest() {
        switch (runState) {
            case WAITING_TO_START:
                if (gamepad1.leftBumperWasPressed()) {
                    updatePIDF(true);
                    timer.reset();
                    motor.setVelocity(RUN_VELOCITY * achievableTicksPerSecond);
                    runState = RunPIDFState.RUN_ONE;
                } else if (gamepad1.rightBumperWasPressed()) {
                    updatePIDF(false);
                    timer.reset();
                    motor.setVelocity(RUN_VELOCITY * achievableTicksPerSecond);
                    runState = RunPIDFState.RUN_ONE;
                }
                break;

            // Start running the motor.
            case RUN_ONE:
                calculatedFeedforward = feedforward.calculate(RUN_VELOCITY);
                motor.set(calculatedFeedforward);
                if (timer.milliseconds() >= 1500) {
                    motor.stopMotor();
                    timer.reset();
                    runState = RunPIDFState.QUICK_STOP;
                }
                logData();
                break;

            // Stop the motor to simulate launching or some other interruption.
            case QUICK_STOP:
                if (timer.milliseconds() >= 10) {
                    timer.reset();
                    runState = RunPIDFState.RUN_TWO;
                }
                logData();
                break;

            // Run the rest of the sequence.
            case RUN_TWO:
                calculatedFeedforward = feedforward.calculate(RUN_VELOCITY);
                motor.set(calculatedFeedforward);
                if (timer.milliseconds() >= 1500) {
                    motor.stopMotor();
                    timer.reset();
                    runState = RunPIDFState.DELAYING_AFTER_RUNNING;
                }
                logData();
                break;

            case DELAYING_AFTER_RUNNING:
                // Give some time for the motor to stop.
                if (timer.milliseconds() >= 300) {
                    datalogger.closeDataLogger();
                    runState = RunPIDFState.WAITING_TO_START;
                }
                logData();
                break;

            default:
                break;
        }

    }

    /**
     * This method will be called once, when this OpMode is stopped.
     * <p>
     * Your ability to control hardware from this method will be limited.
     */
    @Override
    public void stop() {
        stopAndResetEncoder(motor);
    }

    /// @param motor
    private void stopAndResetEncoder(MotorEx motor) {
        motor.setRunMode(Motor.RunMode.RawPower);
        motor.stopAndResetEncoder();
    }

    /*
     * Initialize the datalogger by naming the fields (column labels).
     * */
    private void initDatalogger() {
        // Name the fields (column labels) generated by this OpMode.
        datalogger.addField("Target Velocity");
        datalogger.addField("Calculated Feedforward");
        datalogger.addField("Velocity");
        datalogger.addField("Current (mA)");
        datalogger.addField("Voltage (V)");
        datalogger.addField("Acceleration");
        datalogger.firstLine();                        // end first line (row)
    }

    /*
     * Log data to the datalogger when isRunning = true.
     * */
    private void logData() {
        datalogger.addField(TARGET);
        datalogger.addField(calculatedFeedforward);
        datalogger.addField(motor.getVelocity());
        datalogger.addField(motor.getCurrent(CurrentUnit.MILLIAMPS));
        datalogger.addField(controlHubVoltage);
        datalogger.addField(motor.getAcceleration());
        datalogger.newLine();
    }

    private void getPIDFCoefficients() {
        velocityCoefficients = motor.getVeloCoefficients();
        feedforwardCoefficients = motor.getFeedforwardCoefficients();
    }

    /*
     * */
    private void updatePIDF(boolean useDefaults) {
        if (useDefaults) {
            motor.setFeedforwardCoefficients(defaultFeedforwardCoefficients[0],
                    defaultFeedforwardCoefficients[1],
                    defaultFeedforwardCoefficients[2]);
            motor.setVeloCoefficients(defaultVelocityCoefficients[0],
                    defaultVelocityCoefficients[1],
                    defaultVelocityCoefficients[2]);
        } else {
            motor.setVeloCoefficients(VELOCITY_P, VELOCITY_I, VELOCITY_D);
            motor.setFeedforwardCoefficients(FF_S, FF_V);
        }
        // Update the coefficients from the motor after setting them for telemetry display.
        getPIDFCoefficients();
    }

    private void showTelemetry() {
        controlHubVoltage = voltageSensor.getVoltage();
        telemetry.addData("Run state", runState);
        telemetry.addData("Max RPM", motor.getMaxRPM());
        telemetry.addData("Achievable Ticks", achievableTicksPerSecond);
        telemetry.addData("Target Velocity", "%6.2f", TARGET);
        telemetry.addData("Velocity", "%6.2f", motor.getVelocity());
        telemetry.addData("Acceleration", "%6.2f", motor.getAcceleration());
        telemetry.addData("Current (milli amps)", "%6.2f", motor.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Voltage", "%6.2f", controlHubVoltage);
        telemetry.addData("Velocity kP", velocityCoefficients[0]);
        telemetry.addData("Velocity kI", velocityCoefficients[1]);
        telemetry.addData("Velocity kD", velocityCoefficients[2]);
        telemetry.addData("FF kS", feedforwardCoefficients[0]);
        telemetry.addData("FF kV", feedforwardCoefficients[1]);
        telemetry.addData("FF kA", feedforwardCoefficients[2]);
        telemetry.update();
    }
}
