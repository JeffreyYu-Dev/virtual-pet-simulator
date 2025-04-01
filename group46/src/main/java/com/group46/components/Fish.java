package com.group46.components;

public class Fish extends pet{

    final int decrease = 1;

    public Fish(String name, int health, int fullness, int happiness, int energy) {
        super("fish", name, health, fullness, happiness, energy);
    }

    public Fish(String name){
        super("fish", name);
    }

    public void healthDecrease() {
        decreaseStat(decrease, "health");
    }
    public void fullnessDecrease(){
        decreaseStat(decrease, "fullness");
    }
    public void happinessDecrease(){
        decreaseStat(decrease, "happiness");
    }
    public void energyDecrease(){
        decreaseStat(decrease, "energy");
    }

    public void cleanTank(int health, int happiness){
        increaseHealth(40);
        increaseHappiness(40);
    }
}
