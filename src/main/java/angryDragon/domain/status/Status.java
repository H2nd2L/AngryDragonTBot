package angryDragon.domain.status;

public class Status {
    private int energy;
    private int hunger;
    private int joy;

    public Status(){
        this.energy = 100;
        this.hunger = 100;
        this.joy = 100;
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
        this.energy = valueOfEnergy;
    }

    public void setHunger(int valueOfHunger){
        this.hunger = valueOfHunger;
    }

    public void setJoy(int valueOfJoy){
        this.joy = valueOfJoy;
    }
}
