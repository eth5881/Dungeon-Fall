package com.hunt.eric.dungeonfall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Hero {
    private Bitmap bitmap;
    private Bitmap resizedBitmap;
    private int x,y;

    private int health;

    private boolean attacking;

    // Stop ship leaving the screen
    private int maxX;
    private int minX;

    // A hit box for collision detection
    private Rect hitBox;

    // Constructor
    public Hero(Context context, int screenX, int screenY) {
        x = 50;
        y = 50;

        health = 4;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

        maxX = screenX - bitmap.getWidth();
        minX = 0;

        // Initialize the hit box
        hitBox = new Rect(x, y, resizedBitmap.getWidth(), resizedBitmap.getHeight());

    }

    public void update() {
        // Don't let ship stray off screen
        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }

        // Refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + resizedBitmap.getWidth();
        hitBox.bottom = y + resizedBitmap.getHeight();

    }

    public void setAttack() {

        attacking = true;
    }

    public void stopAttack() {

        attacking = false;
    }

    //Getters
    public Bitmap getBitmap() {

        return resizedBitmap;
    }

    public int getX() {

        return x;
    }

    public int getY() {

        return y;
    }

    public Rect getHitbox(){
        return hitBox;
    }

    public int getHealth() {

        return health;
    }

    public void reduceShieldStrength(){
        health --;
    }

}
