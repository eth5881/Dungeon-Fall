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
        private Bitmap resizedBg;
        private Bitmap hearts;
        private Bitmap gold;
        private Enemy coin1;
        private Enemy coin2;
        private Enemy coin3;
        private Enemy enemy1;
        private Enemy enemy2;
        private Enemy enemy3;
        private Array enemies;
        private Hero hero;
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

            hero = new Hero(context, screenX, screenY);

            enemy1 = new Enemy(context, (int)(Math.random()*screenX), (int)(Math.random()*screenY), 30, 47, 15, 3, BitmapFactory.decodeResource(context.getResources(), R.drawable.bat));
            enemy2 = new Enemy(context, (int)(Math.random()*screenX), (int)(Math.random()*screenY), 30, 47, 15, 3, BitmapFactory.decodeResource(context.getResources(), R.drawable.bat));
            enemy3 = new Enemy(context, (int)(Math.random()*screenX), (int)(Math.random()*screenY), 30, 47, 15, 3, BitmapFactory.decodeResource(context.getResources(), R.drawable.bat));
            coin1 = new Enemy(context, (int)(Math.random()*screenX), (int)(Math.random()*screenY), 32, 32, 15, 8, BitmapFactory.decodeResource(context.getResources(), R.drawable.gold));
            coin2 = new Enemy(context, (int)(Math.random()*screenX), (int)(Math.random()*screenY), 32, 32, 15, 8, BitmapFactory.decodeResource(context.getResources(), R.drawable.gold));
            coin3 = new Enemy(context, (int)(Math.random()*screenX), (int)(Math.random()*screenY), 32, 32, 15, 8, BitmapFactory.decodeResource(context.getResources(), R.drawable.gold));

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
            enemy1.update(System.currentTimeMillis());
            enemy2.update(System.currentTimeMillis());
            enemy3.update(System.currentTimeMillis());

            coin1.update(System.currentTimeMillis());
            coin2.update(System.currentTimeMillis());
            coin3.update(System.currentTimeMillis());
        }
        private void draw(){
            if (ourHolder.getSurface().isValid()) {
                //First we lock the area of memory we will be drawing to
                canvas = ourHolder.lockCanvas();
                paint.setColor(Color.argb(255, 255, 255, 255));
                if(!gameEnded) {
                    // set background to image and scale according to size of device
                    mBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
                    resizedBg = Bitmap.createScaledBitmap(mBackground, screenX, screenY + 100, false);
                    canvas.drawBitmap(resizedBg, 0, 0, paint);
                    // Draw the hud
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(35);
                    canvas.drawText("Level: " + level, (screenX / 3) + 190, 115, paint);
                    canvas.drawText("Floor: " + floor, 10, 115, paint);
                    canvas.drawText("Exp: " + exp, screenX - 130, 115, paint);
                    canvas.drawText("MP:", (screenX / 3) + 130, 55, paint);

                    hearts = BitmapFactory.decodeResource(context.getResources(), R.drawable.hearts);
                    gold = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);
                    powerups = BitmapFactory.decodeResource(context.getResources(), R.drawable.lightning);
                    powerups2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireball);
                    // Draw the player and enemies
                    canvas.drawBitmap(hero.getBitmap(), hero.getX(), hero.getY(), paint);
                    enemy1.draw(canvas);
                    enemy2.draw(canvas);
                    enemy3.draw(canvas);
                    coin1.draw(canvas);
                    coin2.draw(canvas);
                    coin3.draw(canvas);

                    // button
                    canvas.drawRect(screenY, 0, 10, 10, paint);
                    canvas.drawBitmap(hearts, 10, 20, paint);
                    canvas.drawBitmap(gold, 155, 78, paint);
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


