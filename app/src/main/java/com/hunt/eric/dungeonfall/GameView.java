package com.hunt.eric.dungeonfall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Hero hero;
    public Enemy enemy;
    private Bitmap bg;
    private Bitmap resizedBg;

    private int screenX;
    private int screenY;

    private int enemyCount = 12;

    volatile boolean playing;
    Thread gameThread = null;

    private boolean gameEnded;

    private Enemy[] enemies = new Enemy[enemyCount];

    // For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private Context context;

    // For saving and loading the high score
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    GameView(Context context, int x, int y) {
        super(context);
        this.context  = context;

        screenX = x;
        screenY = y;

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();

        for(int i=0; i<enemyCount ;i++) {
            enemy = new Enemy(context, 0, 0, 30, 47, 10, 3);
            enemies[i] = enemy;
            enemies[i].setX((int)(Math.random()*screenX));
            enemies[i].setY((int)(Math.random()*screenY));
        }

        bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        resizedBg = Bitmap.createScaledBitmap(bg, screenX, screenY+100, false);

        // Initialise our player ship
        //player = new PlayerShip(context, x, y);
        //enemy1 = new EnemyShip(context, x, y);
        //enemy2 = new EnemyShip(context, x, y);
        //enemy3 = new EnemyShip(context, x, y);

        //int numSpecs = 40;

        //for (int i = 0; i < numSpecs; i++) {
        // Where will the dust spawn?
        //SpaceDust spec = new SpaceDust(x, y);
        //dustList.add(spec);
        //}

        // Load fastest time
        prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);
        // Initialize the editor ready
        editor = prefs.edit();
        // Load fastest time
        // if not available our highscore = 1000000
        //fastestTime = prefs.getLong("fastestTime", 1000000);

        startGame();
    }

    private void startGame(){
        //Initialise game objects
        hero = new Hero(context, screenX, screenY);

        gameEnded = false;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        // Collision detection on new positions
        // Before move because we are testing last frames
        // position which has just been drawn
        boolean hitDetected = false;

        // Update the player
        hero.update();
        // Update the enemies
        enemy.update(System.currentTimeMillis());

        //Completed the floor
        /*if(distanceRemaining < 0){
            //check for new fastest time
            if(timeTaken < fastestTime) {
                // Save high score
                editor.putLong("fastestTime", timeTaken);
                editor.commit();
                fastestTime = timeTaken;
            }

            // avoid ugly negative numbers
            // in the HUD
            distanceRemaining = 0;

            // Now end the game
            gameEnded = true;
        }*/
    }

    private void draw() {

        if (ourHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();

            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Draw the player, enemies, and background
            canvas.drawBitmap(resizedBg, 0, 0, paint);
            canvas.drawBitmap(hero.getBitmap(), hero.getX(), hero.getY(), paint);
            enemy.draw(canvas);

            /*if(!gameEnded) {
                // Draw the hud
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                //canvas.drawText("Fastest:" + fastestTime + "s", 10, 20, paint);
                canvas.drawText("Fastest:" + formatTime(fastestTime) + "s", 10, 20, paint);
                //canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 20, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance:" + distanceRemaining / 1000 + " KM", screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield:" + player.getShieldStrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed:" + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 20, paint);
            }else{
                // Show pause screen
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX/2, 100, paint);
                paint.setTextSize(25);
                //canvas.drawText("Fastest:"+ fastestTime + "s", screenX/2, 160, paint);
                canvas.drawText("Fastest:"+ formatTime(fastestTime) + "s", screenX/2, 160, paint);
                //canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 200, paint);
                canvas.drawText("Time:" + formatTime(timeTaken) + "s", screenX / 2, 200, paint);
                canvas.drawText("Distance remaining:" + distanceRemaining/1000 + " KM",screenX/2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX/2, 350, paint);
            }*/
            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // There are many different events in MotionEvent
        // We care about just 2 - for now.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Has the player lifted there finger up?
            case MotionEvent.ACTION_UP:
                hero.stopAttack();
                break;

            // Has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                hero.setAttack();
                // If we are currently on the pause screen, start a new game
                if(gameEnded){
                    startGame();
                }
                break;
        }
        return true;
    }

    // Clean up our thread if the game is interrupted or the player quits
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    // Make a new thread and start it
    // Execution moves to our R
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private String formatTime(long time){
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;
        if (thousandths < 100){strThousandths = "0" + thousandths;}
        if (thousandths < 10){strThousandths = "0" + strThousandths;}
        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
    }
}