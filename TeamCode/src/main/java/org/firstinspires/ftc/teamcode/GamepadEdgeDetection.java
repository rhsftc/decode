/*
Copyright (c) 2024 Miriam Sinton-Remes

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of FIRST nor the names of its contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

/*
 * This OpMode illustrates using edge detection on a gamepad.
 * GamepadEx from SolversLib only works in linear OpModes!
 *
 * Simply checking the state of a gamepad button each time could result in triggering an effect
 * multiple times. Edge detection ensures that you only detect one button press, regardless of how
 * long the button is held.
 *
 * There are two main types of edge detection. Rising edge detection will trigger when a button is
 * first pressed. Falling edge detection will trigger when the button is released.
 *
 */

//@Disabled
@TeleOp(name = "Gamepad Edge Detection", group = "test")
public class GamepadEdgeDetection extends LinearOpMode {
    private final long LOOP_DELAY = 500; // milliseconds
    private GamepadEx gamepadEx;
    private TriggerReader leftTriggerReader;

    @Override
    public void runOpMode() {
        gamepadEx = new GamepadEx(gamepad1);
        leftTriggerReader = new TriggerReader(gamepadEx, GamepadKeys.Trigger.LEFT_TRIGGER);
        // Wait for the DS start button to be pressed
        waitForStart();

        while (opModeIsActive()) {
            gamepadEx.readButtons();
            // Update the telemetry
            telemetryButtonData();

            // Wait before doing another check
            sleep(LOOP_DELAY);
        }
    }

    public void telemetryButtonData() {
        telemetry.addData("GamepadEx A Pressed", gamepadEx.wasJustPressed(GamepadKeys.Button.A));
        telemetry.addData("GamepadEx A Released", gamepadEx.wasJustReleased(GamepadKeys.Button.A));
        telemetry.addLine();

        telemetry.addData("GamepadEx Left Bumper Pressed", gamepadEx.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER));
        telemetry.addData("GamepadEx Left Bumper Released", gamepadEx.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER));
        telemetry.addData("GamepadEx Left Bumper Is down", gamepadEx.isDown(GamepadKeys.Button.LEFT_BUMPER));
        // Add an empty line to separate the buttons in telemetry
        telemetry.addLine();

        //TODO: The trigger reader seems to fail.
        telemetry.addData("GamepadEx Left trigger State changed", leftTriggerReader.stateJustChanged());
        telemetry.addData("GamepadEx Left trigger Pressed", leftTriggerReader.wasJustPressed());
        telemetry.addData("GamepadEx Left trigger Released", leftTriggerReader.wasJustReleased());
        telemetry.addData("GamepadEx Left trigger Is down", leftTriggerReader.isDown());

        // Add a note that the telemetry is only updated every 2 seconds
        telemetry.addData("\nTelemetry is updated every ", "%d", LOOP_DELAY);

        // Update the telemetry on the DS screen
        telemetry.update();
    }
}
