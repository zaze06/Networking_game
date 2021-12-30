package me.alien.game;

public class Game {
    public static final String VERSION = "0.0.2-DEV";


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
