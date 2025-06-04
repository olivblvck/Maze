// HomeActivity.java
package com.example.maze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Start new maze generation screen
        Button startBtn = findViewById(R.id.button_start_game);
        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, GeneratorActivity.class);
            startActivity(intent);
        });

        // Navigate to screen to play saved mazes
        Button paramBtn = findViewById(R.id.button_parameters);
        paramBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ParametersActivity.class);
            startActivity(intent);
        });

        // Navigate to game rules screen
        findViewById(R.id.button_rules).setOnClickListener(v -> {
            startActivity(new Intent(this, RulesActivity.class));
        });


    }
}
