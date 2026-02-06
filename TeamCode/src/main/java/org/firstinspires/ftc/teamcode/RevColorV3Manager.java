package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * RevColorV3Manager - A class to manage Rev Color Sensor V3 functionality.
 */
public class RevColorV3Manager {
    //TODO: Tune the gain to optimize calibrated values.
    private float GAIN = 6.0f; // Sensor gain
    // Gain value for Low pass filter.
    // Lower values = more smoothing, but more lag.
    public boolean useRGB = true; // Flag to use RGB or HSV

    public enum CELL_COLOR {
        None,
        Green,
        Purple
    }

    // default constructor.
    public RevColorV3Manager() {
    }

    // Allow selection of RGB or HSV.
    public RevColorV3Manager(boolean useRGB) {
        this.useRGB = useRGB;
    }

    public CELL_COLOR GetCellColor(RevColorSensorV3 sensor) {
        return useRGB ? getCellColorRGB(sensor) : getCellColorHSV(sensor);
    }

    private CELL_COLOR getCellColorHSV(RevColorSensorV3 sensor) {
        float[] hsv = getHSV(sensor);
        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];

        // Simple threshold-based color detection using HSV
        if (hue >= 85 && hue <= 150 && saturation > 0.4 && value > 0.2) {
            return CELL_COLOR.Green;
        } else if (hue >= 250 && hue <= 290 && saturation > 0.4 && value > 0.2) {
            return CELL_COLOR.Purple;
        } else {
            return CELL_COLOR.None;
        }
    }

    private CELL_COLOR getCellColorRGB(RevColorSensorV3 sensor) {
        NormalizedRGBA colors = getRGBA(sensor);
        float red = colors.red;
        float green = colors.green;
        float blue = colors.blue;

        // Simple threshold-based color detection
        //TODO: Tune these thresholds based on testing in opmode SensorColor.
        if (green > 0.15 && red > 0.04 && blue > 0.12) {
            return CELL_COLOR.Green;
        } else if (blue > 0.11 && red > 0.065 && green > 0.07) {
            return CELL_COLOR.Purple;
        } else {
            return CELL_COLOR.None;
        }
    }

    /*
        Return Normalized RGBA values.
     */
    public NormalizedRGBA getRGBA(RevColorSensorV3 colorSensor) {
        setSensorGain(colorSensor);
        return colorSensor.getNormalizedColors();
    }

    /*
        Returns HSV values.
     */
    public float[] getHSV(RevColorSensorV3 colorSensor) {
        NormalizedRGBA colors = getRGBA(colorSensor);
        float[] hsvValues = new float[3];
        // Convert the RGB values to HSV values.
        Color.colorToHSV(colors.toColor(), hsvValues);
        return hsvValues;
    }

    // Set the gain for the color sensor
    public void setSensorGain(RevColorSensorV3 colorSensor) {
        colorSensor.setGain(GAIN);
    }
}
