package com.group46.controllers;

public class Feed {
    private Inventory inventory;
    private Score score;

    public Feed(Inventory inventory, Score score) {
        this.inventory = inventory;
        this.score = score;
    }

    public String feeding(){
        String food = inventory.getFoodItems();
        if (food != null) {
            inventory.removeFromInventory(food);
            score.scoreIncrease(10);
        }
        return food;
    }
}
