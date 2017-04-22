package com.readinguni.rq016842.androgue.Abstract;

import android.graphics.Point;
import android.util.Log;

import static java.lang.Math.abs;

public abstract class  Actor
{
    private String name;
    public boolean alive;
    private int spriteNo;
    public int moveOffsetX = 0, moveOffsetY = 0;
    protected int hp, attack, def, vision, exp;

    public Actor(String name, int spriteNo, int hp, int attack, int def, int vision) {
        this.name = name;
        this.spriteNo = spriteNo;
        this.exp = hp;
        this.hp = hp;
        this.attack = attack;
        this.def = def;
        this.alive = true;
        this.vision = vision;
    }

    /**
     * Calculates damage done and returns whether the actor was killed or not
     * @param incomingAttack Attack value
     * @return Return bool if killed or not
     */
    public boolean takeDamage(int incomingAttack) {
        Log.v("HPBefore",String.valueOf(this.hp));
        int damageTaken = incomingAttack - this.def;
        this.hp -= ((damageTaken > 0) ? damageTaken : 1);
        Log.v("HPAfter",String.valueOf(this.hp));
        if (hp <= 0)
        {
            this.alive = false;
            this.hp = 0;
        }
        return !this.alive; //return if killed
    }

    /**
     * Checks for the Player within an actors range.
     * @param playerCoords The player's coordinates
     * @param actorCoords The actors coordinates
     * @return -1 if player
     */
    public int findPlayer(Point playerCoords, Point actorCoords) {
        if(actorCoords.x - this.vision <= playerCoords.x
                && playerCoords.x <= actorCoords.x + this.vision)
            if(actorCoords.y - this.vision <= playerCoords.y
                    && playerCoords.y <= actorCoords.y + this.vision) {
                //if player within range of actors vision
                int[] totals = new int[4];
                int[] coords = {0,1,2,3};
                totals[0] = abs(playerCoords.x - actorCoords.x) + //up
                        abs((playerCoords.y -(actorCoords.y - 1)));

                totals[1] = abs(playerCoords.x - actorCoords.x) + //down
                        abs((playerCoords.y -(actorCoords.y + 1)));

                totals[3] = abs(playerCoords.x - actorCoords.x-1) + //left
                        abs((playerCoords.y -(actorCoords.y)));

                totals[2] = abs(playerCoords.x - actorCoords.x+1) + //right
                        abs((playerCoords.y -(actorCoords.y)));

                //Log.v("totals pre sort", Arrays.toString(totals));
                //Log.v("coords  pre sort", Arrays.toString(coords));


                boolean inOrder = false;
                int temp;

                //everyone likes a good ol bubble sort (sorry)

                /*sort the array of proximity measurements while also keeping the index of the
                measurements. we sort 2 arrays here to do this*/
                while(!inOrder) {
                    inOrder = true;
                    for(int i = 0; i < coords.length-1; i++) {
                        if(totals[i] > totals[i+1]) {
                            temp = coords[i];
                            coords[i] = coords[i+1];
                            coords[i+1] = temp;
                            temp = totals[i];
                            totals[i] = totals[i+1];
                            totals[i+1] = temp;
                            inOrder = false;
                        }
                    }
                }
                //Log.v("totals", Arrays.toString(totals));
                //Log.v("coords", Arrays.toString(coords));
                //String[] directionsStr = {"Up", "Down", "Left", "Right"};
                //Log.v("Player Found, Going", directionsStr[coords[0]]);
                return coords[0];
            }
        return -1; //player not with range
    }

    public String getName()
    {
        return this.name;
    }

    public String getChar()
    {
        return String.valueOf(name.charAt(0));
    }

    public int getSpriteNo()
    {
        return this.spriteNo;
    }

    public boolean getAlive()
    {
        return this.alive;
    }

    public int getAttack() {
        return attack;
    }

    public long getHP() {
        return hp;
    }

    public int getEXP() {
        return exp;
    }

    public void setOffSets(int difX, int difY) {
        moveOffsetX = difX;
        moveOffsetY = difY;
    }

    /**
     * Iterates offsets +/-4 to do animation
     */
    public void iterateOffsets() {
        if(moveOffsetX < 0) moveOffsetX+=4;
        if(moveOffsetY < 0) moveOffsetY+=4;
        if(moveOffsetX > 0) moveOffsetX-=4;
        if(moveOffsetY > 0) moveOffsetY-=4;
    }

    public Point getOffsets() {
        return new Point(moveOffsetX, moveOffsetY);
    }
}
