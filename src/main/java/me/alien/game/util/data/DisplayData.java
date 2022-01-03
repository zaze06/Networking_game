package me.alien.game.util.data;

public class DisplayData {
    public static final int STRING = 0;
    public static final int RECTANGLE = 1;
    public static final int MAP = 2;

    protected int x;
    protected int y;

    public DisplayData(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
