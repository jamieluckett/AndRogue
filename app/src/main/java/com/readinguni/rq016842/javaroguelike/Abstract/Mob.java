package com.readinguni.rq016842.javaroguelike.Abstract;

/**
 * Created by Jamie on 30/03/2017.
 */

public abstract class Mob extends Actor
{
    public Mob(String name, int spriteNo, int hp, int attack, int def, int vision)
    {
        super(name, spriteNo, hp, attack, def, vision);
    }
}
