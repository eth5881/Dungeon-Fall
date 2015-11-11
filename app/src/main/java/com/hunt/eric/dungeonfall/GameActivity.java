package com.hunt.eric.dungeonfall;

import android.app.Activity;
<<<<<<< HEAD
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class GameActivity extends Activity {

    // Our object to handle the View
    private GameView gameView;

    // This is where the "Play" button from HomeActivity sends us
    @Override
    protected void onCreate(Bundle savedInstanceState) {
=======
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Shawn on 11/10/2015.
 */
public class GameActivity extends Activity implements View.OnClickListener {
    private TDView gameView;
    private Button mStoreButton;
    @Override
    protected void onCreate (Bundle savedInstanceState){
>>>>>>> bf5af08837ffd79b97c7dee3c4a7f637123b7a5f
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

<<<<<<< HEAD
        // Create an instance of our Tappy Defender View
        // Also passing in this.
        // Also passing in the screen resolution to the constructor
        gameView = new GameView(this, size.x, size.y);

        // Make our gameView the view for the Activity
        setContentView(gameView);

    }

=======
        // Create an instance of our Tappy Defender View (TDView)
        // passing in "this" which is the Context of our app, and
        // screen resolution of device being used to run app
        gameView = new TDView(this, size.x, size.y);
        // Make our gameView the view for the Activity
        setContentView(gameView);
        //setContentView(R.layout.activity_game);
        //gameView = (TDView)findViewById(R.id.custom_view);



        // mStoreButton = (Button)findViewById(R.id.store_button);
        //setContentView(R.layout.activity_game);
        //mStoreButton.setOnClickListener(this);

    }
>>>>>>> bf5af08837ffd79b97c7dee3c4a7f637123b7a5f
    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
<<<<<<< HEAD

=======
>>>>>>> bf5af08837ffd79b97c7dee3c4a7f637123b7a5f
    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
<<<<<<< HEAD

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

=======
    // If the player hits the back button, quit the app
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }
>>>>>>> bf5af08837ffd79b97c7dee3c4a7f637123b7a5f
}
