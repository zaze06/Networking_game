package me.alien.game;

import me.alien.game.util.*;
import me.alien.game.util.Client;
import me.alien.game.util.data.display.DataString;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.*;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static me.alien.game.Game.VERSION;

public class Server {
    private static ArrayList<Client> clients = new ArrayList<>();
    ServerSocket serverSocket;
    private static ArrayList<Pair<Integer, Data>> dataOut = new ArrayList<>();

    public Server(){
        try{
            serverSocket = new ServerSocket(3030);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(20);
        }
        ClientAcceptThread clientAcceptThread = new ClientAcceptThread();
        clientAcceptThread.start();
        ClientSendThread clientSendThread = new ClientSendThread();
        clientSendThread.start();
        System.out.println("Listening on ip: "+serverSocket.getInetAddress().getHostAddress()+" on port: "+serverSocket.getLocalPort());
    }

    public static void remove(Client client) {
        dataOut.add(new Pair<>(client.getID(), new Data(Operation.CHAT, client.getName()+" Has disconnected")));
    }

    private class ClientAcceptThread extends Thread{
        @Override
        public void run() {
            while (true){
                try{
                    Socket socket = serverSocket.accept();
                    System.out.println("New client ip: "+socket.getInetAddress().toString()+" port: "+socket.getPort());
                    //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    //out.write(new Data(Operation.JOIN, "Recived join request").toString());
                    //out.write("Received join request");
                    //out.flush();
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("Starts handshake");
                    out.println(new Data(Operation.JOIN, "\"Recived join request\"").toString());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    JSONObject data = null;
                    String line = in.readLine();
                    System.out.println("Got text \""+line+"\"");
                    //while(data == null) {
                    data = new JSONObject(line);
                    //}
                    System.out.println("checking versions");
                    if(!(data.getJSONObject("data")).getString("version").equalsIgnoreCase(VERSION)){
                        out.println(new Data(Operation.ERROR, "\"Outdated version\""));
                        System.out.println("Handshake ended in Outdated version of client");
                    }else {
                        System.out.println("Handshake ended in success. Prosesing new client");
                        out.println(new Data(Operation.SUCCESS, "\"Client version match server version. Waiting for name\""));
                        String rawData = in.readLine();
                        System.out.println(rawData);
                        data = new JSONObject(rawData);
                        String name = (data.getJSONObject("data")).getString("name");
                        int id = clients.size() + 1;
                        Client client = new Client(socket, name, id);
                        clients.add(client);
                        System.out.println("New client added ip: "+client.getSocket().getInetAddress().toString()+" name: "+client.getName());
                        dataOut.add(new Pair<>(id, new Data(Operation.CHAT, "\"" + name + " have joined the game\"")));
                        synchronized (dataOut){
                            dataOut.notifyAll();
                        }
                        client.send(new Data(Operation.DISPLAY_DATA, new TimedPair<Integer, JSONObject>(-1, new JSONObject(new DataString(10,10,"Connected!", Color.BLACK).toString()), 10).toString()).toString());
                    }
                }catch (Exception e){

                }
            }
        }
    }

    private class ClientSendThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    synchronized (dataOut){
                        dataOut.wait();
                    }
                    System.out.println("Sending out data to clients\n"+dataOut.get(0));
                    for(Client client : clients){
                        client.send(dataOut.get(0).getValue().toString());
                    }
                    dataOut.remove(0);
                }catch (Exception e){

                }
            }
        }
    }
}
