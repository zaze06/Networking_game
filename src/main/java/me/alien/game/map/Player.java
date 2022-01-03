package me.alien.game.map;

import java.awt.*;

public class Player extends Tile{
    int hp;

    public Player(int hp, boolean walkable, int x, int y, Color color, boolean exactCords) {
        super(walkable, x, y, color, exactCords);
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    public int removeHp(int damage){
        return hp -= damage;
    }

    @Override
    public String toString(int xOffset, int yOffset) {
        return "{" +
                "\"type\": " + 1 +
                ", \"x\": " + (x+xOffset) +
                ", \"y\": " + (y+yOffset) +
                ", \"walkable\": " + walkable +
                ", \"color\": " + color.getRGB() +
                ", \"hp\": " + hp +
                "}";
    }
}
