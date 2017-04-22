package com.readinguni.rq016842.androgue.Core;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.readinguni.rq016842.androgue.R;

public class MainActivity extends Activity {

    private static final int MENU_RESUME = 1;
    private static final int MENU_START = 2;
    private static final int MENU_STOP = 3;

    private GameThread mGameThread;
    private GameView mGameView;
    private Bundle sIS;

    /**
     * Called when app is first run
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Creation", "Activity Initiated (MainActivity.onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.sIS = savedInstanceState;
        mGameView = (GameView)findViewById(R.id.gamearea);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.startMenu();

        //this.startGame(mGameView, null, savedInstanceState);
    }

    /**
     * Loads up the Menu view
     */
    private void startMenu() {setContentView(R.layout.menu);}

    /**
     * Uses initialised threads + views and starts the game
     */
    private void startGame(GameView gView, GameThread gThread, Bundle savedInstanceState) {
        //Set up a new game, we don't care about previous states
        mGameThread = new TheGame(mGameView);
        mGameView.setThread(mGameThread);
        mGameThread.setState(GameThread.STATE_READY);
    }

	/*
	 * Activity state functions
	 */

    /**
     * Pauses the game
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        try{if(mGameThread.getMode() == GameThread.STATE_RUNNING)
        {
            mGameThread.setState(GameThread.STATE_PAUSE);
        }}catch(NullPointerException e){}
    }

    /**
     * Run when program is closing
     */
    @Override
    protected void onDestroy()
    {
        Log.v("Function Run", "MainActivity.onDestroy()");
        super.onDestroy();
        mGameView.cleanup();
        mGameThread = null;
        mGameView = null;
    }
    
    /*
     * UI Functions
     */

    /**
     * Run when the "Start Game" button is pressed. Sets up variables and starts the game
     * @param view
     */
    public void onClickStartGame(View view) {
        super.onCreate(this.sIS);
        setContentView(R.layout.activity_game);
        mGameView = (GameView)findViewById(R.id.gamearea);
        mGameView.setStatusView((TextView)findViewById(R.id.text));
        mGameView.setScoreView((TextView)findViewById(R.id.score));
        mGameView.setHPView((TextView)findViewById(R.id.hp));
        mGameView.setLVLView((TextView)findViewById(R.id.lvl));
        mGameView.setEXPView((TextView)findViewById(R.id.exp));
        this.startGame(mGameView, null, this.sIS);
    }

    /**
     * Run when the "Quit Game" button is pressed. Closes the game with System.exit(1)
     * @param view
     */
    public void onClickQuitGame(View view) {
        System.exit(1);
    }
}
