package com.readinguni.rq016842.javaroguelike.Core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.readinguni.rq016842.javaroguelike.Dungeon.Dungeon;
import com.readinguni.rq016842.javaroguelike.Dungeon.Floor;
import com.readinguni.rq016842.javaroguelike.Mobs.Player;
import com.readinguni.rq016842.javaroguelike.R;

import java.util.ArrayList;

public class TheGame extends GameThread
{
    private Bitmap mWall, mGround, mDStairs;
    private Dungeon mDungeon;
    public boolean first;
    public Resources mRes;
    public Context cnxt;
    public Boolean drawDung, waitingTurn;
    public boolean floorsDone = false;
    public static int TILE_SIZE = 64;
    public ArrayList<Bitmap> actorSprites, dungeonTiles, buttonSprites;
    public ArrayList<BitmapButton> buttons;
    public int currentFloor;
    public int XOFFSET;
    public int turnCount;
    protected static int NO_OF_FLOORS = 5;
    public ArrayList<Bitmap> backgrounds = new ArrayList<>();


    //This is run before anything else, so we can prepare things here
    public TheGame(GameView gameView)
    {
        //House keeping
        super(gameView);
        //Prepare the image so we can draw it on the screen (using a canvas)
        cnxt = gameView.getContext();
        mRes = cnxt.getResources();
        // Load Bitmap Resources
        loadActorBitmaps();
        loadDungeonBitmaps();
        loadButtonBitmaps();
        setupBeginning();
    }

    /**
     * Initialises JButtons and stores them in the buttons ArrayList
     * @param dimensions
     */
    public void initButtons(Point dimensions) {
        buttons = new ArrayList<>();
        String[] buttonNames = {"Up", "Down", "Left", "Right"};
        for (int i = 0; i < 4; i++)
        {
            buttons.add(new BitmapButton(buttonNames[i], i));
            buttons.get(i).setSize(buttonSprites.get(i).getWidth(), buttonSprites.get(i).getHeight());
        }
        int dungeonBottom = dimensions.y - (TILE_SIZE * 4);
        int middleRowHeight = dungeonBottom + (int)(TILE_SIZE * 1.5f) - 20;
        //up
        buttons.get(0).x = 150;
        buttons.get(0).y = dungeonBottom - 20;
        //down
        buttons.get(1).x = 150;
        buttons.get(1).y = dimensions.y - 120;
        //left
        buttons.get(2).x = 0;
        buttons.get(2).y = middleRowHeight;
        //right
        buttons.get(3).x = 300;
        buttons.get(3).y = middleRowHeight;
    }

