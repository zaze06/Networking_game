package me.alien.game.util;

import me.alien.game.Server;

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
            e.printStackTrace();
        }
        ReceiveThread receiveThread = new ReceiveThread();
        receiveThread.start();
        SendThread sendThread = new SendThread();
        sendThread.start();
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

    public int getHp() {
        return hp;
    }

    public class ReceiveThread extends Thread{
        @Override
        public void run() {
            boolean exception = false;
            while(true){
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    dataIn.add(in.readLine());
                    exception = false;
                }catch (Exception e){
                    if((!exception)){
                        e.printStackTrace();
                        exception = true;
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
                    out.println(dataOut.get(0));
                    System.out.println("Sending data: "+dataOut.get(0)+". To "+socket.getInetAddress().getHostAddress()+" whit name: "+name+". Client id"+ID);
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
}
