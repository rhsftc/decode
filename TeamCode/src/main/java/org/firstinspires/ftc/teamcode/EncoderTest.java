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

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/*
 * Test changing PIDF values for motor encoders.
 * Use it to experiment with PIDF values.
 */
@TeleOp(name = "Encoder Test", group = "test")
//@Disabled
public class EncoderTest extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final ElapsedTime timer = new ElapsedTime();
    private double maxVelocity = 0;
    private DcMotorEx motor;
    private DcMotor.RunMode mode;
    private final double ENCODER_INCREMENT = maxVelocity * 2f; //seconds
    private final double RUN_VELOCITY = maxVelocity * .9f;

    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Dpad Up and Dpad Down");
        telemetry.update();
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        // Find the maximum velocity of the motor.
        findMaxVelocity();
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        PIDFCoefficients pidfVelocityCoefficients = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidfPositionCoefficients = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);
        pidfVelocityCoefficients.p = 1.063f;
        pidfVelocityCoefficients.i = 1.063f;
        pidfVelocityCoefficients.f = 10.63f;
        motor.setVelocityPIDFCoefficients(pidfVelocityCoefficients.p, pidfVelocityCoefficients.i, pidfVelocityCoefficients.d, pidfVelocityCoefficients.f);
        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, pidfPositionCoefficients);
        motor.setPositionPIDFCoefficients(10f);
        motor.setTargetPositionTolerance(5);
    }

    /**
     * This method will be called repeatedly during the period between when
     * the init button is pressed and when the play button is pressed (or the
     * OpMode is stopped).
     */
    @Override
    public void init_loop() {
        telemetry.addLine("Dpad Up and Dpad Down");
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
        motor.setPower(0);
    }

    /**
     * This method will be called repeatedly during the period between when
     * the play button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Run Time: " + runtime);
        if (gamepad1.dpadUpWasReleased()) {
            motor.setTargetPosition((int) (motor.getCurrentPosition() + ENCODER_INCREMENT));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(RUN_VELOCITY);
        }

        if (gamepad1.dpadDownWasReleased()) {
            motor.setTargetPosition((int) (motor.getCurrentPosition() - ENCODER_INCREMENT));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(RUN_VELOCITY);
        }

        if (gamepad1.yWasReleased()) {
            stopAndResetEncoder(motor);
        }

        mode = motor.getMode();
        telemetry.addData("Max Velocity", maxVelocity);
        telemetry.addData("Target", "%d", motor.getTargetPosition());
        telemetry.addData("Position", "%d", motor.getCurrentPosition());
        telemetry.addData("Velocity", "%6.2f", motor.getVelocity());
        telemetry.addData("Power", "%6.2f", motor.getPower());
        telemetry.addData("Busy", motor.isBusy());
        telemetry.addData("Current (milli amps)", "%6.2f", motor.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Mode", mode);
        telemetry.addData("PIDF Run Using", motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER));
        telemetry.addData("PIDF Run To Position", motor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION));
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
        while (timer.seconds() < 4) {
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
}
