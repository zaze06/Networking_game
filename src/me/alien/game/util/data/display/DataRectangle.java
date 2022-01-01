package me.alien.game.util.data.display;

import me.alien.game.util.data.DisplayData;

import java.awt.*;

public class DataRectangle extends DisplayData {

    boolean fill;
    Rectangle rectangle;
    Color color;

    public DataRectangle(int x, int y, int width, int height, Color color, boolean fill) {
        super(x, y);
        rectangle = new Rectangle(x,y,width,height);
        this.color = color;
        this.fill = fill;
    }

    @Override
    public String toString() {
        return "{" +
                    "\"type\": " + DisplayData.RECTANGLE +
                    ", \"x\": " + x +
                    ", \"y\": " + y +
                    ", \"data\": {" +
                        "\"x\": " + rectangle.x +
                        ", \"y\": " + rectangle.y +
                        ", \"width\": " + rectangle.width +
                        ", \"height\": " + rectangle.height +
                        ", \"fill\": " + fill +
                    "}" +
                    ", \"color\": " + color.getRGB() +
                '}';
    }
}
