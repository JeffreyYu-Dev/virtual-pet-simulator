package com.group46.controllers;

public class GiveGift {
    private Inventory inventory;
    private int happinessIncrease;
    private Score score;

    public GiveGift(Inventory inventory, Score score, int happinessIncrease) {
        this.inventory = inventory;
        this.happinessIncrease = happinessIncrease;
        this.score = score;
    }

    public String gifting(){
        String gifts = inventory.getGiftItems();
        if (gifts != null){
            inventory.removeFromInventory("A GIFT!!");
            score.scoreIncrease(10);
        }
        return gifts;
    }


}
