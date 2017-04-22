package com.readinguni.rq016842.androgue.Mobs;

import android.util.Log;

import com.readinguni.rq016842.androgue.Abstract.Mob;

import java.util.Random;

public class Player extends Mob
{
    public int getLevel() {
        return level;
    }

    public int getEXP(){
        return exp;
    }

    private int level = 0, exp = 0, maxHP;
    private int[] levels = {7, 20, 40, 70, 110, 170, 220, 300, 375, 450, 550, 600, 650, 700,
    800,900,1000,1200,1500,2000}; //surely more than we need???
    private Random randGen;

    //String name, int spriteNo, int hp, int attack, int def, int vision)
    public Player(String name) {
        super(name, 0, 10, 3, 2, 10);
        this.randGen = new Random();
        this.maxHP = this.hp;
    }

    /**
     * Adds EXP To the player and checks for a level up.
     * @param amount Amount of EXP earned
     * @return True if leveled up, False otherwise
     */
    public boolean addEXP(int amount) {
        this.exp+= amount;
        Log.v("EXP/LVL", String.valueOf(this.exp) + "/" + String.valueOf(this.level));
        if (this.exp > levels[this.level])
        {
            levelUp();
            return true;
        }
        return false;
    }

    /**
     * Levels the player up (increases their stats)
     */
    public void levelUp() {
        this.level++;
        this.maxHP += this.randGen.nextBoolean() ? 3:4; //random choice between +3 and +4
        this.hp = this.maxHP;
        this.attack+= this.randGen.nextBoolean() ? 2:1;
        this.def+= this.randGen.nextBoolean() ? 2:1;
    }

    public long getMaxHP() {
        return this.maxHP;
    }
}
