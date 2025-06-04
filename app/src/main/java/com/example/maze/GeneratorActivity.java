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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        TextView labelSize = findViewById(R.id.label_size);
        SeekBar seekBar = findViewById(R.id.seekbar_size);
        mazeView = findViewById(R.id.maze_view);

        // Minimalna wartość: 5
        mazeSize = seekBar.getProgress() + 5;
        labelSize.setText("Rozmiar: " + mazeSize + "x" + mazeSize);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mazeSize = progress + 5;
                labelSize.setText("Rozmiar: " + mazeSize + "x" + mazeSize);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Button generateBtn = findViewById(R.id.button_generate);
        generateBtn.setOnClickListener(v -> {
            MazeGenerator generator = new MazeGenerator(mazeSize);
            generatedMaze = generator.generate();
            mazeView.setMaze(generatedMaze);
        });

        Button saveBtn = findViewById(R.id.button_save);
        saveBtn.setOnClickListener(v -> {
            if (generatedMaze == null) {
                Toast.makeText(this, "Wygeneruj najpierw labirynt!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("mazes", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            Map<String, ?> all = prefs.getAll();
            int count = 0;
            for (String key : all.keySet()) {
                if (key.startsWith("maze_" + mazeSize + "_")) {
                    count++;
                }
            }

            String name = "maze_" + mazeSize + "_" + (count + 1);

            StringBuilder sb = new StringBuilder();
            for (int[] row : generatedMaze) {
                for (int cell : row) {
                    sb.append(cell).append(",");
                }
                sb.append(";");
            }

            editor.putString(name, sb.toString());
            editor.apply();

            Toast.makeText(this, "Zapisano labirynt jako: " + name, Toast.LENGTH_SHORT).show();
        });

        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            finish(); // wraca do poprzedniego ekranu (HomeActivity)
        });

    }
}
