import java.awt.*;

public class Paddle {

    double ypos, yVel;
    boolean upAccel, downAccel;
    int xpos;
    final double GRAVITY= .94;
    Rectangle rec;

    public Paddle(int player){

        upAccel= false; downAccel= false;
        ypos= 210; yVel= 0;

        if(player == 2){
            xpos= 20;
        }
        else{
            xpos= 660;
        }

        rec= new Rectangle(xpos, (int) ypos, 20, 80);
    }

    public void draw(Graphics g){
        g.setColor(Color.white);
        g.fillRect(xpos, (int) ypos, 20, 80);

    }

    public void move(){

        if(upAccel){
            yVel -= 2;
        }
        else if(downAccel){
            yVel += 2;
        }
        else if(!upAccel && !downAccel){
            yVel *= GRAVITY;
        }

        if(yVel >= 5){
            yVel= 5;
        }
        else if(yVel <= -5){
            yVel = -5;
        }

        ypos += yVel;

        if(ypos < 0){
            ypos= 0;
        }

        if(ypos > 420){
            ypos= 420;
        }

        rec= new Rectangle(xpos, (int) ypos, 20, 80);
    }

    public void setUpAccel(boolean input){
        upAccel= input;

    }

    public void setDownAccel(boolean input){
        downAccel= input;

    }

}