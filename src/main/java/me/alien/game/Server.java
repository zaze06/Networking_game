package me.alien.game;

import me.alien.game.map.Tile;
import me.alien.game.util.*;
import me.alien.game.util.Client;
import me.alien.game.util.data.DisplayData;
import me.alien.game.util.data.Movement;
import me.alien.game.util.data.display.DataRectangle;
import me.alien.game.util.data.display.DataString;
import org.json.JSONObject;


import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static me.alien.game.Game.VERSION;

public class Server {
    private static final ArrayList<Client> clients = new ArrayList<>();
    ServerSocket serverSocket;
    private static final ArrayList<Pair<Integer, Data>> dataOut = new ArrayList<>();
    private static ArrayList<Tile> map = new ArrayList<>();

    // TODO: Make a game.

    public Server() {
        try {
            serverSocket = new ServerSocket(3030);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(20);
        }

        genNewMap();



        ClientAcceptThread clientAcceptThread = new ClientAcceptThread();
        clientAcceptThread.start();
        ClientSendThread clientSendThread = new ClientSendThread();
        clientSendThread.start();
        System.out.println("Listening on ip: "+serverSocket.getInetAddress().getHostAddress()+" on port: "+serverSocket.getLocalPort());
    }

    public static void remove(Client client) {
        clients.remove(client);
        map.remove(client.getPlayer());
        dataOut.add(new Pair<>(client.getID(), new Data(Operation.CHAT, client.getName()+" Has disconnected")));
        System.out.println("client id "+client.getID()+" ip "+client.getSocket().getInetAddress().getHostAddress()+" have left");
    }

    public static void move(Client client, JSONObject data) {
        map = Movement.move(map, client, data);
        dataOut.add(new Pair<>(-1, Map.sendData(map)));
        synchronized (dataOut) {
            dataOut.notifyAll();
        }
    }

    public static void genNewMap() {
        Tile[][] map;

        MazeGenerator maze = new MazeGenerator(35,35);

        int[][] mapI = maze.getIntMaze();//maze.toIntArray();

        int x = (int) (Math.random() * mapI.length);
        int y = (int) (Math.random() * mapI[x].length);
        while(mapI[x][y] != 0){
            x = (int) (Math.random() * mapI.length);
            y = (int) (Math.random() * mapI[x].length);
        }
        mapI[x][y] = 2;

        map = Map.fromIntArray(mapI);

        Server.map.clear();

        for (Tile[] tiles : map) {
            Server.map.addAll(Arrays.asList(tiles));
        }
        for(Client client : clients){
            client.getPlayer().setPos(10,10);
            Server.map.add(client.getPlayer());
        }


        dataOut.add(new Pair<>(-1, Map.sendData(Server.map)));
        synchronized (dataOut) {
            dataOut.notifyAll();
        }
    }

    private class ClientAcceptThread extends Thread{
        @Override
        public void run() {
            boolean exception = false;
            while (true){
                try{
                    Socket socket = serverSocket.accept();
                    System.out.println("New client ip: "+socket.getInetAddress().toString()+" port: "+socket.getPort());
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("Starts handshake");
                    out.println(new Data(Operation.JOIN, "\"Received join request\""));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    JSONObject data;
                    String line = in.readLine();
                    System.out.println("Got text \""+line+"\"");
                    //while(data == null) {
                    data = new JSONObject(line);
                    //}
                    System.out.println("checking versions");
                    Version version = new Version((data.getJSONObject("data")).getString("version"));
                    System.out.println("Client version: "+version+"\nServer version: "+VERSION);
                    if(!version.equals(VERSION)){
                        out.println(new Data(Operation.ERROR, "\"Outdated version\""));
                        System.out.println("Handshake ended in Outdated version of client");
                    }else {
                        System.out.println("Handshake ended in success. Prosecuting new client");
                        out.println(new Data(Operation.SUCCESS, "\"Client version match server version. Waiting for name\""));
                        String rawData = in.readLine();
                        System.out.println(rawData);
                        data = new JSONObject(rawData);
                        String name = (data.getJSONObject("data")).getString("name");
                        int id = clients.size() + 1;
                        Client client = new Client(socket, name, id);
                        clients.add(client);
                        System.out.println("New client added ip: "+client.getSocket().getInetAddress().toString()+" name: "+client.getName());
                        client.send(new Data(Operation.CHAT, "\"" + "Welcome " + name + ". The game will start soon\"").toString());
                        client.send(new Data(Operation.DISPLAY_DATA, new TimedPair<>(-1, new JSONObject(new DataString(0,10,"Connected!", Color.BLACK).toString()), 10).toString()).toString());
                        client.send(new Data(Operation.DISPLAY_DATA, new Pair<Integer, DisplayData>(0, new DataRectangle(0,10,client.getPlayer().getHp()*10,10, Color.red, true)).toString()).toString());
                        map.add(client.getPlayer());
                        client.send(Map.sendData(map).toString());

                        dataOut.add(new Pair<>(id, new Data(Operation.CHAT, "\"" + name + " have joined the game\"")));
                        synchronized (dataOut){
                            dataOut.notifyAll();
                        }
                        exception = false;
                    }
                }catch (Exception e){
                    if(!exception){
                        e.printStackTrace();
                    }
                    exception = true;
                }
            }
        }
    }

    private static class ClientSendThread extends Thread{
        @Override
        public void run() {
            boolean exception = false;
            while(true){
                try{
                    synchronized (dataOut){
                        dataOut.wait();
                    }
                    Pair<Integer, Data> sendData = dataOut.get(0);
                    System.out.println("Sending out data to clients\n"+ sendData);
                    for(Client client : clients){
                        if(sendData.getKey() != client.getID()) {
                            client.send(sendData.getValue().toString());
                        }
                    }
                    dataOut.remove(0);
                    exception = false;
                }catch (Exception e){
                    if(!exception){
                        e.printStackTrace();
                        exception = true;
                    }
                }
            }
        }
    }
}
