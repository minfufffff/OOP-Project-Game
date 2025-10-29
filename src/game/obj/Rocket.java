package game.obj;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

public class Rocket extends HpRender{
    
    public Rocket(){
        super(new HP(20,20));
        this.image = new ImageIcon(getClass().getResource("/game/image/rocket.png")).getImage();
        Path2D p=new Path2D.Double();
        p.moveTo(37, 15);
        p.lineTo(72, 35);
        p.lineTo(37, 50);
        p.lineTo(0,35);
        rocketShap=new Area(p);
    }
    
    public static final double ROCKET_SIZE = 70;
    private double x;
    private double y;
    private final float speed = 0.3f;
    private float angle=0;
    private final Image image;
    private final Area rocketShap;
    
        public void changeLocation(double x,double y){
        this.x = x;
        this.y = y;
    }
    
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
    
    public void draw(Graphics2D g2){
        AffineTransform oldTranform = g2.getTransform();
        g2.translate(x,y);
        AffineTransform tran=new AffineTransform();
        tran.rotate(Math.toRadians(angle+90),ROCKET_SIZE/2,ROCKET_SIZE/2);
        g2.drawImage(image, tran, null);
        Shape shap=getShape();
        HpRenderRocket(g2,shap,y);
        g2.setTransform(oldTranform);
        
        /*
        g2.setColor(new Color(0,255,0));
        g2.draw(shap);
        g2.draw(shap.getBounds2D());*/
    }
    
    
    
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public float getAngle(){
        return angle;
    }
    
    public Area getShape(){
        AffineTransform afx=new AffineTransform();
        afx.translate(x,y);
        afx.rotate(Math.toRadians(angle),ROCKET_SIZE/2,ROCKET_SIZE/2);
        return new Area(afx.createTransformedShape(rocketShap));
    }
    
    public boolean check(int width,int height){
        Rectangle size=getShape().getBounds();
        if (x<=-size.getWidth()||y<-size.getHeight()||x>width||y>height){
            return false;
        }else{
            return true;
        }
    }
}
