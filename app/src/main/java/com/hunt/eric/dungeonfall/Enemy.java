package com.hunt.eric.dungeonfall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Enemy{
    private Bitmap bitmap;
    private Bitmap resizedBitmap;
    private int x, y;

    private Rect sourceRect;    // the rectangle to be drawn from the animation bitmap
    private int frameNr;        // number of frames in animation
    private int currentFrame;   // the current frame
    private long frameTicker;   // the time of the last frame update
    private int framePeriod;    // milliseconds between each frame (1000/fps)
    private int spriteWidth;    // the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;   // the height of the sprite

    // A hit box for collision detection
    private Rect hitBox;

    // Constructor
    public Enemy(Context context, int x, int y, int width, int height, int fps, int frameCount) {
        /*Random generator = new Random();
        int whichBitmap = generator.nextInt(3);
        switch (whichBitmap){
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bat);
                break;

            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bat);
                break;

            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bat);
                break;
        }*/

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bat);
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150 * 3, 100, false);
        this.x = x;
        this.y = y;
        currentFrame = 0;
        frameNr = frameCount;
        spriteWidth = resizedBitmap.getWidth() / frameCount;
        spriteHeight = resizedBitmap.getHeight();
        sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
        framePeriod = 1000 / fps;
        frameTicker = 0l;

        // Initialize the hit box
        hitBox = new Rect(x, y, resizedBitmap.getWidth(), resizedBitmap.getHeight());

    }

    public void update(long gameTime) {
        if (gameTime > frameTicker + framePeriod) {
            frameTicker = gameTime;
            // increment the frame
            currentFrame++;
            if (currentFrame >= frameNr) {
                currentFrame = 0;
            }
        }
        // define the rectangle to cut out sprite
        this.sourceRect.left = currentFrame * spriteWidth;
        this.sourceRect.right = this.sourceRect.left + spriteWidth;

        // Refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + resizedBitmap.getWidth();
        hitBox.bottom = y + resizedBitmap.getHeight();
    }

    public void draw(Canvas canvas) {
        // where to draw the sprite
        Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
        canvas.drawBitmap(resizedBitmap, sourceRect, destRect, null);
    }

    //Getters and Setters
    public Bitmap getBitmap(){

        return resizedBitmap;
    }
    public int getX() {

        return x;
    }

    public int getY() {

        return y;
    }

    public void setX(int x){

        this.x = x;
    }

    public void setY(int y){

        this.y = y;
    }

    public Rect getHitbox(){
        return hitBox;
    }
}

