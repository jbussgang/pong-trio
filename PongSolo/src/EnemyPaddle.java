import java.awt.*;

public class EnemyPaddle {

    double ypos, yVel;
    boolean upAccel, downAccel;
    int xpos;
    final double GRAVITY= .94;
    Rectangle rec;

    public EnemyPaddle(){

        upAccel= false; downAccel= false;
        ypos= 210; yVel= 2;
        xpos= 40;
        rec= new Rectangle(xpos, (int) ypos, 20, 80);
    }

    public void draw(Graphics g){
        g.setColor(Color.white);
        g.fillRect(xpos, (int) ypos, 20, 80);

    }

    public void move(Ball ball){

        if(ball.ypos<ypos+40){
            ypos -= 3.3;
        }
        else if(ball.ypos>ypos+40){
            ypos += 3.3;

        }

        if(ypos < 0){
            ypos= 0;
        }

        if(ypos > 420){
            ypos= 420;
        }

        rec= new Rectangle(xpos, (int) ypos, 20, 80);
    }
}