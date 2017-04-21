package com.readinguni.rq016842.javaroguelike.Dungeon;

/**
 * Created by Jamie on 30/03/2017.
 */
import java.util.ArrayList;
import java.util.Random;

import android.graphics.Point;
import android.util.Log;

import com.readinguni.rq016842.javaroguelike.Abstract.Actor;
import com.readinguni.rq016842.javaroguelike.Mobs.Crocodile;
import com.readinguni.rq016842.javaroguelike.Mobs.Dragon;
import com.readinguni.rq016842.javaroguelike.Mobs.MoustachedGent;
import com.readinguni.rq016842.javaroguelike.Mobs.Player;
import com.readinguni.rq016842.javaroguelike.Mobs.SkeletonBoss;
import com.readinguni.rq016842.javaroguelike.Mobs.Wolf;

public class Floor
{
    public ArrayList<ArrayList<Tile>> tiles = new ArrayList<>(); //2D array
    Random randomGen = new Random();
    private float ITEM_PERC = 0.05f;
    public int xSize, ySize, playerX, playerY;
    int floorNo;

    public Floor(int xSize, int ySize, int currentFloor, Player player)
    {
        this.xSize = xSize;
        this.ySize = ySize;
        this.floorNo = currentFloor;
        generateFloor(xSize, ySize, player);
    }

    public Floor(int xSize, int ySize, int currentFloor)
    {
        this.xSize = xSize;
        this.ySize = ySize;
        this.floorNo = currentFloor;
        generateFloor(xSize, ySize);
    }

    /**
     * Debug function. Draws the Floor to the Logs using basic ASCII chars to represent them
     */
    public void logPrint()
    {
        String row;
        Log.v("Player X,Y - ", String.valueOf(playerX) + ", " + String.valueOf(playerY));
        for (int x = 0; x < this.ySize; x++)
        {
            row = "";
            for (int y = 0; y < this.xSize; y++) {
                if(this.tiles.get(y).get(x) instanceof Wall) row += "#";
                else if(this.tiles.get(y).get(x) instanceof Stairs) row += ">";
                else {
                    try
                    {
                        row += this.tiles.get(y).get(x).getEmpty() ? " " :
                                this.tiles.get(y).get(x).getActor().getChar();
                    }
                    catch(NullPointerException e)
                    {
                        Log.v("help", String.valueOf(x) + ", " + String.valueOf(y) + " - " +
                                this.tiles.get(y).get(x).toString());
                        Log.v("error", String.valueOf(e));
                    }

                }
            }
            Log.v("Dungeon", row);
        }
    }

    private void generateFloor(int xSize, int ySize){
        this.generateFloor(xSize, ySize, new Player("Player"));
    }

    /**
     * Randomly generates a world using the algorithm linked below.
     * Stores it in a 2D array that is used by GenerateFloor to create appropriate Tile objects.
     * http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
     * @param xSize Width of the Floor
     * @param ySize Height of the Floor
     * @return 2D Array of ints representing world
     */
    private int[][] genTempWorld(int xSize, int ySize)
    {
        int NO_OF_ITERATIONS = 2;
        //roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
        int[][] firstWorld = new int[xSize][ySize];
        int[][] secondWorld = new int[xSize][ySize];

        //Generate world with each tile having a 50:50 chance of being ground/wall
        for (int i = 0; i < firstWorld.length; i++) {
            for (int j = 0; j < firstWorld[0].length; j++) {
                firstWorld[i][j] = (randomGen.nextBoolean()) ? 1 : 0;
            }
        }

        //create a 3 tall ground gap across the floor
        for(int x = (xSize/2) - 2; x <= (xSize/2) + 1; x++){
            for(int y = 1; y < firstWorld[0].length - 1; y++) {
                firstWorld[x][y] = 0;
            }
        }

        //"a tile becomes a wall if it was a wall and 4 or more of its eight neighbors were walls
        //or if it was not a wall and 5 or more neighbors were"
        int wallCount = 0, limit;
        for (int i = 0; i < NO_OF_ITERATIONS; i++)
        {
            for(int x = 0; x < firstWorld.length; x++) {
                for(int y = 0; y < firstWorld[0].length; y++) {
                    if(x == 0||y == 0||x == xSize - 1||y == ySize - 1) secondWorld[x][y] = 1;
                    else{
                        if(firstWorld[x][y] == 0) limit = 5;
                        else limit = 4;
                        for(int ix = -1; ix < 2; ix++){
                            for(int iy = -1; iy < 2; iy++){
                                if (firstWorld[ix + x][iy + y] == 1) wallCount++;
                            }
                        }
                        secondWorld[x][y] = (wallCount >= limit) ? 1: 0;
                        wallCount = 0;
                    }
                }
            }
            firstWorld = secondWorld;
        }
        return secondWorld;
    }

