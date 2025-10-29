package game.obj;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

public class Medkit extends HP {

    private final Image image;
    public static final double MED_SIZE = 32;
    private double x;
    private double y;
    private final Area medShap;
    private final long spawnTime;


    public Medkit(double x, double y) {
        this.x = x;
        this.y = y;
        this.image = new ImageIcon(getClass().getResource("/game/image/health-red 32px.png")).getImage();

        Path2D p = new Path2D.Double();
        p.moveTo(0, 0);
        p.lineTo(MED_SIZE, 0);
        p.lineTo(MED_SIZE, MED_SIZE);
        p.lineTo(0, MED_SIZE);
        p.closePath();

        this.medShap = new Area(p);
        this.spawnTime = System.currentTimeMillis();
        this.x = x;
        this.y = y;        
    }
    
    public long getSpawnTime() {
        return spawnTime;
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTranform = g2.getTransform();
        g2.translate(x, y);
        g2.drawImage(image, 0, 0, null);
        g2.setTransform(oldTranform);

    }
    
    

    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        return new Area(afx.createTransformedShape(medShap));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public void changeLocation(double x,double y){
        this.x = x;
        this.y = y;
    }
}
