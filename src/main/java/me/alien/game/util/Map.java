package me.alien.game.util;

import me.alien.game.exception.NoTileFoundException;
import me.alien.game.map.Player;
import me.alien.game.map.Tile;
import me.alien.game.util.data.display.DataMap;
import org.json.JSONObject;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.ArrayList;

public class Map {
    public static Tile findTile(ArrayList<Tile> map, int x, int y) throws NoTileFoundException {
        for(Tile tile : map){
            if(tile.getX() == x && tile.getY() == y){
                return tile;
            }
        }
        throw new NoTileFoundException("Tile x: "+x+" y: "+y+" dos not exist in provided map");
    }

    public static Data sendData(ArrayList<Tile> map) {
        try {
            JSONObject dataMap = new JSONObject();
            dataMap.put("size", map.size());
            for (int i = 0; i < map.size(); i++) {
                dataMap.put("tile" + i, map.get(i).toData());
            }
            return new Data(Operation.TILE_DATA, new Pair<>(1, new DataMap(dataMap, 0, 20)).toString());
        }catch (Exception e){

        }
        return null;
    }

    public static ArrayList<Tile> fromJson(JSONObject map){
        try {
            ArrayList<Tile> out = new ArrayList<>();
            for (int i = 0; i < map.getInt("size"); i++) {
                JSONObject tile = map.getJSONObject("tile" + i);
                if (tile.getInt("type") == 0) {
                    out.add(new Tile(tile.getBoolean("walkable"), tile.getInt("x"), tile.getInt("y"), new ColorUIResource(tile.getInt("color")), true));
                } else if (tile.getInt("type") == 1) {
                    out.add(new Player(tile.getInt("hp"), tile.getBoolean("walkable"), tile.getInt("x"), tile.getInt("y"), new ColorUIResource(tile.getInt("color")), true));
                }
            }
            return out;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Tile[][] fromIntArray(int[][] mapI) {
        Tile[][] out = new Tile[mapI.length][mapI[0].length];
        int maxX = mapI.length;
        int maxY = mapI[0].length;
        for(int x = 0; x < maxX; x++){
            for(int y = 0; y < maxY; y++){
                if(mapI[x][y] == 2){
                    out[x][y] = new Tile(true, x, y, new ColorUIResource(0, 255, 46), false, true);
                }else {
                    out[x][y] = (mapI[x][y] == 0 ? new Tile(true, x, y, new ColorUIResource(238, 238, 238), false) : new Tile(false, x, y, Color.darkGray, false));
                }
            }
        }
        return out;
    }
}
