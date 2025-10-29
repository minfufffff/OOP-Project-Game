package game.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class HpRender {
    final HP hp;
    
    public HpRender(HP hp){
        this.hp = hp;
    }
    
    protected void HpRenderRocket(Graphics2D g2,Shape shape,double y){
        if(hp.getCurrentHp()!=hp.getMAX_HP()){
            double hpY=shape.getBounds().getY()-y-15;
            g2.setColor(new Color(70,70,70));
            g2.fill(new Rectangle2D.Double(0,hpY,Rocket.ROCKET_SIZE,2));
            g2.setColor(new Color(253,91,91));
            double hpSize=hp.getCurrentHp()/hp.getMAX_HP()*Rocket.ROCKET_SIZE;
            g2.fill(new Rectangle2D.Double(0,hpY,hpSize,2));
        }
    }
    
    /*protected void HpRenderPlayer(Graphics2D g2,Shape shape,double y){
        if(hp.getCurrentHp()!=hp.getMAX_HP()){
            double hpY=shape.getBounds().getY()-y-15;
            g2.setColor(new Color(70,70,70));
            g2.fill(new Rectangle2D.Double(0,hpY,Player.PLAYER_SIZE,2));
            g2.setColor(new Color(112,224,65));
            double hpSize=hp.getCurrentHp()/hp.getMAX_HP()*Player.PLAYER_SIZE;
            g2.fill(new Rectangle2D.Double(0,hpY,hpSize,2));
      }
    } */
    
    protected void HpRenderPlayer(Graphics2D g2, Shape shape) {
        if(hp.getCurrentHp()!=hp.getMAX_HP()){
            double hpWidth = hp.getCurrentHp() / hp.getMAX_HP() * Player.PLAYER_SIZE;
            double hpX = shape.getBounds().getCenterX() - Player.PLAYER_SIZE / 2;
            double hpY = shape.getBounds().getY()-15;

            // Background
            g2.setColor(new Color(70, 70, 70));
            g2.fill(new Rectangle2D.Double(hpX, hpY, Player.PLAYER_SIZE, 4));

            // HP
            g2.setColor(new Color(112,224,65));
            g2.fill(new Rectangle2D.Double(hpX, hpY, hpWidth, 4));
        }
    }
    
    public boolean updateHP(double cutHP){
        hp.setCurrentHp(hp.getCurrentHp()-cutHP);
        return hp.getCurrentHp() > 0;
    }
    
    public double getHP(){
        return hp.getCurrentHp();
    }
    
    public void resetHP(){
        hp.setCurrentHp(hp.getMAX_HP());
    }
}
