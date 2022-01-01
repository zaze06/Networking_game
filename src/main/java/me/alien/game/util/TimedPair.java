package me.alien.game.util;

import java.time.*;

public class TimedPair<K, V> extends Pair<K, V>{
    Instant time;

    /**
     *
     * @param key what Object the key shall be
     * @param value what object the value shall be
     * @param time how long in seconds to keep this object
     */
    public TimedPair(K key, V value, int time) {
        this(key, value , Instant.now().plusSeconds(time));
    }

    public TimedPair(K key, V value, Instant time) {
        super(key, value);
        this.time = time;
    }

    public boolean checkTime(){
        Instant currentTime = Instant.now();
        return time.equals(currentTime) || time.isBefore(currentTime);
    }

    @Override
    public String toString() {
        return "{" +
                "\"pairID\": 1" +
                ", \"key\": " + key.toString() +
                ", \"value\": " + value.toString() +
                ", \"time\": \"" + time.toString() + "\"" +
                '}';
    }
}