    /**
     * Generates a Floor and adds Player, mobs and stairs to it.
     * @param xSize Width of the Floor
     * @param ySize Height of the Floor
     * @param player Player object to store on floor
     */
    private void generateFloor(int xSize, int ySize, Player player)
    {
        int noFreeSpaces = 0;
        int[][] tempWorld = genTempWorld(xSize, ySize);

        for (int x = 0; x < tempWorld.length; x++) {
            tiles.add(new ArrayList<Tile>());
            for (int y = 0; y < tempWorld[0].length; y++) {
                if(tempWorld[x][y] == 0) {
                    tiles.get(x).add(y, new Ground());
                    noFreeSpaces++;
                }
                else
                {
                    tiles.get(x).add(y, new Wall());
                }
            }
        }

        //Add Mobs
        float MOB_PERC = 0.08f;
        int noOfMobs = (int) (((float) xSize * ySize) * MOB_PERC);

        int randX, randY;
        for (int j = 0; j < noOfMobs; j++) {
            randX = randomGen.nextInt(xSize);
            randY = randomGen.nextInt(ySize);
            if(tempWorld[randX][randY] == 0 && tiles.get(randX).get(randY).getEmpty()) {
                switch(this.floorNo) {
                    //add mob for each floor
                    case 0:
                        tiles.get(randX).get(randY).setActor(new Crocodile());
                        break;
                    case 1:
                        tiles.get(randX).get(randY).setActor(new Wolf());
                        break;
                    case 2:
                        tiles.get(randX).get(randY).setActor(new Dragon());
                        break;
                    case 3:
                        tiles.get(randX).get(randY).setActor(new MoustachedGent());
                        break;
                    case 4:
                        tiles.get(randX).get(randY).setActor(new SkeletonBoss());
                        break;
                }
            }
        }
        //add player
        boolean playerAdded = false;
        while(!playerAdded) {
            randX = randomGen.nextInt(xSize);
            randY = randomGen.nextInt(ySize);
            if(tempWorld[randX][randY] == 0 && tiles.get(randX).get(randY).getEmpty()) {
                tiles.get(randX).get(randY).setActor(player);
                playerAdded = true;
                playerX = randX;
                playerY = randY;
            }
        }
        //add staircase (we do this after adding the player so the player doesnt spawn on the stairs!)
        boolean stairsAdded = false;
        while(!stairsAdded) {
            randX = randomGen.nextInt(xSize);
            randY = randomGen.nextInt(ySize);
            if(tempWorld[randX][randY] == 0 && tiles.get(randX).get(randY).getEmpty()) {
                tiles.get(randX).set(randY,new Stairs());
                stairsAdded = true;
            }
        }
    }

    /**
     * Moves designated mob in direction. 0 Up, 1 Down, 2 Left, 3 Right
     * @param origins Coordinates of the mob
     * @param direction Direction in which to move
     * @return True if moved, False if attacked or walked in to a wall
     */
    public boolean moveMob(Point origins, int direction)
    {
        // 0 Up, 1 Down, 2 Left, 3 Right
        // Y--, Y++, X--, X++
        Point newLocation = new Point(origins.x, origins.y);
        boolean moved = false;
        int difY = 0, difX = 0;
        switch(direction) {
            case 0:
                newLocation.y--;
                difY = 64;
                break;
            case 1:
                newLocation.y++;
                difY = -64;
                break;
            case 2:
                newLocation.x--;
                difX = 64;
                break;
            case 3:
                newLocation.x++;
                difX = -64;
                break;
        }

        if((newLocation.x >= 0 && newLocation.x < this.xSize) &&
                (newLocation.y >= 0 && newLocation.y < this.ySize)) {
/*            Log.v("New Location", "Class: " + tiles.get(newLocation.x).get(newLocation.y).getClass() + " - " +
                    String.valueOf(tiles.get(newLocation.x).get(newLocation.y).getEmpty()));*/
            if(tiles.get(newLocation.x).get(newLocation.y).getEmpty()) {
/*                Log.v("Old X,Y / New X,Y: ", String.valueOf(origins.x) + ", " +
                        String.valueOf(origins.y) + " / " + String.valueOf(newLocation.x) +
                        ", " + String.valueOf(newLocation.y));*/
                Actor mobToMove = tiles.get(origins.x).get(origins.y).getActor();
                mobToMove.setOffSets(difX, difY);
                tiles.get(newLocation.x).get(newLocation.y).setActor(mobToMove);
                tiles.get(origins.x).get(origins.y).clearActor();
                moved = true;
            }
            else {
                if ((tiles.get(newLocation.x).get(newLocation.y) instanceof Ground)) {
                    int attack = tiles.get(origins.x).get(origins.y).getActor().getAttack();
                    Log.v("attack", "here");
                    Log.v("New Location", "Class: " + tiles.get(newLocation.x).get(newLocation.y).getClass() + " - " +
                            String.valueOf(tiles.get(newLocation.x).get(newLocation.y).getEmpty()));
                    tiles.get(newLocation.x).get(newLocation.y).getActor().takeDamage(attack);
                }
            }
        }
        return moved;
    }

