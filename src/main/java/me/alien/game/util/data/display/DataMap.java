package me.alien.game.util.data.display;

import me.alien.game.util.data.DisplayData;
import org.json.JSONObject;

import java.awt.*;

public class DataMap extends DisplayData {
    JSONObject dataMap;

    public DataMap(JSONObject dataMap, int x, int y) {
        super(x,y);
        this.dataMap = dataMap;
    }

    @Override
    public String toString() {
        return "{"+
                    "\"type\": " + DisplayData.MAP +
                    ", \"x\": " + x +
                    ", \"y\": " + y +
                    ", \"data\": " + dataMap.toString() +
                    ", \"color\": " + Color.BLACK.getRGB() +
                "}";
    }
}
