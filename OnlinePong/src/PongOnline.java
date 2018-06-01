import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class PongOnline implements Runnable, KeyListener, MouseListener {

    final int WIDTH = 700;
    final int HEIGHT = 500;

    public JFrame frame= new JFrame("PongOnline");
    public JPanel panel;
    public Canvas canvas;
    public BufferStrategy bufferStrategy;
    public boolean gameStarted = false, receivedUPD= false;
    public int p1Score = 0, p2Score = 0;

    private Paddle myPaddle, oppPaddle;
    private Ball b1;

    public Data data, oppData;

    public String ip;
    public InetAddress clientAddress= null;
    public DatagramSocket socket= null;
    public String myName;
    public int sleepTime = 3;
    public int port = 2345;
    private sendThread st;
    private getThread gt;

    public static void main(String[] args) throws SocketException, UnknownHostException {
        PongOnline ex = new PongOnline();
        new Thread(ex).start();

    }

    public PongOnline() throws SocketException, UnknownHostException {
        panel = (JPanel) frame.getContentPane();

        String nameInput = JOptionPane.showInputDialog("Enter Name:");
        myName=nameInput;

        if(!myName.equals("server")){
            String ipInput = JOptionPane.showInputDialog("Enter server IP here");
            ip=ipInput;
        }
        else{
            JOptionPane.showMessageDialog(panel, "Your IP address: "+InetAddress.getLocalHost().getHostAddress());
        }

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setLayout(null);
        canvas = new Canvas();
        canvas.setBounds(0, 0, WIDTH, HEIGHT);
        canvas.setIgnoreRepaint(true);

        panel.setLayout(new FlowLayout());
        panel.add(canvas);

        canvas.addKeyListener(this);
        canvas.addMouseListener(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();

        canvas.requestFocus();

        if (myName.equals("server")) {
            myPaddle = new Paddle(1);
            oppPaddle = new Paddle(2);
        } else {
            myPaddle = new Paddle(2);
            oppPaddle = new Paddle(1);
        }

        b1 = new Ball();

        threadSetup();

    }// BasicGameApp()

    public void moveEverything() {
        myPaddle.move();

        if (gameStarted) {
            b1.move();
            b1.checkPaddleCollision(myPaddle, oppPaddle);
        }
    }

    //thread
    public void run() {

        while (true) {
            // paint the graphics
            render();
            moveEverything();
            //sleep
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {

            }
        }
    }

    //paints things on the screen using bufferStrategy
    private void render() {
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if (p1Score == 10 || p2Score == 10) {
            g.setColor(Color.red);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 70));
            g.drawString("GAME OVER", 170, 270);

            if (p1Score == 10)
                g.drawString("Paddle 1 wins", 180, 350);
            if (p2Score == 10)
                g.drawString("Paddle 2 wins", 180, 350);
        } else {
            g.setColor(Color.white);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
            g.drawString("" + p2Score, 100, 70);
            g.drawString("" + p1Score, 600, 70);

            if (b1.xpos < -10) {
                gameStarted = false;
                p1Score++;
                b1.xpos = 350;
                b1.ypos = 250;
                myPaddle.ypos = 210;
            }
            if (b1.xpos > 710) {
                gameStarted = false;
                p2Score++;
                b1.xpos = 350;
                b1.ypos = 250;
                myPaddle.ypos = 210;
            } else {
                myPaddle.draw(g);
                oppPaddle.draw(g);
                b1.draw(g);
            }
        }

        g.dispose();

        bufferStrategy.show();
    }

    public void threadSetup() throws SocketException {

        socket= new DatagramSocket(port);

        st = new sendThread();
        gt = new getThread();

        //All networking stuff has to be put into try - catches
        try //catch(SocketException sx)
        {
            if(myName.equals("server")){
                gt.start();
                st.start();
            }
            else {
                st.start();
                gt.start();
            }
        }//try

        catch (Exception ex2)        // if the above fails close up things and then try again
        {
            System.out.println(ex2);
        }//SocketException sx


    } //runServer

    class sendThread extends Thread//threads sending to make it less laggy
    {
        public void run() {
            while (true) {
                if (myName.equals("server") && clientAddress!=null) {
                    try {
                        sendData(clientAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (!myName.equals("server")){
                    try {
                        sendData(InetAddress.getByName(ip));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }//sendThread

    class getThread extends Thread//threads sending to make it less laggy
    {
        public void run() {

            while (true) {
                try {
                    getData();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }//receiveThread

    public void getData() throws IOException, ClassNotFoundException {
        // Create a packet
        byte[] incomingData = new byte[1024];
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);

        // Receive a packet (blocking)
        socket.receive(incomingPacket);
        clientAddress = incomingPacket.getAddress();

        System.out.println("Packet received from "+ clientAddress);

        // Process info

        byte[] incomingPacketData = incomingPacket.getData();

        ByteArrayInputStream in = new ByteArrayInputStream(incomingPacketData);
        ObjectInputStream is = new ObjectInputStream(in);

        oppData = (Data) is.readObject();

        oppPaddle.xpos = oppData.Pxpos;
        oppPaddle.ypos = oppData.Pypos;

        if(myName.equals("server")) {
            b1.xpos = oppData.Bxpos;
            b1.ypos = oppData.Bypos;
        }

        receivedUPD=true;
    }

    public void sendData(InetAddress address) throws IOException {
        data = new Data(myPaddle.xpos, (int) myPaddle.ypos, (int) b1.xpos, (int) b1.ypos);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(data);
        byte[] dataBytes = outputStream.toByteArray();

        //send data
        DatagramPacket sendPacket = new DatagramPacket(dataBytes, dataBytes.length, address, port);
        socket.send(sendPacket);
    }

    // REQUIRED KEYBOARD METHODS
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            myPaddle.setUpAccel(true);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            myPaddle.setDownAccel(true);
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            gameStarted = true;
            b1 = new Ball();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            myPaddle.setUpAccel(false);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            myPaddle.setDownAccel(false);
        }
    }

    public void keyTyped(KeyEvent e) {
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
}