    /**
     * Move the player in a direction. Will attack any enemy in the way
     * @param direction Direction to move. 0 Up, 1 Down, 2 Left, 3 Right
     * @return 0 unless an enemy is killed in which case the EXP earnt
     */
    public int movePlayer(int direction)
    {
        // 0 Up, 1 Down, 2 Left, 3 Right
        // Y--, Y++, X--, X++
        Point origins = new Point(playerX, playerY);
        Point newLocation = new Point(origins.x, origins.y);
        int difX = 0, difY = 0;
        switch(direction) {
            case 0:
                newLocation.y--;
                difY = 64;
                break;
            case 1:
                newLocation.y++;
                difY = -64;
                break;
            case 2:
                newLocation.x--;
                difX = 64;
                break;
            case 3:
                newLocation.x++;
                difX = -64;
                break;
        }

        if((newLocation.x >= 0 && newLocation.x < this.xSize) &&
                (newLocation.y >= 0 && newLocation.y < this.ySize)) {
/*            Log.v("New Location", "Class: " + tiles.get(newLocation.x).get(newLocation.y).getClass() + " - " +
                    String.valueOf(tiles.get(newLocation.x).get(newLocation.y).getEmpty()));*/
            if(tiles.get(newLocation.x).get(newLocation.y).getEmpty()) {
/*                Log.v("Old X,Y / New X,Y: ", String.valueOf(origins.x) + ", " +
                        String.valueOf(origins.y) + " / " + String.valueOf(newLocation.x) +
                        ", " + String.valueOf(newLocation.y));*/
                Actor mobToMove = tiles.get(origins.x).get(origins.y).getActor();
                mobToMove.setOffSets(difX, difY);
                tiles.get(newLocation.x).get(newLocation.y).setActor(mobToMove);
                tiles.get(origins.x).get(origins.y).clearActor();
                playerX = newLocation.x;
                playerY = newLocation.y;
            }
            else {
                //ATTACK
                if((tiles.get(newLocation.x).get(newLocation.y) instanceof Ground)) {
                    int attack = tiles.get(origins.x).get(origins.y).getActor().getAttack();
                    Log.v("attack", "here");
                    Log.v("New Location", "Class: " + tiles.get(newLocation.x).get(newLocation.y).getClass() + " - " +
                            String.valueOf(tiles.get(newLocation.x).get(newLocation.y).getEmpty()));
                    boolean killed = tiles.get(newLocation.x).get(newLocation.y).getActor().takeDamage(attack);
                    Log.v("Killed", String.valueOf(tiles.get(newLocation.x).get(newLocation.y).getActor().getEXP()));
                    if(killed) return tiles.get(newLocation.x).get(newLocation.y).getActor().getEXP();
                    else return 0;
                }
            }
        }
        return 0;
    }

    /**
     * Returns whether the player is currently standing on stairs
     * @return Boolean true/false
     */
    public boolean playerOnStairs(){
        return this.tiles.get(playerX).get(playerY) instanceof Stairs;
    }

    public Player getPlayer()
    {
        return (Player)tiles.get(playerX).get(playerY).getActor();
    }

    /**
     * Simulates a single turn in the world.
     */
    public void takeTurn() {
        int direction;
        ArrayList<Point> toMove = new ArrayList<>();
        ArrayList<Integer> toMoveDir = new ArrayList<>();
        for (int x = 0; x < tiles.size(); x++) {
            for (int y = 0; y < tiles.get(0).size(); y++) {
                if (tiles.get(x).get(y) instanceof Ground || tiles.get(x).get(y) instanceof Stairs) {
                    if (!(tiles.get(x).get(y).getEmpty())) {
                        if (!(tiles.get(x).get(y).getActor() instanceof Player)) {
                            if(!tiles.get(x).get(y).getActor().getAlive()) {
                                tiles.get(x).get(y).clearActor();
                            }
                            else {
                                int playerFind = tiles.get(x).get(y).getActor().findPlayer(
                                        new Point(playerX, playerY), new Point(x,y));
                                direction = (playerFind != -1) ? playerFind : randomGen.nextInt(3);
                                toMove.add(new Point(x, y));
                                toMoveDir.add(direction);
                            }
                        }
                    }
                }
            }
        }
        //move all mobs
        for (int i = 0; i < toMove.size(); i++) {
            moveMob(toMove.get(i), toMoveDir.get(i));
        }
    }

    public void setPlayer(Player player) {
        tiles.get(playerX).get(playerY).setActor(player);

    }
}
