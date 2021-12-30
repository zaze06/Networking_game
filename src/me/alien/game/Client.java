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
    int width;
    int height;
    BufferedReader in;
    PrintWriter out;

    public Client(String ip, int port) {
        try {
            chatText = new JTextArea();
            System.out.println("Communicate with server "+ip+" at "+port);
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            JSONObject data = new JSONObject(line);;
            out = new PrintWriter(socket.getOutputStream(), true);
            dataIn = new ArrayList<>();
            dataOut = new ArrayList<>();
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
            display.setBounds(0,0,600,400);
            ReciveThread reciveThread = new ReciveThread();
            SendThread sendThread = new SendThread();
            reciveThread.start();
            sendThread.start();
            ChatThread chatThread = new ChatThread();
            chatThread.start();
            setLayout(new BorderLayout());

            chatText.setBounds(570,0,30,30);
            //chatText.setEnabled(false);
            //chatText.setM
            chatText.append("Chat started\n");
            //this.setComponentZOrder(chatText, 0);
            //chatText




            add(display, BorderLayout.CENTER);
            add(chatText, BorderLayout.EAST);
            setName("Game");
            addKeyListener(this);
            addMouseListener(this);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            setVisible(true);


            //this.setComponentZOrder(chatText, 20);
            width = getWidth();
            height = getHeight();

            out.println(new Data(Operation.SUCCESS, "\"Setup complete. Waiting for data\""));
            chatText.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendChat(String data){
        chatText.append(data);
        chatText.repaint();
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
        static int counter = 0;
        @Override
        public void run() {
            while(true){
                try{
                    String dataRaw = in.readLine();
                    System.out.println("Received: "+dataRaw);
                    DataRawHandler dataRawHandler = new DataRawHandler(dataRaw);
                    dataRawHandler.start();
                    //in.close();
                }catch (Exception e) {
                    if (counter < 10) {
                        System.out.println("ReceiveThread.run exception\n" + e.toString());
                        counter++;
                    }
                }
            }
        }
    }

    private class DataRawHandler extends Thread{
        String dataRaw;
        public DataRawHandler(String dataRaw){
            this.dataRaw = dataRaw;
            //System.out.println("DRH"+dataRaw);
        }
        @Override
        public void run() {
            //System.out.println(dataRaw);
            JSONObject data = new JSONObject(dataRaw);
            if(data.getInt("operation") == Operation.DISPLAY_DATA){

                JSONObject data1 = data.getJSONObject("data");
                if(data1.getInt("pairID") == 0) {
                    boolean found = false;
                    for(int i = 0; i < display.displayDataIn.size(); i++){
                        Pair<Integer, JSONObject> displayData = display.displayDataIn.get(i);
                        if(displayData.getKey() == data1.getInt("key")){
                            display.displayDataIn.set(i, new Pair<>(data1.getInt("key"), data1.getJSONObject("value")));
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        display.displayDataIn.add(new Pair<>(data1.getInt("key"), data1.getJSONObject("value")));
                    }

                }else if(data1.getInt("pairID") == 1){
                    boolean found = false;
                    for(int i = 0; i < display.displayDataIn.size(); i++){
                        Pair<Integer, JSONObject> displayData = display.displayDataIn.get(i);
                        if(displayData.getKey() == data1.getInt("key")){
                            display.displayDataIn.set(i, new TimedPair<>(data1.getInt("key"), data1.getJSONObject("value"), Instant.parse(data1.getString("time"))));
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        display.displayDataIn.add(new TimedPair<>(data1.getInt("key"), data1.getJSONObject("value"), Instant.parse(data1.getString("time"))));
                    }
                }


            }else{
                //System.out.println(data.toString());
                dataIn.add(data);

                synchronized (dataIn){
                    dataIn.notifyAll();
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
                    out.println(dataOut.get(0));
                    out.close();
                    dataOut.remove(0);

                }catch (Exception e){

                }
            }
        }
    }

    public class ChatThread extends Thread{
        @Override
        public void run() {
            while (true){
                synchronized (dataIn){
                    try {
                        dataIn.wait();
                        //System.out.println("Wait done");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for(int i = 0; i < dataIn.size(); i++){
                    if(dataIn.get(i).getInt("operation") == Operation.CHAT){
                        chatText.append(dataIn.get(i).getString("data")+"\n");
                        //System.out.println(dataIn.get(i).getString("data"));
                        chatText.repaint();
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
            //g2d.setColor(Color.CYAN);
            //g2d.drawRect(0,0, width, height);

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
                }else if(type == DisplayData.RECTANGLE){
                    JSONObject rectangle = (JSONObject) data;
                    boolean fill = rectangle.getBoolean("fill");
                    g2d.setColor(color);
                    Rectangle rect = new Rectangle(rectangle.getInt("x"), rectangle.getInt("y"), rectangle.getInt("width"), rectangle.getInt("height"));
                    if(!fill) {
                        g2d.draw(rect);
                    }else{
                        g2d.fill(rect);
                    }
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
