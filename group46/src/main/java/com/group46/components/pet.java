package com.group46.components;

import com.group46.controllers.Inventory;

/**
 * This class will be used implement base
 *
 * @author Jagger Adams
 */
public class pet {
  private String animal;
  private String name;
  private int health;
  private int fullness;
  private int happiness;
  private int energy;
  private boolean dead = false;
  private boolean sleeping = false;
  private boolean angry = false;
  private boolean hungry = false;
  private boolean normal = true;
  private long playTime = 0;
  private long vetTime = 0;
  private Inventory inventory = Inventory.getInventory();
  private int score = 0;

  /**
   * This constructor method is for loading an existing pet so you can manually
   * enter all their info
   *
   * @param animal
   * @param name
   * @param health
   * @param fullness
   * @param happiness
   * @param energy
   */
  public pet(String animal, String name, int health, int fullness, int happiness, int energy) {
    this.animal = animal;
    this.name = name;
    this.health = health;
    this.fullness = fullness;
    this.happiness = happiness;
    this.energy = energy;
  }

  /**
   * This constructor is for making new pets. Starting out with all their vital stats full
   *
   * @param animal
   * @param name
   */
  public pet(String animal, String name) {
    this.animal = animal;
    this.name = name;
    this.health = 100;
    this.fullness = 100;
    this.happiness = 100;
    this.energy = 100;
  }


  /**
   * This method gets the pet name
   *
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * This method gets the pet health
   *
   * @return health
   */
  public int getHealth() {

    return this.health;
  }

  /**
   * This method gets the pet happiness
   *
   * @return happiness
   */
  public int getHappiness() {
    return this.happiness;
  }

  /**
   * This method gets the pet energy
   *
   * @return health
   */
  public int getEnergy() {
    return this.energy;
  }


  /**
   * This method gets the pet fullness
   *
   * @return fullness
   */
  public int getFullness() {
    return this.fullness;
  }

  /**
   * This method determines the pets state (e.g. hungry, dead, etc.) by checking their stats
   * and returns their state as a string
   *
   * @return pet state as a string
   */
  public String getState() {
    if (this.health <= 0 || this.dead) {
      this.resetStates();
      this.dead = true;
      return "dead";
    } else if (this.energy <= 0 || this.sleeping) {
      this.sleeping = true;
      this.normal = false;
      return "sleeping";
    } else {
      if (this.happiness <= 0) {
        this.angry = true;
      }
      if (this.fullness <= 0) {
        this.hungry = true;
      }
      if (this.angry || this.hungry) {
        this.normal = false;
        if (this.angry && this.hungry) return "hangry";
        else if (this.angry) return "angry";
        else return "hungry";
      } else return "normal";
    }
  }


  /**
   * This is a private helper method set all state variables to false
   */
  private void resetStates() {
    this.angry = false;
    this.hungry = false;
    this.normal = false;
    this.dead = false;
    this.sleeping = false;
  }

  /**
   * This method is called when a food item if given to the pet.
   * It increments their fullness and changes hungry status if necessary
   *
   * @param foodItem the rarity of food being given
   */
  public String feed(String foodItem) {
    if (inventory.getNumberOfFood(foodItem) > 0) {
      switch (foodItem) {
        case "common":
          this.fullness += 10;
          this.score += 10;
          break;
        case "rare":
          this.fullness += 20;
          this.score += 20;
          break;
        case "legendary":
          this.fullness += 30;
          this.score += 30;
          break;
      }
      if (this.fullness > 100) this.fullness = 100;
      this.hungry = false;
      inventory.useFood(foodItem);
    } else {
      return "No " + foodItem + " food items in your inventory";
    }
    return "You gave " + this.name + " a " + foodItem + " food item!";
  }

  /**
   * This method is called when a gift item if given to the pet.
   * It increments their fullness and changes angry status if necessary
   *
   * @param giftItem the rarity of the gift being given
   */
  public String gift(String giftItem) {
    if (inventory.getNumberOfGifts(giftItem) > 0) {
      switch (giftItem) {
        case "common":
          this.happiness += 10;
          this.score += 10;
          break;
        case "rare":
          this.happiness += 20;
          this.score += 20;
          break;
        case "legendary":
          this.happiness += 30;
          this.score += 30;
          break;
      }
      if (this.happiness > 100) this.happiness = 100;
      if (this.happiness >= 50) this.angry = false;

      inventory.useGift(giftItem);
    } else {
      return "No " + giftItem + " gifts in your inventory";
    }
    return "You gave " + this.name + " a " + giftItem + " gift item!";
  }

