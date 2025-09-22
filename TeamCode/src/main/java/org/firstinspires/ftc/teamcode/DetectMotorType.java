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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * This op mode gets information about the connected motor.
 * The code is structured as a LinearOpMode
 * <p>
 * This code assumes a DC motor configured with the name "motor" as is found on
 * a Robot.
 * <p>
 * INCREMENT sets how much to increase/decrease the power each cycle
 * CYCLE_MS sets the update period.
 * <p>
 * Remove or comment out the @Disabled line to add this opmode to the Driver
 * Station OpMode list
 */
@TeleOp(name = "Detect Motor", group = "test")
// @Disabled
public class DetectMotorType extends LinearOpMode {

    private static final double INCREMENT = 0.01; // amount to ramp motor each CYCLE_MS cycle
    private static final int CYCLE_MS = 25; // period of each cycle
    private static final int PAUSE_MS = 1000; // pause between direction switches
    private ElapsedTime timer;

    // Define class members
    private DcMotorEx motor;
    // Motor information
    private MotorConfigurationType type;
    private double maxRPM;
    double maxVelocity = 0;
    private double velocity = 0;
    private PIDFCoefficients pidfCoefficentsRunToPosition;
    private PIDFCoefficients pidfCoefficientsRunUsing;
    private double achievableTicsPerSecond;
    private double ticsPerRev;
    private int currentPosition;
    private double current;
    private double power = 0;
    private boolean rampUp = true;

    @Override
    public void runOpMode() {
        timer = new ElapsedTime();
        // Change the text in quotes to match any motor name on your robot.
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        type = motor.getMotorType();
        maxRPM = type.getMaxRPM();
        achievableTicsPerSecond = type.getAchieveableMaxTicksPerSecond();
        ticsPerRev = type.getTicksPerRev();
        currentPosition = motor.getCurrentPosition();
        getMaxVelocity();
        pidfCoefficentsRunToPosition = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);
        pidfCoefficientsRunUsing = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        telemetry.addData("Device Type", type.getName());
        telemetry.addData("RPM", "%5.2f", maxRPM);
        telemetry.addData("Max Velocity", "%5.2f", maxVelocity);
        telemetry.addData("Achievable Tics per Second", "%5.2f", achievableTicsPerSecond);
        telemetry.addData("Tics Per Rev", "%5.2f", ticsPerRev);
        telemetry.addData("Current Position", "%d", currentPosition);
        telemetry.addData("PIDF Run Using", pidfCoefficientsRunUsing);
        telemetry.addData("PIDF Run To Position", pidfCoefficentsRunToPosition);
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();

        // Wait for the start button
        waitForStart();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficientsRunUsing);

        // Ramp motor speeds till stop pressed.
        while (opModeIsActive()) {

            // Ramp the motors, according to the rampUp variable.
            if (rampUp) {
                // Keep stepping up until we hit the max value.
                velocity += INCREMENT * maxVelocity;
                if (velocity >= maxVelocity) {
                    velocity = maxVelocity;
                    rampUp = !rampUp; // Switch ramp direction
                }
            } else {
                // Keep stepping down until we hit the min value.
                velocity -= INCREMENT * maxVelocity;
                if (velocity <= -maxVelocity) {
                    velocity = -maxVelocity;
                    rampUp = !rampUp; // Switch ramp direction
                }
            }

            // Set the motor to the new power and pause;
            motor.setVelocity(velocity);
            sleep(CYCLE_MS);

            power = motor.getPower();
            currentPosition = motor.getCurrentPosition();
            current = motor.getCurrent(CurrentUnit.MILLIAMPS);

            // Display the current value
            telemetry.addData("Motor Power", "%5.2f", power);
            telemetry.addData("Velocity (Tics per Second)", "%5.2f", velocity);
            telemetry.addData("Current Position", "%d", currentPosition);
            telemetry.addData("Current (milli amps)", "%5.2f", current);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }

        // Turn off motor and signal done;
        motor.setPower(0);
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    private void getMaxVelocity() {
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(1);
        timer.reset();
        while (timer.seconds() < 4 && !isStopRequested()) {
            velocity = motor.getVelocity();
            if (velocity > maxVelocity) {
                maxVelocity = velocity;
            }
            telemetry.addData("current velocity", "%5.2f", velocity);
            telemetry.addData("maximum velocity", "%5.2f", maxVelocity);
            telemetry.addData("Power", "%5.2f", motor.getPower());
            telemetry.update();
        }

        motor.setPower(0);
        velocity = 0;
    }
}
