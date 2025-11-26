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
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/*
 * OpMode to test the effect of  changing PIDF values for motor encoders.
 * Use it to experiment with PIDF values.
 */
//TODO: Add data logging.
@TeleOp(name = "PIDF Velocity", group = "test")
@Configurable
//@Disabled
public class PIDFVelocity extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final ElapsedTime timer = new ElapsedTime();
    private double maxVelocity = 0;
    private DcMotorEx motor;
    PIDFCoefficients pidfVelocityCoefficients;
    private final double RUN_VELOCITY = .9f;

    public static float velocityP = 1.063f;
    public static float velocityI = 1.063f;
    public static float velocityD = 0;
    public static float velocityF = 10.63f;

    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Left bumper - Start/Stop");
        telemetry.update();
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Get the default PIDF coefficients for velocity control.
        pidfVelocityCoefficients = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        // Find the maximum velocity of the motor.
        findMaxVelocity();
        stopAndResetEncoder(motor);

        // Get the default PIDF's and then set new values for velocity control.
        updatePIDF();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the init button is pressed and when the play button is pressed (or the
     * OpMode is stopped).
     */
    @Override
    public void init_loop() {
        telemetry.addLine("Left bumper - Start/Stop");
        telemetry.addLine("Y - Stop and Reset");
        telemetry.addData("Position", motor.getCurrentPosition());
        telemetry.update();
    }

    /**
     * This method will be called once, when the play button is pressed.
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the play button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
        updatePIDF();
        telemetry.addData("Status", "Run Time: " + runtime);
        // Start the motor at RUN_VELOCITY when the left bumper is released.
        if (gamepad1.leftBumperWasReleased()) {
            motor.setVelocity(maxVelocity * RUN_VELOCITY);
            timer.reset();
        }

        // Stop and reset the encoder after 2 seconds at max velocity.
        if (timer.seconds() >= 2 && motor.getVelocity() >= maxVelocity * RUN_VELOCITY) {
            stopAndResetEncoder(motor);
        }

        // Stop and reset the encoder when Y is released.
        if (gamepad1.yWasReleased()) {
            stopAndResetEncoder(motor);
        }

        telemetry.addData("Max Velocity", maxVelocity);
        telemetry.addData("Velocity", "%6.2f", motor.getVelocity());
        telemetry.addData("Power", "%6.2f", motor.getPower());
        telemetry.addData("Busy", motor.isBusy());
        telemetry.addData("Current (milli amps)", "%6.2f", motor.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Mode", motor.getMode());
        telemetry.addData("PIDF Run Using", motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER));
        telemetry.update();
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

    /**
     * This seems to be the only way to reliably stop a motor and reset the encoder.
     * This wos only tested on a goBilda motor.
     *
     * @param motor
     */
    private void stopAndResetEncoder(DcMotorEx motor) {
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void findMaxVelocity() {
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(1);
        timer.reset();
        double velocity;
        while (timer.seconds() < 3) {
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
    }

    /*
     * */
    private void updatePIDF() {
        pidfVelocityCoefficients.p = velocityP;
        pidfVelocityCoefficients.i = velocityI;
        pidfVelocityCoefficients.d = velocityD;
        pidfVelocityCoefficients.f = velocityF;
        motor.setVelocityPIDFCoefficients(pidfVelocityCoefficients.p, pidfVelocityCoefficients.i, pidfVelocityCoefficients.d, pidfVelocityCoefficients.f);
    }
}