    /**
     * Loads Actor Bitmap resources in to array.
     */
    public void loadActorBitmaps()
    {
        // 0 - Player
        // 1 - Crocodile
        // 2 - Wolf
        // 3 - Dragon
        // 4 - Moustache
        // 5 - Skeleton
        actorSprites = new ArrayList<>();
        actorSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.player64));
        actorSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.croc64));
        actorSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.wolf64));
        actorSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.dragon64));
        actorSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.moustache64));
        actorSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.skeleton64));
    }

    /**
     * Loads Dungeon Tile Bitmap resources in to array.
     */
    public void loadDungeonBitmaps()
    {
        // 0 - Wall
        // 1 - Floor
        // 2 - Stairs
        dungeonTiles = new ArrayList<>();
        dungeonTiles.add(BitmapFactory.decodeResource(mRes, R.drawable.grey_floor64));
        dungeonTiles.add(BitmapFactory.decodeResource(mRes, R.drawable.blue_wall64));
        dungeonTiles.add(BitmapFactory.decodeResource(mRes, R.drawable.d_stairs64));
    }

    /**
     * Loads Button Bitmap resources in to array.
     */
    public void loadButtonBitmaps()
    {
        // 0 - Up
        // 1 - Down
        // 2 - Left
        // 3 - Right
        buttonSprites = new ArrayList<>();
        buttonSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.arrow_up));
        buttonSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.arrow_down));
        buttonSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.arrow_left));
        buttonSprites.add(BitmapFactory.decodeResource(mRes, R.drawable.arrow_right));
    }

    //This is run before a new game (also after an old game)
    @Override
    public void setupBeginning() {
        this.currentFloor = 0;
        this.turnCount = 0;
        first = true;
        WindowManager wm = (WindowManager) cnxt.getSystemService(Context.WINDOW_SERVICE);
        Point dimensions = new Point();
        wm.getDefaultDisplay().getSize(dimensions); //get screen dimensions
        Log.v("Width, Height: " ,String.valueOf(dimensions.x) + ", " + String.valueOf(dimensions.y));
        // mDungeon = new Dungeon(3,8); // used for testing
        mDungeon = new Dungeon(dimensions.x / TILE_SIZE, (dimensions.y / TILE_SIZE) - 4);
        XOFFSET = (int) (0.5f * (dimensions.x - ((dimensions.x / TILE_SIZE)*TILE_SIZE)));
        mDungeon.addFloor();
        mDungeon.logDungeon();
        mDungeon.logStats();
        //Map Generation Thread
        Thread mapGenThread = new Thread(){
            // Do map generation in a thread so the player can play while levels generate
            @Override
            public void run() {
                for(int floorNo = 0; floorNo < NO_OF_FLOORS-1; floorNo++) {
                    mDungeon.addFloor(floorNo+1);
                    Log.v("Thread mapGenThread", "Floor #" + String.valueOf(floorNo + 1) + " added");
                }
                floorsDone = true;
            }
        };
        mapGenThread.start();
        initButtons(dimensions);
    }

    /**
     * doDraw function, run every frame. Calls drawFloor and drawButton methods.
     * @param canvas Canvas to be drawn on
     */
    @Override
    protected void doDraw(Canvas canvas) {
        if(canvas == null) return;
        super.doDraw(canvas);
        if (mDungeon!=null) {
            drawFloor(mDungeon.getFloor(currentFloor), canvas);
            drawMobs(mDungeon.getFloor(currentFloor), canvas);
            drawButtons(canvas);
            //drawDung = false;
        }
        else Log.w("RLError", "Dungeon hasn't been initialised");
    }

    /**
     * Draws a Floor to a Canvas by iterating through tile by tile and drawing appropriate sprites
     * @param floor Floor Object to be drawn.
     * @param canvas Canvas to be drawn on
     */
    public void drawFloor(Floor floor, Canvas canvas) {
        if(first) {
            first = false;
            backgrounds.clear();
        }
        if(backgrounds.size() == currentFloor)
        {
            backgrounds.add(loadBackground(mDungeon.getFloor(currentFloor), canvas));
        }
        canvas.drawBitmap(backgrounds.get(currentFloor), 0f, 0f, null);
    }

    /**
     * Creates a bitmap from a given Floor
     * @param floor Floor to be rendered
     * @param canvas Canvas of dimensions ned to be drawn-
     * @return
     */
    public Bitmap loadBackground(Floor floor, Canvas canvas) {
        Bitmap bm = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas();
        tempCanvas.setBitmap(bm);
        int sNo;
        float drawX, drawY;
        for (int x = 0; x < floor.tiles.size(); x++) {
            for (int y = 0; y < floor.tiles.get(0).size(); y++) {
                drawX = (float) ((x * TILE_SIZE) + XOFFSET);
                drawY = ((float) y * TILE_SIZE);
                sNo = floor.tiles.get(x).get(y).getSpriteNo();
                switch (sNo) {
                    case 0: // ground
                        tempCanvas.drawBitmap(dungeonTiles.get(0), drawX, drawY, null);
                        break;
                    case 1:
                        tempCanvas.drawBitmap(dungeonTiles.get(1), drawX, drawY, null);
                        break;
                    case 2:
                        tempCanvas.drawBitmap(dungeonTiles.get(2), drawX, drawY, null);
                        break;
                }
            }
        }
        return bm;
    }

    /**
     * Shows the lose game toast and sets GameMode to lose
     */
    public void loseGame()
    {
        Toast toast = Toast.makeText(cnxt, "Game Over!", Toast.LENGTH_LONG);
        toast.show();
        mMode = STATE_LOSE;
    }

    /**
     * Shows the win game toast and sets GameMode to won
     */
    public void winGame() {
        Toast toast = Toast.makeText(cnxt, "You Win!! Score: " + String.valueOf(this.score),
                Toast.LENGTH_LONG);
        toast.show();
        mMode = STATE_WIN;
    }

    /**
     * Draws mobs on to the canvas from their XY coordinates + offsets (to do animation)
     * @param floor Floor to draw mobs from
     * @param canvas Canvas to draw to
     */
    public void drawMobs(Floor floor, Canvas canvas) {
        for (int x = 0; x < floor.tiles.size(); x++) {
            for (int y = 0; y < floor.tiles.get(0).size(); y++) {
                if (!floor.tiles.get(x).get(y).getEmpty() && floor.tiles.get(x).get(y).getActor() != null) {
                    float drawX = (float) ((x * TILE_SIZE) + XOFFSET + floor.tiles.get(x).get(y).getActor().getOffsets().x);
                    float drawY = (float) ((y * TILE_SIZE) + floor.tiles.get(x).get(y).getActor().getOffsets().y);
                    canvas.drawBitmap(actorSprites.get(floor.tiles.get(x).get(y).getActor().getSpriteNo()), drawX, drawY, null);
                    floor.tiles.get(x).get(y).getActor().iterateOffsets();
                }
            }
        }
    }

    /**
     * Draws BitmapButton objects to the screen
     * @param canvas Canvas to be drawn on
     */
    public void drawButtons(Canvas canvas)
    {
        if(buttons.size() > 0)
        {
            BitmapButton toDraw;
            for (int i = 0; i < buttons.size(); i++)
            {
                toDraw = buttons.get(i);
                canvas.drawBitmap(buttonSprites.get(toDraw.spriteNo), (float) toDraw.x, (float) toDraw.y, null);
            }
        }
        else Log.e("Draw Error", "No Buttons in buttons array");
    }

    /**
     * Iterates through the array of JButtons to check if the touch coordinates are in any of their bounding boxes
     * @param x X Touch Co-ord
     * @param y Y Touch Co-ord
     * @return -1 if no BitmapButton pressed/ID of BitmapButton touched
     */
    public int checkButton(float x, float y)
    {
        int ix = (int) x;
        int iy = (int) y;
        for (int i = 0; i < buttons.size(); i++)
        {
            if (buttons.get(i).checkPress(ix, iy)) return i;
        }
        return -1; //no button pressed
    }

    /**
     * Moves the player in direction + simulates world while also checking for level ups, going up stairs
     * and player death.
     * @param direction Int direction (0 Up, 1 Down, 2 Left, 3 Right)
     */
    public void takeTurn(int direction) {
        turnCount++;
        if(turnCount%12 == 0) {
            turnCount = 0;
            updateScore(-1);
        }
        int exp = mDungeon.getFloor(currentFloor).movePlayer(direction);
        updateScore((long)exp);
        if(exp > 0) {
            boolean levelUp = (mDungeon.getFloor(currentFloor).getPlayer()).addEXP(exp);
            if(levelUp) {
                // LEVEL UP
                int oldLevel = mDungeon.getFloor(currentFloor).getPlayer().getLevel() - 1;
                Toast levelToast = Toast.makeText(cnxt, "Level Up! Lvl " + String.valueOf(oldLevel+1)
                                + " -> Lvl " + String.valueOf(oldLevel + 2) + "!", Toast.LENGTH_LONG);
                levelToast.show();
                updateScore(1);
            }
        }
        if (mDungeon.getFloor(currentFloor).playerOnStairs()) {
            if (currentFloor < 4) {
                mDungeon.goUpFloor(currentFloor, mDungeon.getFloor(currentFloor).getPlayer());
                currentFloor++;
            }
            else {
                winGame();
            }
        }
        else {
            mDungeon.takeTurn(currentFloor);
            updateHP(mDungeon.getFloor(currentFloor).getPlayer().getHP(), mDungeon.getFloor(currentFloor).getPlayer().getMaxHP());
            //mDungeon.logDungeon();
            if (!mDungeon.getFloor(currentFloor).getPlayer().getAlive()) loseGame();
        }
    }

    /**
     * Run every time the screen is touched. Calls a check to the buttons.
     * @param x X-Coord of Touch
     * @param y Y-Coord of Touch
     */
    @Override
	protected void actionOnTouch(float x, float y)
    {
        int buttonPressed = checkButton(x, y);
        if (buttonPressed > -1) //if a button has been pressed
        {
            takeTurn(buttonPressed);
        }
    }

    //This is run just before the game "scenario" is printed on the screen
    @Override
    protected void updateGame(float secondsElapsed) {
        //Log.v("FPS: ", String.valueOf((1000/secondsElapsed) / 1000));
        updateHP(mDungeon.getFloor(currentFloor).getPlayer().getHP(),
                mDungeon.getFloor(currentFloor).getPlayer().getMaxHP());
        Player temp = mDungeon.getFloor(currentFloor).getPlayer();
        setEXP(temp.getEXP());
        setLVL(temp.getLevel() + 1);
        waitingTurn = true;
    }
}
