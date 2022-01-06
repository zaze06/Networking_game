package me.alien.game.util;

import org.json.JSONObject;

public class Data {
    int operation;
    Object data;

    public Data(int operation, Object data) {
        this.operation = operation;
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "\"operation\":" + operation +
                ",\"data\":"+ data +
                '}';
    }

    public JSONObject toJSON() {
        return new JSONObject(toString());
    }
}
