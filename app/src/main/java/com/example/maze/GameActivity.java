package com.example.maze;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private MazeView mazeView;
    private int[][] maze;
    private int x = 0, y = 0;
    private int playerX = 0, playerY = 0;
    private int size;
    private List<List<Integer>> visited = new ArrayList<>();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastMoveTime = 0;
    private int currentOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        currentOrientation = getResources().getConfiguration().orientation;

        if (savedInstanceState != null) {
            playerX = savedInstanceState.getInt("playerX");
            playerY = savedInstanceState.getInt("playerY");
            x = playerX;
            y = playerY;

            int width = savedInstanceState.getInt("mazeWidth", -1);
            int height = savedInstanceState.getInt("mazeHeight", -1);
            int[] flat = savedInstanceState.getIntArray("mazeFlat");

            if (flat != null && width > 0 && height > 0) {
                maze = new int[width][height];
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        maze[i][j] = flat[i * height + j];
                    }
                }
            }

            Object obj = savedInstanceState.getSerializable("visited");
            if (obj instanceof List) {
                visited = (List<List<Integer>>) obj;
            }
        } else {
            String raw = getIntent().getStringExtra("maze_data");
            maze = parseMaze(raw);
        }

        mazeView = findViewById(R.id.maze_view);
        TextView status = findViewById(R.id.text_status);

        size = maze.length;
        mazeView.setMaze(maze);
        mazeView.setPlayerPosition(x, y);

        Button up = findViewById(R.id.button_up);
        Button down = findViewById(R.id.button_down);
        Button left = findViewById(R.id.button_left);
        Button right = findViewById(R.id.button_right);
        Button backBtn = findViewById(R.id.button_back);
        Button rotateBtn = findViewById(R.id.button_rotate);

        up.setOnClickListener(v -> move(0, -1, status));
        down.setOnClickListener(v -> move(0, 1, status));
        left.setOnClickListener(v -> move(-1, 0, status));
        right.setOnClickListener(v -> move(1, 0, status));

        backBtn.setOnClickListener(v -> finish());

        rotateBtn.setOnClickListener(v -> {
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private int[][] parseMaze(String raw) {
        String[] rows = raw.split(";");
        int[][] result = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cells = rows[i].split(",");
            result[i] = new int[cells.length];
            for (int j = 0; j < cells.length; j++) {
                if (!cells[j].isEmpty()) result[i][j] = Integer.parseInt(cells[j]);
            }
        }
        return result;
    }

    private void move(int dx, int dy, TextView status) {
        int newX = x + dx;
        int newY = y + dy;

        visited.add(Arrays.asList(x, y));
        mazeView.setVisited(visited);


        if (newX < 0 || newY < 0 || newX >= size || newY >= size)
            return;

        int current = maze[y][x];
        boolean canMove = false;

        if (dx == 1 && (current & 4) != 0) canMove = true;
        if (dx == -1 && (current & 8) != 0) canMove = true;
        if (dy == 1 && (current & 1) != 0) canMove = true;
        if (dy == -1 && (current & 2) != 0) canMove = true;

        if (canMove) {
            x = newX;
            y = newY;
            playerX = x;
            playerY = y;
            visited.add(Arrays.asList(x, y));
            mazeView.setPlayerPosition(x, y);

            if (x == size - 1 && y == size - 1) {
                status.setText("Gratulacje! Wyszedłeś z labiryntu!");
                Toast.makeText(this, "Wygrana!", Toast.LENGTH_LONG).show();
                findViewById(R.id.button_back).setVisibility(View.VISIBLE);
            } else {
                status.setText("Pozycja: " + x + ", " + y);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        currentOrientation = newConfig.orientation;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("playerX", playerX);
        outState.putInt("playerY", playerY);

        if (maze != null) {
            outState.putInt("mazeWidth", maze.length);
            outState.putInt("mazeHeight", maze[0].length);

            int[] flat = new int[maze.length * maze[0].length];
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[0].length; j++) {
                    flat[i * maze[0].length + j] = maze[i][j];
                }
            }
            outState.putIntArray("mazeFlat", flat);
        }

        outState.putSerializable("visited", new ArrayList<>(visited));
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            float ax = event.values[0];
            float ay = event.values[1];

            long now = System.currentTimeMillis();
            if (now - lastMoveTime < 300) return;

            int dx = 0, dy = 0;

            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                if (Math.abs(ax) > Math.abs(ay)) {
                    if (ax > 2) dx = -1;
                    else if (ax < -2) dx = 1;
                } else {
                    if (ay < -2) dy = -1;
                    else if (ay > 2) dy = 1;
                }
            } else {
                if (Math.abs(ay) > Math.abs(ax)) {
                    if (ay > 2) dx = 1;
                    else if (ay < -2) dx = -1;
                } else {
                    if (ax < -2) dy = -1;
                    else if (ax > 2) dy = 1;
                }
            }

            if (dx != 0 || dy != 0) {
                move(dx, dy, findViewById(R.id.text_status));
                lastMoveTime = now;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }
}
