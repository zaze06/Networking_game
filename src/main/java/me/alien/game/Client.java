package me.alien.game;

import me.alien.game.map.Tile;
import me.alien.game.util.*;
import me.alien.game.util.data.DisplayData;
import me.alien.game.util.data.Movement;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static me.alien.game.Game.VERSION;

public class Client extends JFrame {
    Socket socket;
    ArrayList<String> dataOut;
    ArrayList<JSONObject> dataIn;
    Display display;
    javax.swing.JTextArea chatText;
    int width;
    int height;
    BufferedReader in;
    PrintWriter out;
    private ArrayList<String> dataInRaw;
    private final ArrayList<Integer> keysDown = new ArrayList<>();

    public Client(String ip, int port) {
        try {
            chatText = new JTextArea();
            System.out.println("Communicate with server "+ip+" at "+port);
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            JSONObject data = new JSONObject(line);
            out = new PrintWriter(socket.getOutputStream(), true);
            dataIn = new ArrayList<>();
            dataOut = new ArrayList<>();
            dataInRaw = new ArrayList<>();
            DataRawHandler dataRawHandler = new DataRawHandler();
            dataRawHandler.start();
            System.out.println("Server have accepted the socket, got \""+line+"\"");
            if(data.getInt("operation") == Operation.JOIN) {
                out.println(new Data(Operation.DATA,"{version: \""+VERSION+"\"}"));
            }else{
                System.out.println("Invalid packet: "+data.toString(4)+"\nContact server host for help");
                System.exit(0);
            }
            data = new JSONObject(in.readLine());
            if(data.getInt("operation") == Operation.ERROR){
                System.out.println("Failed to connect to server. reason: "+data.getString("data"));
                System.exit(100);
            }else if(data.getInt("operation") == Operation.SUCCESS){
                System.out.println("What is the wanted name?");
                String name = (new BufferedReader(new InputStreamReader(System.in)).readLine());
                out.println(new Data(Operation.DATA, "{\"name\": \""+name+"\"}"));
            }else{
                System.out.println("Invalid packet: "+data.toString(4)+"\nContact server host for help");
                System.exit(0);
            }
            setSize(600,400);
            display = new Display(100, this);
            display.setBounds(0,0,600,400);
            ReceiveThread receiveThread = new ReceiveThread();
            SendThread sendThread = new SendThread();
            KeyHandlerThread keyHandlerThread = new KeyHandlerThread();
            ChatThread chatThread = new ChatThread();
            keyHandlerThread.start();
            receiveThread.start();
            sendThread.start();
            chatThread.start();
            setLayout(new BorderLayout());

            chatText.setBounds(570,0,30,30);
            chatText.setEditable(false);
            //chatText.setEnabled(false);
            //chatText.setM
            chatText.append("Chat started\n");
            //this.setComponentZOrder(chatText, 0);
            //chatText




            add(display, BorderLayout.CENTER);
            add(chatText, BorderLayout.EAST);
            setName("Game");
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            /*addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    out.println(new Data(Operation.EXIT, new JSONString("Exiting").toString()));
                }
            });*/

            setVisible(true);


            //this.setComponentZOrder(chatText, 20);
            width = getWidth();
            height = getHeight();

            out.println(new Data(Operation.SUCCESS, "\"Setup complete. Waiting for data\""));
            chatText.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class KeyHandlerThread extends Thread
    {
        @Override
        public void run() {

        }
    }

    public void sendChat(String data){
        chatText.append(data);
        chatText.repaint();
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        dataOut.add(new Data(Operation.MOVEMENT_DATA, Movement.fromKey(e.getKeyCode(), 1).toString()).toString());
        synchronized (dataOut) {
            dataOut.notifyAll();
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public class ReceiveThread extends Thread{
        int counter = 0;
        @Override
        public void run() {
            while(true){
                try{
                    String dataRaw = in.readLine();
                    System.out.println("Received: "+dataRaw);
                    dataInRaw.add(dataRaw);
                    synchronized (dataInRaw){
                        dataInRaw.notifyAll();
                    }
                }catch (Exception e) {
                    if (counter < 10) {
                        System.out.println("ReceiveThread.run exception\n" + e);
                        counter++;
                    }
                }
            }
        }
    }

    private class DataRawHandler extends Thread{
        @Override
        public void run() {
            while (true) {
                try {
                    if (dataInRaw.size() == 0) {
                        synchronized (dataInRaw) {
                            dataInRaw.wait();
                        }
                    }
                    String dataRaw = dataInRaw.get(0);
                    dataInRaw.remove(0);
                    JSONObject data = new JSONObject(dataRaw);
                    if (data.getInt("operation") == Operation.DISPLAY_DATA) {

                        JSONObject data1 = data.getJSONObject("data");
                        if (data1.getInt("pairID") == 0) {
                            boolean found = false;
                            for (int i = 0; i < display.displayDataIn.size(); i++) {
                                Pair<Integer, JSONObject> displayData = display.displayDataIn.get(i);
                                if (displayData.getKey() == data1.getInt("key")) {
                                    display.displayDataIn.set(i, new Pair<>(data1.getInt("key"), data1.getJSONObject("value")));
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                display.displayDataIn.add(new Pair<>(data1.getInt("key"), data1.getJSONObject("value")));
                            }

                        } else if (data1.getInt("pairID") == 1) {
                            boolean found = false;
                            for (int i = 0; i < display.displayDataIn.size(); i++) {
                                Pair<Integer, JSONObject> displayData = display.displayDataIn.get(i);
                                if (displayData.getKey() == data1.getInt("key")) {
                                    display.displayDataIn.set(i, new TimedPair<>(data1.getInt("key"), data1.getJSONObject("value"), Instant.parse(data1.getString("time"))));
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                display.displayDataIn.add(new TimedPair<>(data1.getInt("key"), data1.getJSONObject("value"), Instant.parse(data1.getString("time"))));
                            }
                        }


                    } else {
                        //System.out.println(data.toString());
                        dataIn.add(data);

                        synchronized (dataIn) {
                            dataIn.notifyAll();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public class SendThread extends Thread{
        @Override
        public void run() {
            boolean exception = false;
            while(true){
                try{
                    if(dataOut.size() == 0) {
                        synchronized (dataOut) {
                            dataOut.wait();
                        }
                    }
                    out.println(dataOut.get(0));
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

    public class ChatThread extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    if (dataIn.size() == 0) {
                        synchronized (dataIn) {
                            dataIn.wait();
                        }
                    }
                    for (int i = 0; i < dataIn.size(); i++) {
                        if (dataIn.get(i).getInt("operation") == Operation.CHAT) {
                            chatText.append(dataIn.get(i).getString("data") + "\n");
                            dataIn.remove(i);
                            i--;
                            //System.out.println(dataIn.get(i).getString("data"));
                            chatText.repaint();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Display extends JPanel implements KeyListener, MouseListener, ActionListener {
        int Delay;
        Timer timer;
        Timer dataTimer;
        ArrayList<Pair<Integer, JSONObject>> displayDataIn = new ArrayList<>();
        Client client;


        public Display(int Delay, Client client){
            this.client = client;
            this.Delay = Delay;
            this.setFocusable(true);
            this.requestFocusInWindow();
            addKeyListener(this);
            addMouseListener(this);
            timer = new Timer(Delay, this);
            timer.start();
            dataTimer = new Timer(Delay, e -> {
                for(int i = 0; i < displayDataIn.size(); i++){
                    Pair<Integer, JSONObject> data = displayDataIn.get(i);
                    if(data instanceof TimedPair){
                        TimedPair<Integer, JSONObject> timedPair = (TimedPair<Integer, JSONObject>) data;
                        if(timedPair.checkTime()){
                            displayDataIn.remove(data);
                        }
                    }
                }
            });
            dataTimer.start();
        }

        public void doDraw(Graphics2D g2d){

            for(Pair<Integer, JSONObject> displayDataRaw : displayDataIn){
                try {
                    JSONObject displayData = displayDataRaw.getValue();
                    int x = displayData.getInt("x");
                    int y = displayData.getInt("y");
                    Color color = new ColorUIResource(displayData.getInt("color"));
                    Object data = displayData.get("data");
                    int type = displayData.getInt("type");
                    if (type == DisplayData.STRING) {
                        g2d.setColor(color);
                        g2d.drawString((String) data, x, y);
                    } else if (type == DisplayData.RECTANGLE) {
                        JSONObject rectangle = (JSONObject) data;
                        boolean fill = rectangle.getBoolean("fill");
                        g2d.setColor(color);
                        Rectangle rect = new Rectangle(rectangle.getInt("x"), rectangle.getInt("y"), rectangle.getInt("width"), rectangle.getInt("height"));
                        if (!fill) {
                            g2d.draw(rect);
                        } else {
                            g2d.fill(rect);
                        }
                    } else if (type == DisplayData.MAP) {
                        for (Tile tile : Map.fromJson((JSONObject) data)) {
                            tile.draw(g2d);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            doDraw((Graphics2D) g);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            client.keyTyped(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            client.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            client.keyReleased(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            client.mouseClicked(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            client.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            client.mouseReleased(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {

            client.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            client.mouseExited(e);
        }
    }
}
