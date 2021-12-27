package me.alien.game.util;

import me.alien.game.Game;
import me.alien.game.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Client {
    Socket socket;
    String name;
    final int ID;
    ArrayList<String> data;

    public Client(Socket socket, String name, int ID) {
        this.socket = socket;
        this.name = name;
        this.ID = ID;
        data = new ArrayList<>();
        ReciveThread reciveThread = new ReciveThread();
        reciveThread.start();

    }

    public void send(String data){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(data);
        }catch (Exception e){
            Server.remove(this);
        }
    }

    public class ReciveThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    data.add(in.readLine());
                }catch (Exception e){

                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return ID == client.ID && Objects.equals(socket, client.socket) && Objects.equals(name, client.name) && Objects.equals(data, client.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, name, ID, data);
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public Socket getSocket() {
        return socket;
    }
}
