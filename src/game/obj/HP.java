package game.obj;

public class HP {

    public double getMAX_HP() {
        return MAX_HP;
    }

 
    public void setMAX_HP(double MAX_HP) {
        this.MAX_HP = MAX_HP;
    }


    public double getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(double CurrentHp) {
        this.currentHp = CurrentHp;
    }
    
    public HP(double MAX_HP,double currentHp){
        this.MAX_HP = MAX_HP;
        this.currentHp = currentHp;
    }
    
    public HP(){
        
    }
    
    public void addHP(){
        if(currentHp<=MAX_HP){
            if(currentHp+10>MAX_HP){
                currentHp = MAX_HP;
            }
            else
                currentHp += 10;
        }
    }
   
    
    private double MAX_HP;
    private double currentHp;
}
