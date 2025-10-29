package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

public class Meteor extends HpRender {

    public Meteor() {
        super(new HP(40, 40));
        this.image = new ImageIcon(getClass().getResource("/game/image/meteor.png")).getImage();

        Path2D p = new Path2D.Double();
        p.moveTo(0, 0);
        p.lineTo(0, 70);
        p.lineTo(70, 70);
        p.lineTo(70, 0);
        meteorShap = new Area(p);
    }

    public static final double METEOR_SIZE = 70;
    private double x;
    private double y;
    private final float speed = 0.3f;

    private float moveAngle = 0;
    private float rotateAngle = 0;

    private final Image image;
    private final Area meteorShap;

    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void changeAngle(float angle) {
        if (angle < 0) angle = 359;
        else if (angle > 359) angle = 0;
        this.moveAngle = angle;
    }

    public void update() {
        x += Math.cos(Math.toRadians(moveAngle)) * speed;
        y += Math.sin(Math.toRadians(moveAngle)) * speed;

        rotateAngle += 0.5f;
        if (rotateAngle >= 360) rotateAngle -= 360;
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();

        g2.translate(x, y);

        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(rotateAngle), METEOR_SIZE / 2, METEOR_SIZE / 2);

        g2.drawImage(image, tran, null);

        Shape shape = getShape();
        HpRenderRocket(g2, shape, y);

        g2.setTransform(oldTransform);
    }

    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        return new Area(afx.createTransformedShape(meteorShap));
    }

    public boolean check(int width, int height) {
        Rectangle size = getShape().getBounds();
        return !(x <= -size.getWidth() || y < -size.getHeight() || x > width || y > height);
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public float getAngle(){
        return moveAngle;
    }
}
