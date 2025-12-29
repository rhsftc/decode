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
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

/*
 * Demonstrates gamepad edge detection using the GamepadEx and TriggerReader classes.
 */
@TeleOp(name = "Gamepad Test", group = "test")
//@Disabled
public class GamepadTest extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime telemetryTimer = new ElapsedTime();
    private final long LOOP_DELAY = 2000; // milliseconds
    private GamepadEx gamepadEx;
    private TriggerReader triggerReader;

    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        gamepadEx = new GamepadEx(gamepad1);
        triggerReader = new TriggerReader(
                gamepadEx, GamepadKeys.Trigger.LEFT_TRIGGER
        );
    }

    /**
     * This method will be called repeatedly during the period between when
     * the INIT button is pressed and when the START button is pressed (or the
     * OpMode is stopped).
     */
    @Override
    public void init_loop() {
    }

    /**
     * This method will be called once, when the START button is pressed.
     */
    @Override
    public void start() {
        runtime.reset();
        telemetryTimer.reset();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the START button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
        gamepadEx.readButtons();
//        triggerReader.readValue();
//        telemetry.addData("Status", "Run Time: " + runtime.toString());
        if ((telemetryTimer.milliseconds() >= LOOP_DELAY)) {
            telemetryTimer.reset();
        } else {
            telemetryButtonData();
        }
    }

    /**
     * This method will be called once, when this OpMode is stopped.
     * <p>
     * Your ability to control hardware from this method will be limited.
     */
    @Override
    public void stop() {

    }

    private void telemetryButtonData() {
        // Add the status of the GamepadEx Left Bumper
        telemetry.addData("Gamepad A Pressed", gamepad1.aWasPressed());
        telemetry.addData("Gamepad A Released", gamepad1.aWasReleased());
        telemetry.addData("Gamepad A", gamepad1.a);
        telemetry.addData("GamepadEx Left Bumper Pressed", gamepadEx.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER));
        telemetry.addData("GamepadEx Left Bumper Released", gamepadEx.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER));
        telemetry.addData("GamepadEx Left Bumper IsDown", gamepadEx.isDown(GamepadKeys.Button.LEFT_BUMPER));

        // Add an empty line to separate the buttons in telemetry
        telemetry.addLine();

        // Add the status of the GamepadEx Left Trigger
        telemetry.addData("GamepadEx Left Trigger value", gamepadEx.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER));
        telemetry.addData("GamepadEx Left Trigger IsDown", triggerReader.isDown());
        telemetry.addData("GamepadEx Left Trigger Pressed", triggerReader.wasJustPressed());
        telemetry.addData("GamepadEx Left Trigger Released", triggerReader.wasJustReleased());
        telemetry.addData("GamepadEx Left Trigger Changed", triggerReader.stateJustChanged());

        telemetry.addData("\nTelemetry is updated every %5f milliseconds.", LOOP_DELAY);

        // Update the telemetry on the DS screen
        telemetry.update();
    }
}
