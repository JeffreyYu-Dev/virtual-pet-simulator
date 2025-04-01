package com.group46.controllers;

import java.util.Arrays;

public class Inventory {

  private String[] foodItems;
  private String[] giftItems;
  private int numberOfFood;


  //new vars
  private int commonFoodItems;
  private int rareFoodItems;
  private int legendaryFoodItems;
  private int commonGiftItems;
  private int rareGiftItems;
  private int legendaryGiftItems;
  private int numberOfGifts = 0;
  public static Inventory inventory;

  public Inventory() {
    this.commonFoodItems = 3;
    this.rareFoodItems = 3;
    this.legendaryFoodItems = 3;
    this.commonGiftItems = 3;
    this.rareGiftItems = 3;
    this.legendaryGiftItems = 3;
  }

  public static Inventory getInventory() {
    if (inventory == null) {
      inventory = new Inventory();
    }
    return inventory;
  }

  /**
   * Decrements the count of whichever food item is being used
   *
   * @param foodItem
   */
  public void useFood(String foodItem) {
    switch (foodItem) {
      case "common":
        this.commonFoodItems -= 1;
        break;
      case "rare":
        this.rareFoodItems -= 1;
        break;
      case "legendary":
        this.legendaryFoodItems -= 1;
        break;
    }
  }

  /**
   * Decrements the count of whichever gift item is being used
   *
   * @param giftItem
   */
  public void useGift(String giftItem) {
    switch (giftItem) {
      case "common":
        if (this.commonGiftItems > 0) this.commonGiftItems--;
        break;
      case "rare":
        if (this.rareGiftItems > 0) this.rareGiftItems--;
        break;
      case "legendary":
        if (this.legendaryGiftItems > 0) this.legendaryGiftItems--;
        break;
    }
  }

  /**
   * This method increments the counter of a given gift rarity
   *
   * @param giftItem
   */
  public void addGift(String giftItem) {
    switch (giftItem) {
      case "common":
        this.commonGiftItems++;
      case "rare":
        this.rareGiftItems++;
      case "legendary":
        this.legendaryGiftItems++;
    }
  }


  /**
   * sets a specific gift rarity count to a given value
   * @param giftItem
   * @param num
   */
  public void setGifts(String giftItem, int num) {
    switch (giftItem) {
      case "common":
        this.commonGiftItems = num;
      case "rare":
        this.rareGiftItems = num;
      case "legendary":
        this.legendaryGiftItems = num;
    }
  }

  /**
   * This method increments the counter of a given food rarity
   *
   * @param foodItem
   */
  public void addFood(String foodItem) {
    switch (foodItem) {
      case "common":
        this.commonFoodItems++;
      case "rare":
        this.rareFoodItems++;
      case "legendary":
        this.legendaryFoodItems++;
    }
  }

  /**
   * sets a specific food rarity count to a given value
   * @param foodItem
   * @param num
   */
  public void setFood(String foodItem, int num) {
    switch (foodItem) {
      case "common":
        this.commonFoodItems = num;
      case "rare":
        this.rareFoodItems = num;
      case "legendary":
        this.legendaryFoodItems = num;
    }
  }


  /**
   * This method returns the amount of a given food type
   *
   * @param foodItem
   * @return number of food items
   */
  public int getNumberOfFood(String foodItem) {
    switch (foodItem) {
      case "common":
        return this.commonFoodItems;
      case "rare":
        return this.rareFoodItems;
      case "legendary":
        return this.legendaryFoodItems;
    }
    return 0;
  }

  /**
   * This method returns the amount of a given gift type
   *
   * @param giftItem
   * @return number of gift items
   */
  public int getNumberOfGifts(String giftItem) {
    switch (giftItem) {
      case "common":
        return this.commonGiftItems;
      case "rare":
        return this.rareGiftItems;
      case "legendary":
        return this.legendaryGiftItems;
    }
    return 0;
  }


  public void addToInventory(String item) {
    if (item.contains("Food")) {
      if (numberOfFood <= foodItems.length) {
        foodItems[numberOfFood] = item;
        numberOfFood++;
      }
    } else {
      giftItems[numberOfGifts] = item;
      numberOfGifts++;
    }
  }


  public void removeFromInventory(String item) {
    for (int i = 0; i < foodItems.length; i++) {
      if (foodItems[i].equals(item)) {
        foodItems[i] = foodItems[numberOfFood - 1];
        foodItems[numberOfFood - 1] = null;
        numberOfFood--;
        return;
      }
    }

    for (int i = 0; i < giftItems.length; i++) {
      if (giftItems[i].equals(item)) {
        giftItems[i] = giftItems[numberOfGifts - 1];
        giftItems[numberOfGifts - 1] = null;
        numberOfGifts--;
        return;
      }
    }
  }

  
  public String getFoodItems() {
    System.out.println(Arrays.toString(foodItems));
    return Arrays.toString(foodItems);
  }

  public String getGiftItems() {
    System.out.println(Arrays.toString(giftItems));
    return Arrays.toString(giftItems);
  }
}
