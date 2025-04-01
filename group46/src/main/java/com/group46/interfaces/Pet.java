package com.group46.interfaces;

public interface Pet {
    /**
     *
     * @param name
     * @return
     */
    public String getName(String name);


    /**
     *
     * @param health
     * @return
     */
    public int getHealth(int health);

    /**
     *
     * @param hungerBar
     * @return
     */
    public int getHungerBar(int hungerBar);

    /**
     *
     * @param mood
     * @return
     */
    public int getMood(int mood);

    /**
     *
     * @param score
     * @return
     */
    public int getScore(int score);
    public String setMood();
    public String setSprite();
}
