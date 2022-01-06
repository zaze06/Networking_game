package me.alien.game;

import me.alien.game.map.Tile;

import java.awt.*;

public class tmp {
    public static void main(String[] args){
        Tile tile = new Tile(true, 0, 0, Color.red, false);
        System.out.println(tile.toData());
        System.out.println(tile); // this
        System.out.println("\n");
        System.out.println("tile from data: "+ Tile.fromData(tile.toData(0, 20))); // and this should be the same
    }
}
