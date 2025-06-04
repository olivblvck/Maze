package com.example.maze;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

// MazeGenerator uses randomized DFS to generate
// a perfect maze (without cycles or isolated sections).

public class MazeGenerator {

    private final int size; // Maze size (NxN)
    private final int[][] maze; // Maze representation
    private final Random rand = new Random(); // Random number generator

    public MazeGenerator(int size) {
        this.size = size;
        maze = new int[size][size];
    }

    // Public method to start maze generation
    // return 2D array representing the maze
    public int[][] generate() {
        dfs(0, 0, new boolean[size][size]); // Start DFS from top-left
        return maze;
    }

    // Recursive depth-first search to carve paths.
    private void dfs(int x, int y, boolean[][] visited) {
        visited[x][y] = true;
        // Possible directions: down, up, right, left
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        Collections.shuffle(Arrays.asList(dirs)); // Randomize directions

        for (int[] dir : dirs) {
            int nx = x + dir[0], ny = y + dir[1];
            if (nx >= 0 && ny >= 0 && nx < size && ny < size && !visited[nx][ny]) {
                maze[x][y] |= encode(dir); // Carve path from current to neighbor
                maze[nx][ny] |= encode(new int[]{-dir[0], -dir[1]}); // Carve path from current to neighbor
                dfs(nx, ny, visited);
            }
        }
    }

    //Encode direction into a bit flag:
    // 1 = down, 2 = up, 4 = right, 8 = left
    private int encode(int[] dir) {
        if (dir[0] == 1) return 1;     // down
        if (dir[0] == -1) return 2;    // up
        if (dir[1] == 1) return 4;     // right
        return 8;                     // left
    }
}
