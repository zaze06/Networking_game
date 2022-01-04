package me.alien.game;

import me.alien.game.util.Version;

public class Game {
    public static final Version VERSION = new Version("0.2");


    public static void main(String[] args) {
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("server")){
                new Server();
            }else{
                new Client(args[0], 3030);
            }
        }
    }
}
