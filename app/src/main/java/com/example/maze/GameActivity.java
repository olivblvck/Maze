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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// Main gameplay activity.
// Displays the maze, handles player movement (buttons and accelerometer),
// and manages orientation and state restoration.

public class GameActivity extends AppCompatActivity {

    private MazeView mazeView;// Custom view that renders the maze
    private int[][] maze; // 2D array representing the maze structure
    private int x = 0, y = 0; // Current player position (dynamic)
    private int playerX = 0, playerY = 0;  // Saved player position for orientation change
    private int size; // Size of the maze
    private List<List<Integer>> visited = new ArrayList<>(); // List of visited coordinates

    private SensorManager sensorManager; // For accessing device sensors
    private Sensor accelerometer;  // Accelerometer sensor
    private long lastMoveTime = 0; // Timestamp of last move to avoid rapid updates
    private int currentOrientation;  // Used to control custom orientation toggle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game); // Load layout for game screen

        // Save current orientation (portrait or landscape)
        currentOrientation = getResources().getConfiguration().orientation;

        // Restore state if exists (e.g., after screen rotation)
        if (savedInstanceState != null) {
            playerX = savedInstanceState.getInt("playerX");
            playerY = savedInstanceState.getInt("playerY");
            x = playerX;
            y = playerY;

            int width = savedInstanceState.getInt("mazeWidth", -1);
            int height = savedInstanceState.getInt("mazeHeight", -1);
            int[] flat = savedInstanceState.getIntArray("mazeFlat");

            // Reconstruct the 2D maze array from saved 1D version
            if (flat != null && width > 0 && height > 0) {
                maze = new int[width][height];
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        maze[i][j] = flat[i * height + j];
                    }
                }
            }

            // Restore visited path
            Object obj = savedInstanceState.getSerializable("visited");
            if (obj instanceof List) {
                visited = (List<List<Integer>>) obj;
            }
        } else {
            String raw = getIntent().getStringExtra("maze_data");
            maze = parseMaze(raw);
        }

        // Find and initialize MazeView (custom view rendering the maze)
        mazeView = findViewById(R.id.maze_view);
        TextView status = findViewById(R.id.text_status); // TextView showing player status

        size = maze.length; // Get maze size from the array length
        mazeView.setMaze(maze); // Provide the maze data to MazeView
        mazeView.setPlayerPosition(x, y); // Set initial player position
        mazeView.setVisitedPath(visited); // Set initial player position

        // Reference buttons from layout
        Button up = findViewById(R.id.button_up);
        Button down = findViewById(R.id.button_down);
        Button left = findViewById(R.id.button_left);
        Button right = findViewById(R.id.button_right);
        Button backBtn = findViewById(R.id.button_back);

        // Movement control via arrow buttons
        up.setOnClickListener(v -> move(0, -1, status));
        down.setOnClickListener(v -> move(0, 1, status));
        left.setOnClickListener(v -> move(-1, 0, status));
        right.setOnClickListener(v -> move(1, 0, status));

        // Return to previous screen
        backBtn.setOnClickListener(v -> finish());

        // Rotate screen manually via button
        Button rotateBtn = findViewById(R.id.button_rotate);
        rotateBtn.setOnClickListener(v -> rotateScreen());

        // Sensor initialization
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    //Parses the flat maze string (e.g. "1,2,3;4,5,6") into a 2D array
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

    //Handles player movement and collision detection.
    //Updates position and visual path.
    private void move(int dx, int dy, TextView status) {
        int newX = x + dx;
        int newY = y + dy;

        visited.add(Arrays.asList(x, y));  // Add current position to visited path
        mazeView.setVisited(visited);

        // Prevent out-of-bounds movement
        if (newX < 0 || newY < 0 || newX >= size || newY >= size)
            return;

        int current = maze[y][x];
        boolean canMove = false;

        // Bitmask for checking walls (1 = bottom, 2 = top, 4 = right, 8 = left)
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
            mazeView.setPlayerPosition(x, y); // Move player on view

            // Check win condition (bottom-right corner)
            if (x == size - 1 && y == size - 1) {
                status.setText("Congratulations! You exited the maze!");
                Toast.makeText(this, "You won!", Toast.LENGTH_LONG).show();
                findViewById(R.id.button_back).setVisibility(View.VISIBLE);
            } else {
                status.setText("Position: " + x + ", " + y);
            }
        }
    }

    // Updates orientation flag on system configuration change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        currentOrientation = newConfig.orientation;
    }

    //Saves current state before orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("playerX", playerX);
        outState.putInt("playerY", playerY);

        if (maze != null) {
            outState.putInt("mazeWidth", maze.length);
            outState.putInt("mazeHeight", maze[0].length);

            // Flatten the 2D maze array for storage
            int[] flat = new int[maze.length * maze[0].length];
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[0].length; j++) {
                    flat[i * maze[0].length + j] = maze[i][j];
                }
            }
            outState.putIntArray("mazeFlat", flat);
        }

        outState.putSerializable("visited", new ArrayList<>(visited)); // Save visited pat
    }

    // Accelerometer listener: interprets device tilts as movement
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            float ax = event.values[0];
            float ay = event.values[1];

            long now = System.currentTimeMillis();
            if (now - lastMoveTime < 300) return; // Delay between moves

            int dx = 0, dy = 0;

            // Interpret tilt based on current screen orientation
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

    // Resume listening to sensor
    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    // Stop listening to sensor
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    // Manual screen orientation toggle via button
    private void rotateScreen() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Delay before reloading layout to ensure orientation settled
        new Handler().postDelayed(this::reloadLayout, 500);
    }

    // Rebind views and state after manual rotation
    private void reloadLayout() {
        setContentView(R.layout.activity_game);

        // Re-bind views
        mazeView = findViewById(R.id.maze_view);
        TextView status = findViewById(R.id.text_status);
        Button up = findViewById(R.id.button_up);
        Button down = findViewById(R.id.button_down);
        Button left = findViewById(R.id.button_left);
        Button right = findViewById(R.id.button_right);
        Button backBtn = findViewById(R.id.button_back);
        Button rotateBtn = findViewById(R.id.button_rotate);

        // Set up listeners again
        up.setOnClickListener(v -> move(0, -1, status));
        down.setOnClickListener(v -> move(0, 1, status));
        left.setOnClickListener(v -> move(-1, 0, status));
        right.setOnClickListener(v -> move(1, 0, status));
        backBtn.setOnClickListener(v -> finish());
        rotateBtn.setOnClickListener(v -> rotateScreen());

        // Restore maze state
        mazeView.setMaze(maze);
        mazeView.setPlayerPosition(x, y);
        mazeView.setVisitedPath(visited);
    }
}
