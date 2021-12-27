package me.alien.game.util;

import java.sql.Timestamp;
import java.time.*;

public class TimedPair<K, V> extends Pair<K, V>{
    Timestamp time;

    /**
     *
     * @param key
     * @param value
     * @param time how long in seconds to keep this object
     */
    public TimedPair(K key, V value, int time) {
        super(key, value);
        this.time = new Timestamp(System.currentTimeMillis()+(time*1000));
    }

    public boolean checkTime(){
        Timestamp curentTime = new Timestamp(System.currentTimeMillis());
        return time.equals(curentTime) || time.before(curentTime);
    }

    @Override
    public String toString() {
        return "{" +
                "\"pairID\": 1" +
                ", \"key\": " + key.toString() +
                ", \"value\": " + value.toString() +
                ", \"time\": " + time.getTime() +
                '}';
    }
}
