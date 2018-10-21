package com.cryoingdevs.common;

import java.awt.*;

/**
 * Created by IvànAlejandro on 20/10/2018.
 */
public class ColorUtils {

    // Color for ice
    private static final double HSB_COLOR_MIN_BLUE = 0.58;
    private static final double HSB_COLOR_MAX_BLUE = 0.759999;

    // Color for water
    private static final double HSB_COLOR_MIN_LIGHT_BLUE = 0.43913043;
    private static final double HSB_COLOR_MAX_LIGHT_BLUE = 0.57999999;

    // Color for earth
    private static final double HSB_COLOR_MIN_LOWER_RED = 0;
    private static final double HSB_COLOR_MAX_LOWER_RED =0.08045977;

    private static final double HSB_COLOR_MIN_UPPER_RED = 0.908046;
    private static final double HSB_COLOR_MAX_UPPER_RED = 0.999999;

    public static boolean isWhite(int r, int g, int b){
        if(( r>=0 && r<=10 ) && ( g>=0 && g<=10) && ( b>=0 && b<=10)){
            return true;
        }
        return false;
    }

    public static boolean isFrozenWater(float hue) {
        if (hue >= HSB_COLOR_MIN_BLUE && hue <= HSB_COLOR_MAX_BLUE){
            return true;
        }
        return false;
    }

    public static boolean isCryogenicArea(int red, int green, int blue) {
        float[] hsv = Color.RGBtoHSB(red, green, blue, null);
        float hue = hsv[0];
        return isWhite(red, green, blue) || isFrozenWater(hue);
    }

    public static boolean isLiquidWater(float hue) {
        if (hue >= HSB_COLOR_MIN_LIGHT_BLUE && hue <= HSB_COLOR_MAX_LIGHT_BLUE){
            return true;
        }
        return false;
    }

    public static boolean isEarth(float hue) {
        if ((hue >= HSB_COLOR_MIN_LOWER_RED && hue <= HSB_COLOR_MAX_LOWER_RED) || (hue >= HSB_COLOR_MIN_UPPER_RED && hue <= HSB_COLOR_MAX_UPPER_RED)) {
            return true;
        }
        return false;
    }

}
