package com.readinguni.rq016842.javaroguelike.Dungeon;

import android.util.Log;

import com.readinguni.rq016842.javaroguelike.Mobs.Player;

import java.util.ArrayList;

/**
 * Created by Jamie on 30/03/2017.
 */

public class Dungeon
{
    private int noFloors;
    private ArrayList<Floor> floors = new ArrayList<>();
    private int xSize, ySize;

    public Dungeon(int xSize, int ySize)
    {
        this.xSize = xSize;
        this.ySize = ySize;
    }

    public Dungeon()
    {
        this(10, 10);
    }

    public void addFloor(Player player, int currentFloor)
    {
        floors.add(new Floor(this.xSize, this.ySize, currentFloor, player));
    }

    public void addFloor(int currentFloor)
    {
        floors.add(new Floor(this.xSize, this.ySize, currentFloor));
    }

    public void addFloor()
    {
        floors.add(new Floor(this.xSize, this.ySize, 0));
    }

    /**
     * Calls upon Floor's logPrint() to draw the dungeon to the Logs
     */
    public void logDungeon()
    {
        for(int i = 0; i < floors.size(); i++)
        {
            floors.get(i).logPrint();
        }
    }

    /**
     * Calls takeTurn on the instructed Floor
     * @param floorNo Floor to be simulated
     */
    public void takeTurn(int floorNo)
    {
        floors.get(floorNo).takeTurn();
    }

    public Floor getFloor(int floorNo)
    {
        return floors.get(floorNo);
    }

    /**
     * Logs basic Dungeon information
     */
    public void logStats()
    {
        Log.v("== Dungeon Stats ==", "");
        Log.v("No of Floors: ", String.valueOf(floors.size()));
        Log.v("XSize, YSize: ", String.valueOf(xSize) + ", " + String.valueOf(ySize));
    }

    public void goUpFloor(int currentFloor, Player player) {
        floors.get(currentFloor+1).setPlayer(player);
    }
}
