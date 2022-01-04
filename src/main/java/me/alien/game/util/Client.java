package me.alien.game.util;

import me.alien.game.Server;
import me.alien.game.map.Player;
import me.alien.game.map.Tile;
import org.json.JSONObject;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    Socket socket;
    String name;
    final int ID;
    ArrayList<String> dataIn;
    final ArrayList<String> dataOut;
    BufferedReader in;
    PrintWriter out;
    Client client;
    Player player;


    public Client(Socket socket, String name, int ID) {
        this.socket = socket;
        this.name = name;
        this.ID = ID;
        dataIn = new ArrayList<>();
        dataOut = new ArrayList<>();
        int R = (int)(Math.random()*255);
        int G = (int)(Math.random()*255);
        int B = (int)(Math.random()*255);
        player = new Player(3,true,1,1, new ColorUIResource(R,G,B), false);
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }catch (Exception e){
            e.printStackTrace();
        }
        ReceiveThread receiveThread = new ReceiveThread();
        receiveThread.start();
        SendThread sendThread = new SendThread();
        sendThread.start();
        KeyHandlerThread keyHandlerThread = new KeyHandlerThread();
        keyHandlerThread.start();
        client = this;
    }

    public void send(String data){
        try {
            dataOut.add(data);
            synchronized (dataOut){
                dataOut.notifyAll();
            }
        }catch (Exception e){
            System.out.println("Exception in client send\n"+e);
            out.println(new Data(Operation.ERROR, "\"An Exception occurred on the server for you. closing socket"));
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Server.remove(this);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public class ReceiveThread extends Thread{
        @Override
        public void run() {
            boolean exception = false;
            while(true){
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String e = in.readLine();
                    if(socket.isClosed()){
                        return;
                    }
                    if(e == null){
                        int x = 0;
                        throw new NullPointerException("e is null");
                    }
                    //System.out.println("received: "+e+" on client id "+ID);
                    if((new JSONObject(e)).getInt("operation") == Operation.EXIT){
                        out.println(new Data(Operation.EXIT, new JSONString("good bay").toString()));
                        Server.remove(client);
                        socket.close();
                        return;
                    }
                    dataIn.add(e);
                    synchronized (dataIn) {
                        dataIn.notifyAll();
                    }
                    exception = false;
                }catch (Exception e){
                    if((!exception)){
                        e.printStackTrace();
                        exception = true;
                    }
                    if(socket.isClosed()){
                        dataOut.notifyAll();
                        dataIn.notifyAll();
                        Server.remove(client);
                        this.stop();
                        return;
                    }
                }
            }
        }
    }

    public class SendThread extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    if(dataOut.size() == 0){
                        synchronized (dataOut){
                            dataOut.wait();
                        }
                    }
                    if(socket.isClosed()) {
                        this.stop();
                        return;
                    }
                    out.println(dataOut.get(0));
                    //System.out.println("Sending data: "+dataOut.get(0)+". To "+socket.getInetAddress().getHostAddress()+" whit name: "+name+". Client id"+ID);
                    dataOut.remove(0);
                }catch (Exception e){
                    System.out.println("ReceiveThread.run exception\n" + e);
                    Server.remove(client);
                }
            }
        }
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

    private class KeyHandlerThread extends Thread{
        @Override
        public void run() {
            while (true) {
                try {
                    if (dataIn.size() == 0) {
                        synchronized (dataIn) {
                            try {
                                dataIn.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (socket.isClosed()) {
                        this.stop();
                        return;
                    }
                    JSONObject data = new JSONObject(dataIn.get(0));
                    if (data.getInt("operation") == Operation.MOVEMENT_DATA) {
                        Server.move(client, data.getJSONObject("data"));
                        //dataIn.remove(0);
                    }
                    dataIn.remove(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
