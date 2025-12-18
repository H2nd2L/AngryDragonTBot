package angryDragon.business.domain.status;

public class Status {
    private int energy;
    private int hunger;
    private int joy;

    public Status(){
        this.energy = 50;
        this.hunger = 50;
        this.joy = 50;
    }

    public int getEnergy(){
        return energy;
    }

    public int getHunger(){
        return hunger;
    }

    public int getJoy(){
        return joy;
    }

    public void setEnergy(int valueOfEnergy){
        this.energy = Math.min(valueOfEnergy, 100);
    }

    public void setHunger(int valueOfHunger){
        this.hunger = Math.min(valueOfHunger, 100);
    }

    public void setJoy(int valueOfJoy){
        this.joy = Math.min(valueOfJoy, 100);
    }
}
