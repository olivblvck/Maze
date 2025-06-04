package com.example.maze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.maze.GameActivity;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;

public class ParametersActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayList<String> mazeKeys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        spinner = findViewById(R.id.spinner_mazes);
        Button buttonPlay = findViewById(R.id.button_play);

        SharedPreferences prefs = getSharedPreferences("mazes", MODE_PRIVATE);
        Map<String, ?> allMazes = prefs.getAll();

        if (allMazes.isEmpty()) {
            Toast.makeText(this, "Brak zapisanych labiryntów", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mazeKeys.addAll(allMazes.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mazeKeys);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        buttonPlay.setOnClickListener(v -> {
            String selectedKey = (String) spinner.getSelectedItem();
            String mazeData = prefs.getString(selectedKey, null);

            if (mazeData != null) {
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("maze_key", selectedKey);
                intent.putExtra("maze_data", mazeData);
                startActivity(intent);
            }
        });
        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            finish(); // zamyka tę aktywność, wraca do HomeActivity
        });

    }

}
