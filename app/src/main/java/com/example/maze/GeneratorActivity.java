package com.example.maze;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class GeneratorActivity extends AppCompatActivity {

    private MazeView mazeView;
    private int[][] generatedMaze;
    private int mazeSize;

    private final int MIN_SIZE = 10;
    private final int MAX_SIZE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        TextView labelSize = findViewById(R.id.label_size);
        SeekBar seekBar = findViewById(R.id.seekbar_size);
        mazeView = findViewById(R.id.maze_view);

        // Set SeekBar range from MIN_SIZE to MAX_SIZE
        seekBar.setMax(MAX_SIZE - MIN_SIZE);

        // Initialize maze size
        mazeSize = MIN_SIZE + seekBar.getProgress();
        labelSize.setText("Size: " + mazeSize + "x" + mazeSize);

        // Handle changes in SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mazeSize = progress + 10;
                labelSize.setText("Size: " + mazeSize + "x" + mazeSize);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Generate maze when button is clicked
        Button generateBtn = findViewById(R.id.button_generate);
        generateBtn.setOnClickListener(v -> {
            MazeGenerator generator = new MazeGenerator(mazeSize);
            generatedMaze = generator.generate();
            mazeView.setMaze(generatedMaze);
        });

        // Save generated maze to SharedPreferences
        Button saveBtn = findViewById(R.id.button_save);
        saveBtn.setOnClickListener(v -> {
            if (generatedMaze == null) {
                Toast.makeText(this, "Generate a maze first!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("mazes", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            // Count how many mazes of this size already exist
            Map<String, ?> all = prefs.getAll();
            int count = 0;
            for (String key : all.keySet()) {
                if (key.startsWith("maze_" + mazeSize + "_")) {
                    count++;
                }
            }

            // Build name based on size and index
            String name = "maze_" + mazeSize + "_" + (count + 1);

            // Convert maze to a single string
            StringBuilder sb = new StringBuilder();
            for (int[] row : generatedMaze) {
                for (int cell : row) {
                    sb.append(cell).append(",");
                }
                sb.append(";");
            }

            // Save maze
            editor.putString(name, sb.toString());
            editor.apply();

            Toast.makeText(this, "Maze saved as: " + name, Toast.LENGTH_SHORT).show();
        });

        // Return to home screen
        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            finish(); // go back to HomeActivity
        });

    }
}
