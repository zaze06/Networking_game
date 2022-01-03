package me.alien.game.util;

public class JSONString {
    private final String data;

    public JSONString(String data){
        this.data = data;
    }

    @Override
    public String toString() {
        return "\"" + data + '\"';
    }
}
