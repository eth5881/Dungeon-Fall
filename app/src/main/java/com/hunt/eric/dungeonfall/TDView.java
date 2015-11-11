package com.hunt.eric.dungeonfall;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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

/**
 * Created by Shawn on 11/10/2015.
 */
    public class TDView extends SurfaceView implements Runnable {
        volatile boolean playing;
        Thread gameThread = null;
        private Paint paint;
        private Canvas canvas;
        private SurfaceHolder ourHolder;

        private int screenX;
        private int screenY;
        private Context context;
        private boolean gameEnded;
        private Bitmap mBackground;
        private Bitmap hearts;
        private Bitmap coins;
        private Bitmap enemy ;
        private Array enemies;
        private Bitmap player;
        private Bitmap powerups;
        private Bitmap powerups2;
        private int numbCoins;
        private long exp;
        private int floor;
        private long level;


        public TDView(Context context, int x, int y) {
            super(context);
            this.context = context;

            screenX = x;
            screenY = y;
            // Initialize our drawing objects
            ourHolder = getHolder();
            paint = new Paint();
            //call start game
            startGame();

        }
        private void startGame(){

            numbCoins = 100;
            level = 22;
            exp = 22;
            floor = 22;

            //game has not ended
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
        private void update(){
        }
        private void draw(){
            if (ourHolder.getSurface().isValid()) {
                //First we lock the area of memory we will be drawing to
                canvas = ourHolder.lockCanvas();
                // set background to image and scale according to size of device
                mBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.brown_bg);
                canvas.drawBitmap(Bitmap.createScaledBitmap(mBackground,canvas.getWidth(),canvas.getHeight(),true),0,0,null);
                paint.setColor(Color.argb(255, 255, 255, 255));
                if(!gameEnded) {
                    // Draw the hud
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(35);
                    canvas.drawText("Level: " + level, (screenX / 3) + 190, 115, paint);
                    canvas.drawText("Floor: " + floor, 10, 115, paint);
                    canvas.drawText("Exp: " + exp, screenX - 130, 115, paint);
                    canvas.drawText("MP:", (screenX / 3) + 130, 55, paint);

                    hearts = BitmapFactory.decodeResource(context.getResources(), R.drawable.hearts);
                    coins = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);
                    powerups = BitmapFactory.decodeResource(context.getResources(), R.drawable.lightning);
                    powerups2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireball);
                    enemy = BitmapFactory.decodeResource(context.getResources(), R.drawable. bat);
                    player = BitmapFactory.decodeResource(context.getResources(), R.drawable. hero2);


                    // button
                    canvas.drawRect(screenY, 0, 10, 10, paint);
                    canvas.drawBitmap(hearts, 10, 20, paint);
                    canvas.drawBitmap(coins, 150, 75, paint);
                    canvas.drawText(String.valueOf(numbCoins), 205, 115, paint);
                    canvas.drawBitmap(powerups, 0, screenY - 100, paint);
                    canvas.drawBitmap(powerups2, screenX - 100, screenY - 100, paint);

                    //paint.setColor(Color.argb(255, 46, 46, 254));
                    //paint.setStyle(Paint.Style.STROKE);
                    ///paint.setStrokeWidth(4.5f);
                    //paint.setColor(Color.BLACK);
                    canvas.drawRect((screenX / 3) + 200, 18, screenX - 10, 70, paint);
                    //rectangle = new Rect((screenX / 3) + 210, 20, screenX, 30);
                    //rectangle = new Rect((screenX / 3) + 210, 20, screenX - 10, 70);
                    //canvas.drawRect(rectangle,paint);

                    /*
                    Bitmap[] clubs = {enemy};

                    for (int i =0; i<=5; i++){
                        Random rdX = new Random();
                        int i1 = rdX.nextInt((screenX-40) - 10) + 10;
                        Random rdY = new Random();
                        int i2 = rdX.nextInt((screenY-40) - 10) + 10;

                        canvas.drawBitmap(clubs[i], i1, i2, paint);

                    }*/

                    canvas.drawBitmap(enemy, screenX - 400, screenY - 200, paint);
                    canvas.drawBitmap(enemy, screenX - 220, screenY - 500, paint);
                    canvas.drawBitmap(enemy, 150, 250, paint);
                    canvas.drawBitmap(coins, screenX - 210, screenY - 300, paint);
                    canvas.drawBitmap(coins, 40, 200, paint);
                    canvas.drawBitmap(coins,screenX - 300, 350, paint);
                    canvas.drawBitmap(player,(screenX/2)-200,screenY/2, paint);





                }
                else{

                }
                // Unlock and draw the scene
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }
        private void control(){
            try {
                gameThread.sleep(17);
            } catch (InterruptedException e) {

            }
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
        // SurfaceView allows us to handle the onTouchEvent
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Has the player lifted their finger up?
                case MotionEvent.ACTION_UP:
                   // player.stopBoosting();
                    break;
                // Has the player touched the screen?
                case MotionEvent.ACTION_DOWN:
                    //player.setBoosting();
                    // If we are currently on the pause screen, start a new game
                    if(gameEnded){ startGame(); }
                    break;
            }
            return true;
        }
    }


