package me.alien.game;

public class Game {
    public static final String VERSION = "0.0.1-DEV";


    public static void main(String[] args) {
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("server")){
                new Server();
            }else{
                new Client("127.0.0.1"/*args[0]*/, 3030);
            }
        }
    }
}
