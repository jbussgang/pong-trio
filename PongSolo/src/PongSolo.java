import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class PongSolo implements Runnable, KeyListener{

    final int WIDTH = 700;
    final int HEIGHT = 500;

    public JFrame frame= new JFrame("PongOnline");
    public JPanel panel;
    public Canvas canvas;
    public BufferStrategy bufferStrategy;
    public boolean gameStarted = false;
    public int p1Score = 0, p2Score = 0;

    private Paddle myPaddle;
    private EnemyPaddle oppPaddle;
    private Ball b1;

    public static void main(String[] args){
        PongSolo ex = new PongSolo();
        new Thread(ex).start();

    }

    public PongSolo() {
        panel = (JPanel) frame.getContentPane();

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setLayout(null);
        canvas = new Canvas();
        canvas.setBounds(0, 0, WIDTH, HEIGHT);
        canvas.setIgnoreRepaint(true);

        panel.setLayout(new FlowLayout());
        panel.add(canvas);

        canvas.addKeyListener(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();

        canvas.requestFocus();

        myPaddle= new Paddle();
        oppPaddle= new EnemyPaddle();

        b1 = new Ball();
    }// BasicGameApp()

    public void moveEverything() {
        myPaddle.move();
        oppPaddle.move(b1);

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
    private void render(){
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
}