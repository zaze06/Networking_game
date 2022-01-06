package me.alien.game.util.data;

import me.alien.game.exception.NoTileFoundException;
import me.alien.game.map.Player;
import me.alien.game.map.Tile;
import me.alien.game.util.Client;
import me.alien.game.util.Data;
import me.alien.game.util.Map;
import me.alien.game.util.Operation;
import org.json.JSONObject;

import javax.swing.plaf.ColorUIResource;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Movement {
    public static JSONObject fromKey(int key, int distance) {
        try {
            JSONObject out = new JSONObject();
            int mode = -1;
            switch (key) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    mode = 0;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    mode = 1;
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    mode = 2;
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    mode = 3;
                    break;
                default:
                    return new Data(Operation.KEY_DATA, key).toJSON();
            }
            out.put("direction", mode);
            out.put("length", distance * 10);
            return new Data(Operation.MOVEMENT_DATA, out.toString()).toJSON();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Data(Operation.KEY_DATA, key).toJSON();
    }

    public static ArrayList<Tile> move(ArrayList<Tile> map, Client client, JSONObject data){
        try {
            ArrayList<Tile> newMap = (ArrayList<Tile>) map.clone();
            int direction = data.getInt("direction");
            int distance = data.getInt("length");
            int x = client.getPlayer().getX();
            int y = client.getPlayer().getY();
            int width = 100;
            int height = 100;
            if (direction == 0) {
                try {
                    Tile tile = Map.findTile(map, x, y - distance);
                    if (tile.isWalkable()) {
                        client.getPlayer().addY(-distance);
                    }
                } catch (NoTileFoundException e) { }
            } else if (direction == 1) {
                try {
                    Tile tile = Map.findTile(map, x, y + distance);
                    if (tile.isWalkable()) {
                        client.getPlayer().addY(distance);
                    }
                } catch (NoTileFoundException e) {}
            } else if (direction == 2) {
                try {
                    Tile tile = Map.findTile(map, x - distance, y);
                    if (tile.isWalkable()) {
                        client.getPlayer().addX(-distance);
                    }
                } catch (NoTileFoundException e) {}
            } else if (direction == 3) {
                try {
                    Tile tile = Map.findTile(map, x + distance, y);
                    if (tile.isWalkable()) {
                        client.getPlayer().addX(distance);
                    }
                } catch (NoTileFoundException e) {}
            }

            return newMap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}
