package com.example.maze;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

public class MazeGenerator {

    private final int size;
    private final int[][] maze;
    private final Random rand = new Random();

    public MazeGenerator(int size) {
        this.size = size;
        maze = new int[size][size];
    }

    public int[][] generate() {
        dfs(0, 0, new boolean[size][size]);
        return maze;
    }

    private void dfs(int x, int y, boolean[][] visited) {
        visited[x][y] = true;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        Collections.shuffle(Arrays.asList(dirs));

        for (int[] dir : dirs) {
            int nx = x + dir[0], ny = y + dir[1];
            if (nx >= 0 && ny >= 0 && nx < size && ny < size && !visited[nx][ny]) {
                maze[x][y] |= encode(dir);
                maze[nx][ny] |= encode(new int[]{-dir[0], -dir[1]});
                dfs(nx, ny, visited);
            }
        }
    }

    private int encode(int[] dir) {
        if (dir[0] == 1) return 1;     // dół
        if (dir[0] == -1) return 2;    // góra
        if (dir[1] == 1) return 4;     // prawo
        return 8;                     // lewo
    }
}
