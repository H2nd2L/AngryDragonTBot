package angryDragon.business.domain.status;

public class Status {
    private int energy;
    private int hunger;
    private int joy;

    public Status(){
        this.energy = 60;
        this.hunger = 60;
        this.joy = 60;
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
        this.energy = Math.max(Math.min(valueOfEnergy, 100), 0);
    }

    public void setHunger(int valueOfHunger){
        this.hunger = Math.max(Math.min(valueOfHunger, 100), 0);
    }

    public void setJoy(int valueOfJoy){
        this.joy = Math.max(Math.min(valueOfJoy, 100), 0);
    }
}
