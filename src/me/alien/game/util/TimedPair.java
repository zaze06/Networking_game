package me.alien.game.util;

import java.sql.Timestamp;
import java.time.*;

public class TimedPair<K, V> extends Pair<K, V>{
    Instant time;

    /**
     *
     * @param key
     * @param value
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
        Instant curentTime = Instant.now();
        return time.equals(curentTime) || time.isBefore(curentTime);
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
