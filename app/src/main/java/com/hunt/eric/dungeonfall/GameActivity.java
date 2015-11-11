package com.hunt.eric.dungeonfall;

import android.app.Activity;
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
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

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
    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
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
}
