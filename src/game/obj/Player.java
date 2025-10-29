package game.obj;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

public class Player extends HpRender{

    public Player(){
        super(new HP(50,50));
        this.image = new ImageIcon(getClass().getResource("/game/image/plane_1.png")).getImage();
        this.image_speed = new ImageIcon(getClass().getResource("/game/image/plane_speed_1.png")).getImage();
        Path2D p=new Path2D.Double();
        p.moveTo(-5, -25);
        p.lineTo(30, 0);
        p.lineTo(-5, 25);
        p.lineTo(-30, 0);
        playerShap = new Area(p);
    }
    
    public static final double PLAYER_SIZE = 100;
    private double x;
    private double y;
    private final float MAX_SPEED = 1f;
    private float speed = 0f;
    private float angle=0f;
    private final Area playerShap;
    private final Image image;
    private final Image image_speed;
    private boolean speedUp;
    private boolean alive=true;

    public void changeLocation(double x,double y){
        this.x = x;
        this.y = y;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    
    public void update(){
        x+=Math.cos(Math.toRadians(angle))*speed;
        y+=Math.sin(Math.toRadians(angle))*speed;
    }
    public void changeAngle(float angle){
        if(angle<0){
            angle=359;
        }else if(angle>359){
            angle=0;
        }
        this.angle = angle;
    }
    
    public static BufferedImage resizeSprite(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resizedImage;
    }
    
    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();

        AffineTransform tran = new AffineTransform();
        tran.translate(x - PLAYER_SIZE/2, y - PLAYER_SIZE/2);
        tran.rotate(Math.toRadians(angle+90),PLAYER_SIZE/2, PLAYER_SIZE/2);
        g2.drawImage(speedUp ? image_speed : image, tran, null);
        Shape shap=getShape();
        HpRenderPlayer(g2,shap);
        g2.setTransform(old);
}
    
    public float getAngle(){
        return angle;
    }
    
    public void speedUp(){
        speedUp = true;
        if(speed>MAX_SPEED){
            speed = MAX_SPEED;
        }else{
            speed+= 0.01f;
        }
    }
    
    public void speedDown(){
        speedUp = false;
        if(speed<=0){
            speed = 0;
        }else{
            speed -= 0.003f;
        }
    }
    
    public Area getShape(){
        AffineTransform afx=new AffineTransform();
        afx.translate(x,y);
        afx.rotate(Math.toRadians(angle),0,0);
        return new Area(afx.createTransformedShape(playerShap));
    }
    
    public boolean isAlive(){
        return alive;
    }
    
    public void setAlive(boolean alive){
        this.alive = alive;
    }
    
    public void reset(){
        alive = true;
        resetHP();
        angle = 0;
        speed = 0;
    }
    
    public HP getHp() {
        return hp;
    }
    
    public void wrapAround(int screenWidth, int screenHeight) {
        if (x < -Player.PLAYER_SIZE) {
            x = screenWidth;
        } else if (x > screenWidth) {
            x = -Player.PLAYER_SIZE;
        }

        if (y < -Player.PLAYER_SIZE) {
            y = screenHeight;
        } else if (y > screenHeight) {
            y = -Player.PLAYER_SIZE;
        }
}

}