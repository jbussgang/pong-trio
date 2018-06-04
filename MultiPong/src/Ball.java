import java.awt.*;

public class Ball{

    double xVel, yVel, xpos, ypos;
    double speed;
    Rectangle rec;

    public Ball(){
        speed= 4;
        xVel= speed * getRandomDirection();
        yVel= speed * getRandomDirection();
        xpos= 350;
        ypos= 250;

        rec= new Rectangle((int)xpos, (int) ypos, 20, 20);
    }

    public void draw(Graphics g){
        g.setColor(Color.white);
        g.fillOval((int)xpos, (int)ypos, 20, 20);
    }

    public int getRandomDirection(){
        int rand = (int) (Math.random()*2);
        if(rand== 1)
            return 1;
        else
            return -1;
    }


    public void checkPaddleCollision(Paddle p1, Paddle p2){
        if(rec.intersects(p1.rec) || rec.intersects(p2.rec))
            xVel= -xVel;
    }

    public void move(){

        xpos+= xVel;
        ypos+= yVel;

        if(ypos<10)
            yVel= -yVel;

        if(ypos > 490)
            yVel = -yVel;

        rec= new Rectangle((int)xpos, (int) ypos, 20, 20);
    }
}