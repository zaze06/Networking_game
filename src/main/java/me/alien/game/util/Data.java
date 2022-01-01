package me.alien.game.util;

public class Data {
    int operation;
    String data;

    public Data(int operation, String data) {
        this.operation = operation;
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "\"operation\": " + operation +
                ", \"data\": " + data +
                '}';
    }
}
