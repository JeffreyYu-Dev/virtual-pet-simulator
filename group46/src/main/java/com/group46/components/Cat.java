package com.group46.components;

public class Cat extends pet{

    final int decrease = 2;

    public Cat(String name, int health, int fullness, int happiness, int energy) {
        super("cat", name, health, fullness, happiness, energy);
    }

    public Cat(String name){
        super("cat", name);
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

    public void cleanLitterBox(){
        increaseHealth(10);
        increaseHappiness(20);
    }
}
