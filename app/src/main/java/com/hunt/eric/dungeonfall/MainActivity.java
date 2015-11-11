package com.hunt.eric.dungeonfall;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements  View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the button in our layout
        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        // Get a reference to the TextView in our layout
        // Listen for clicks
        buttonPlay.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
        finish();
    }
}
