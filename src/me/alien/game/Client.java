package me.alien.game;

import me.alien.game.util.Data;
import me.alien.game.util.Operation;
import me.alien.game.util.Pair;
import me.alien.game.util.TimedPair;
import me.alien.game.util.data.DisplayData;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;

import static me.alien.game.Game.VERSION;

public class Client extends JFrame implements KeyListener, MouseListener {
    Socket socket;
    ArrayList<Pair<Integer, Data>> dataOut;
    ArrayList<JSONObject> dataIn;
    Display display;
    javax.swing.JTextArea chatText;

    public Client(String ip, int port) {
        try {
            System.out.println("Communicate with server "+ip+" at "+port);
            socket = new Socket(ip, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            JSONObject data = new JSONObject(line);;
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
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
            display = new Display(100);
            ReciveThread reciveThread = new ReciveThread();
            SendThread sendThread = new SendThread();
            reciveThread.start();
            sendThread.start();

            chatText = new JTextArea();
            chatText.setBounds(new Rectangle(10,10,30,30));
            chatText.setEnabled(false);
            chatText.append("Chat started");
            //chatText


            add(display);
            //add(chatText);
            setName("Game");
            addKeyListener(this);
            addMouseListener(this);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendChat(String data){
        chatText.append(data);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public class ReciveThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String dataRaw = in.readLine();
                    System.out.println(dataRaw);
                    JSONObject data = new JSONObject(dataRaw);
                    if(data.getInt("operation") == Operation.DISPLAY_DATA){

                        JSONObject data1 = data.getJSONObject("data");
                        if(data1.getInt("pairID") == 0) {
                            display.displayDataIn.add(new Pair<>(data1.getInt("key"), data1.getJSONObject("value")));
                        }else if(data1.getInt("pairID") == 1){
                            display.displayDataIn.add(new TimedPair<>(data1.getInt("key"), data1.getJSONObject("value"), Instant.parse(data1.getString("time"))));
                        }


                    }else{
                        dataIn.add(data);

                        synchronized (dataIn){
                            dataIn.notifyAll();
                        }
                    }
                    in.close();
                }catch (Exception e){

                }
            }
        }
    }

    public class SendThread extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    synchronized (dataOut){
                        dataOut.wait();
                    }
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(dataOut.get(0).toString());
                    out.close();
                    dataOut.remove(0);

                }catch (Exception e){

                }
            }
        }
    }

    public class chatThread extends Thread{
        @Override
        public void run() {
            while (true){
                synchronized (dataIn){
                    try {
                        dataIn.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for(int i = 0; i < dataIn.size(); i++){
                    if(dataIn.get(i).getInt("operation") == Operation.CHAT){
                        J
                    }
                }
            }
        }
    }

    public class Display extends JPanel implements ActionListener {
        int Delay;
        Timer timer;
        Timer dataTimer;
        ArrayList<Pair<Integer, JSONObject>> displayDataIn = new ArrayList<>();


        public Display(int Delay){
            this.Delay = Delay;
            timer = new Timer(Delay, this);
            timer.start();
            dataTimer = new Timer(Delay, e -> {
                for(int i = 0; i < displayDataIn.size(); i++){
                    Pair<Integer, JSONObject> data = displayDataIn.get(i);
                    if(data instanceof TimedPair<Integer,JSONObject>){
                        TimedPair<Integer, JSONObject> timedPair = (TimedPair<Integer, JSONObject>) data;
                        if(timedPair.checkTime()){
                            displayDataIn.remove(data);
                        }
                    }
                }
            });
            dataTimer.start();
        }

        public void doDraw(Graphics2D g2d) throws InterruptedException {
            g2d.setColor(Color.CYAN);
            g2d.drawRect(0,0,this.getWidth(),this.getHeight());

            for(Pair<Integer, JSONObject> displayDataRaw : displayDataIn){
                JSONObject displayData = displayDataRaw.getValue();
                int x = displayData.getInt("x");
                int y = displayData.getInt("y");
                Color color = new ColorUIResource(displayData.getInt("color"));
                Object data = displayData.get("data");
                int type = displayData.getInt("type");
                if(type == DisplayData.STRING){
                    g2d.setColor(color);
                    g2d.drawString((String)data,x,y);
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
            try {
                doDraw((Graphics2D) g);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
