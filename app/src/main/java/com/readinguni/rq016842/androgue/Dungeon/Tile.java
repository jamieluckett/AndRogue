package com.readinguni.rq016842.androgue.Dungeon;

import com.readinguni.rq016842.androgue.Abstract.Actor;

public class Tile {
    protected boolean empty;
    private Actor actor;
    private int spriteNo;

    @Override
    public String toString() {
        return "Tile{" +
                "empty=" + empty +
                ", actor=" + actor +
                ", spriteNo=" + spriteNo +
                '}';
    }

    public Tile(int sNo, boolean empty) {
        this.spriteNo = sNo;
        this.empty = empty;
    }

    public int getSpriteNo()
    {
        return spriteNo;
    }

    public Actor getActor()
    {
        return this.actor;
    }

    public boolean getEmpty()
    {
        return empty;
    }

    /**
     * Marks empty as true and sets actor to null
     */
    public void clearActor(){
        this.actor = null;
        empty = true;
    }

    /**
     * Sets empty to false and this.actor to passed entity
     * @param entity Actor to store
     */
    public void setActor(Actor entity) {
        empty = false;
        this.actor = entity;
    }
}
