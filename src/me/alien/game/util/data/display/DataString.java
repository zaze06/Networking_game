package me.alien.game.util.data.display;

import me.alien.game.util.data.DisplayData;

import java.awt.*;

public class DataString extends DisplayData {
    String data;
    Color color;

    public DataString(int x, int y, String data, Color color) {
        super(x, y);
        this.data = data;
        this.color = color;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\": " + DisplayData.STRING +
                ", \"x\": " + x +
                ", \"y\": " + y +
                ", \"data\": \"" + data + '\"' +
                ", \"color\": " + color.getRGB() +
                '}';
    }
}
