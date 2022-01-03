package me.alien.game.map;

import java.awt.*;

public class Tile {
    private final boolean isEnd;
    protected int x;
    protected int y;
    protected final boolean walkable;
    protected final Color color;

    public Tile(boolean walkable, int x, int y, Color color, boolean exactCords) {
        this(walkable, x, y, color, exactCords, false);
    }

    public Tile(boolean walkable, int x, int y, Color color, boolean exactCords, boolean isEnd){
        this.walkable = walkable;
        this.color = color;
        this.x = (x*(exactCords?1:10));
        this.y = (y*(exactCords?1:10));
        this.isEnd = isEnd;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = (y*10);
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = (x*10);
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void addY(int distance) {
        y += (distance);
    }

    public void addX(int distance) {
        x += (distance);
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fill3DRect(x,y,10,10,true);
    }

    @Override
    public String toString() {
        return toString(0,20);
    }

    public String toString(int xOffset, int yOffset) {
        return "{" +
                "\"type\": " + 0 +
                ", \"x\": " + (x+xOffset) +
                ", \"y\": " + (y+yOffset) +
                ", \"walkable\": " + walkable +
                ", \"color\": " + color.getRGB() +
                "}";
    }
}
