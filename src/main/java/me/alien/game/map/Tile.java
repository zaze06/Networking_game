package me.alien.game.map;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.ArrayList;

public class Tile {
    private final boolean isEnd;
    protected int x;
    protected int y;
    protected final boolean walkable;
    protected final Color color;

    private static int Y_POSITION = 0x30;
    private static int X_POSITION = 0x30;
    private static int WALKABLE = 0x32;
    private static int COLOR = 0x31;

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

    public char[] toData(int xOffset, int yOffset){
        ArrayList<Character> out = new ArrayList<>();
        out.add((char) 0x26);
        out.add((char) (X_POSITION + (x+xOffset)));
        out.add((char) 0x23);
        out.add((char) (Y_POSITION + (y+yOffset)));
        out.add((char) 0x24);
        out.add((char) (WALKABLE + (walkable?1:2)));
        out.add((char) 0x25);
        out.add((char) (COLOR + color.getRed()));
        out.add((char) (COLOR + color.getGreen()));
        out.add((char) (COLOR + color.getBlue()));
        Object[] tmp1 = out.toArray();
        char[] tmp = new char[tmp1.length];
        for(int i = 0; i < tmp1.length; i++){
            tmp[i] = (char) tmp1[i];
        }
        return tmp;
    }

    public static Tile fromData(char[] data){
        int x = 0;
        int y = 0;
        boolean walkible = false;
        Color color = null;
        for(int i = 0; i < data.length; i++){
            char c = data[i];
            if(c == (0x24)){
                int tmp = (data[i+1] - WALKABLE);
                walkible = (tmp==1?true:false);
            }else if(c == 0x26){
                x = (data[i+1]-X_POSITION);
            }else if(c == 0x23){
                y = (data[i+1]-Y_POSITION);
            }else if(c == 0x25){
                color = new ColorUIResource(data[i+1] - COLOR, data[i+2] - COLOR,data[i+3] - COLOR);
            }
        }
        return new Tile(walkible, x, y, color, true);
    }

    public String toData(){
        StringBuilder out = new StringBuilder();
        for(char character : toData(0, 20)){
            out.append(character);
        }
        return out.toString();
    }

    public String toString(int xOffset, int yOffset) {
        return "{" +
                "\"type\":" + 0 +
                ",\"x\":" + (x+xOffset) +
                ",\"y\":" + (y+yOffset) +
                ",\"walkable\":" + walkable +
                ",\"color\":" + color.getRGB() +
                "}";
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
