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

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/*
 * OpMode to test the effect of  changing PIDF values for motor encoders.
 * Use it to experiment with PIDF values.
 */
//TODO: Add ability to change coefficients during the test.
@TeleOp(name = "PIDF Velocity", group = "test")
//@Disabled
public class PIDFVelocity extends OpMode {
    private final ElapsedTime timer = new ElapsedTime();
    private MotorEx motor;
    private double achievableTicksPerSecond;
    private SimpleMotorFeedforward feedforward;
    private Datalogger datalogger;
    private String datalogFilename = "PIDFDatalog";   // modify name for each run

    private double[] velocityCoefficients = new double[4];
    private double[] feedforwardCoefficients = new double[4];
    // Control running and logging of the test.
    private boolean isRunningTest = false;
    private final double RUN_VELOCITY = .8;
    private double velocityP = 0.06;
    private double velocityI = 0.03;
    private double velocityD = 0.0;
    private double ffV = 1.0;


    private enum RunPIDFState {
        WAITING_TO_START,
        RUNNING,
        DELAYING_AFTER_RUNNING
    }

    RunPIDFState runState = RunPIDFState.WAITING_TO_START;

    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        datalogger = new Datalogger(datalogFilename);
        initDatalogger();

        motor = new MotorEx(hardwareMap, "motor", Motor.GoBILDA.RPM_435);
        motor.setInverted(false);
        stopAndResetEncoder(motor);
        motor.setZeroPowerBehavior(MotorEx.ZeroPowerBehavior.BRAKE);
        motor.setRunMode(MotorEx.RunMode.VelocityControl);
        achievableTicksPerSecond = motor.ACHIEVABLE_MAX_TICKS_PER_SECOND;
        velocityCoefficients = motor.getVeloCoefficients();
        feedforwardCoefficients = motor.getFeedforwardCoefficients();
        feedforward = new SimpleMotorFeedforward(feedforwardCoefficients[0],
                feedforwardCoefficients[1],
                feedforwardCoefficients[2]);

        telemetry.addLine("Left bumper: Start PIDF test and logging");
        telemetry.addLine("dpad left/right: Decrease/Increase velocity kP by 0.01");
        telemetry.addLine("dpad up/down: Increase/Decrease feedforward kV by 0.1");
        telemetry.update();

        // Set new values for velocity control.
        updatePIDF();
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

        motor.set(feedforward.calculate(motor.getVelocity()));

        telemetry.addData("Max RPM", motor.getMaxRPM());
        telemetry.addData("Corrected Velocity", motor.getCorrectedVelocity());
        telemetry.addData("Achievable Ticks", achievableTicksPerSecond);
        telemetry.addData("Velocity", "%6.2f", motor.getVelocity());
        telemetry.addData("Acceleration", "%6.2f", motor.getAcceleration());
        telemetry.addData("Current (milli amps)", "%6.2f", motor.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Velocity kP", velocityCoefficients[0]);
        telemetry.addData("Velocity kI", velocityCoefficients[1]);
        telemetry.addData("Velocity kD", velocityCoefficients[2]);
        telemetry.addData("FF kS", feedforwardCoefficients[0]);
        telemetry.addData("FF kV", feedforwardCoefficients[1]);
        telemetry.addData("FF kA", feedforwardCoefficients[2]);
        telemetry.update();
    }

    /*
     * The main state machine for running the PIDF test.
     * */
    private void runPIDFTest() {
        logData();
        switch (runState) {
            case WAITING_TO_START:
                if (gamepad1.leftBumperWasPressed()) {
                    motor.setVelocity(RUN_VELOCITY * achievableTicksPerSecond);
                    timer.reset();
                    runState = RunPIDFState.RUNNING;
                }
                break;

            case RUNNING:
                motor.setVelocity(RUN_VELOCITY * achievableTicksPerSecond);
                if (timer.milliseconds() >= 3000) {
                    motor.stopMotor();
                    timer.reset();
                    runState = RunPIDFState.DELAYING_AFTER_RUNNING;
                }
                break;

            case DELAYING_AFTER_RUNNING:
                // Give some time for the motor to stop.
                if (timer.milliseconds() >= 500) {
                    datalogger.closeDataLogger();
                    runState = RunPIDFState.WAITING_TO_START;
                }
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
        datalogger.addField("Velocity");
        datalogger.addField("Corrected Velocity");
        datalogger.addField("Current (mA)");
        datalogger.addField("Acceleration");
        datalogger.firstLine();                        // end first line (row)
    }

    /*
     * Log data to the datalogger when isRunning = true.
     * */
    private void logData() {
        if (runState == RunPIDFState.RUNNING || runState == RunPIDFState.DELAYING_AFTER_RUNNING) {
            datalogger.addField(RUN_VELOCITY * achievableTicksPerSecond);
            datalogger.addField(motor.getVelocity());
            datalogger.addField(motor.getCorrectedVelocity());
            datalogger.addField(motor.getCurrent(CurrentUnit.MILLIAMPS));
            datalogger.addField(motor.getAcceleration());
            datalogger.newLine();
        }
    }

    /*
     * */
    private void updatePIDF() {
        motor.setVeloCoefficients(velocityP, velocityI, velocityD);
        motor.setFeedforwardCoefficients(1, ffV);
        velocityCoefficients = motor.getVeloCoefficients();
        feedforwardCoefficients = motor.getFeedforwardCoefficients();
    }
}
