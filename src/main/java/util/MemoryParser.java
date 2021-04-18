package util;

import environment.Coordinate;

import java.awt.*;

public class MemoryParser {

    public static Color parseColorFromMessage(String msg){
        return Color.getColor(msg.split(";")[1]);
    }

    public static Coordinate parseCoordinateFromMessage(String msg){
        return Coordinate.fromString(msg.split(";")[2]);
    }
}
