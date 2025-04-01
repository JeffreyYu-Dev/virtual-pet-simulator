package com.group46.components;

public class Dog extends pet{

    final int decrease = 3;

    public Dog(String name, int health, int fullness, int happiness, int energy) {
        super("dog", name, health, fullness, happiness, energy);
    }

    public Dog(String name){
        super("dog", name);
    }

}
