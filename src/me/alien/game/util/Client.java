package me.alien.game.util;

import me.alien.game.Server;
import me.alien.game.util.data.DisplayData;
import me.alien.game.util.data.display.DataRectangel;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class Client {
    Socket socket;
    String name;
    final int ID;
    ArrayList<String> dataIn;
    ArrayList<String> dataOut;
    BufferedReader in;
    PrintWriter out;
    Client client;

    // Game Variables
    int hp;


    public Client(Socket socket, String name, int ID) {
        this.socket = socket;
        this.name = name;
        this.ID = ID;
        dataIn = new ArrayList<>();
        dataOut = new ArrayList<>();
        hp = 3;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }catch (Exception e){

        }
        ReciveThread reciveThread = new ReciveThread();
        reciveThread.start();
        SendThread sendThread = new SendThread();
        sendThread.start();
        client = this;
    }

    public void send(String data){
        try {
            //System.out.println("sending data to client: "+socket.getInetAddress().getHostAddress());
            //System.out.println(data);
            dataOut.add(data);
            synchronized (dataOut){
                dataOut.notifyAll();
            }
        }catch (Exception e){
            System.out.println("Exception in client send\n"+e.toString());
            Server.remove(this);
        }
    }

    public int getHp() {
        return hp;
    }

    public class ReciveThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    dataIn.add(in.readLine());
                }catch (Exception e){

                }
            }
        }
    }

    public class SendThread extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    synchronized (dataOut){
                        dataOut.wait();
                    }
                    out.println(dataOut.get(0));
                    System.out.println("Sending data: "+dataOut.get(0)+". To "+socket.getInetAddress().getHostAddress()+" whit name: "+name+". Client id"+ID);
                    dataOut.remove(0);
                }catch (Exception e){
                    System.out.println("ReceiveThread.run exception\n" + e.toString());
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
}
