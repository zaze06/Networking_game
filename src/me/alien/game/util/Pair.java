package me.alien.game.util;

public class Pair<K, V> {
    K key;
    V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Pair(){
        key = null;
        value = null;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "\"pairID\": 0" +
                "\"key\": " + key.toString() +
                ", \"value\": " + value.toString() +
                '}';
    }
}