  /**
   * Exercise pet function decreases energy and fullness but increases happiness.
   * Updates angry status if necessary
   */
  public String exercise() {
    this.fullness -= 10;
    this.energy -= 10;
    this.happiness += 25;
    if (this.fullness < 0) this.fullness = 0;
    if (this.energy < 0) this.energy = 0;
    if (this.happiness > 100) this.happiness = 100;
    if (this.happiness >= 50) this.angry = false;
    return this.name + " got some exercise!";
  }

  /**
   * This set the pet's energy
   *
   * @param energy
   */
  public void setEnergy(int energy) {
    this.energy = energy;
  }

  /**
   * This function increases the pets happiness and implements a 5 minute cooldown between uses
   */
  public String play() {
    if (System.currentTimeMillis() > this.playTime) {
      this.happiness += 10;
      if (this.happiness > 100) this.happiness = 100;
      if (this.happiness >= 50) this.angry = false;
      // 5 minutes = 5 * 60 * 1000 milliseconds
      long cooldown = 5 * 60 * 1000;
      this.playTime = System.currentTimeMillis() + cooldown;
    } else {
      long remainingTime = this.playTime - System.currentTimeMillis();
      long remainingMinutes = remainingTime / 60000;
      long remainingSeconds = (remainingTime % 60000) / 1000;

      return "Play cooldown ends in " +
          remainingMinutes + " minutes and " +
          remainingSeconds + " seconds";
    }
    this.score += 25;
    return "You and " + this.name + " played for 30 min!";
  }

  /**
   * This function increases the pets health and implements a 10 minute cooldown between uses
   */
  public String vet() {
    if (System.currentTimeMillis() > this.vetTime) {
      this.health += 25;
      if (this.health > 100) this.health = 100;
      // 10 minutes = 10 * 60 * 1000 milliseconds
      long cooldown = 10 * 60 * 1000;
      this.vetTime = System.currentTimeMillis() + cooldown;
    } else {
      long remainingTime = this.vetTime - System.currentTimeMillis();
      long remainingMinutes = remainingTime / 60000;
      long remainingSeconds = (remainingTime % 60000) / 1000;

      return "Vet cooldown ends in " +
          remainingMinutes + " minutes and " +
          remainingSeconds + " seconds";
    }
    this.score += 25;
    return "You took " + this.name + " to the vet!";
  }

  /**
   * This function puts the pet to sleep
   */
  public String goToBed() {
    this.sleeping = true;
    return "You put " + this.name + " to bed!";
  }

  public void awake() {
    this.sleeping = false;
  }

  public boolean isSleeping() {
    return this.sleeping;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }


  public void decreaseStat(int multiplier, String value) {
    int baseValue = 1;

    if (value.equals("health")) {
      this.health -= baseValue * multiplier;
    }

    if (value.equals("fullness")) {
      this.fullness -= baseValue * multiplier;
    }

    if (value.equals("happiness")) {
      this.happiness -= baseValue * multiplier;
    }

    if (value.equals("energy")) {
      this.energy -= baseValue * multiplier;
    }
  }

  public void increaseHappiness(int increase) {
    if (this.happiness + increase > 100) {
      this.happiness = 100;
    } else {
      this.happiness = happiness + increase;
    }
  }

  public void increaseHealth(int increase) {
    if (this.health + increase > 100) {
      this.health = 100;
    } else {
      this.health = health + increase;
    }
  }

  public void healthDecrease(int multiplier) {
    decreaseStat(multiplier, "health");
  }

  public void fullnessDecrease(int multiplier) {
    decreaseStat(multiplier, "fullness");
  }

  public void happinessDecrease(int multiplier) {
    decreaseStat(multiplier, "happiness");
  }

  public void energyDecrease(int multiplier) {
    decreaseStat(multiplier, "energy");
  }

  public void setHealth(int value) {
    this.health = health - value;
  }
}
