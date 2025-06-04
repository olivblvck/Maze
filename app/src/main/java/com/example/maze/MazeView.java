package com.example.maze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class MazeView extends View {

    private int[][] maze;
    private int playerX = 0, playerY = 0;
    private int cellSize;
    private Paint wallPaint, playerPaint, pathPaint, goalPaint;
    private List<List<Integer>> visited;
    private int offsetX = 0, offsetY = 0;

    public MazeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(4);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        goalPaint = new Paint();
        goalPaint.setColor(Color.GREEN);

        pathPaint = new Paint();
        pathPaint.setColor(Color.BLUE);
        pathPaint.setStrokeWidth(2);
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
        invalidate();
    }

    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        invalidate();
    }

    public void setVisited(List<List<Integer>> visited) {
        this.visited = visited;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maze == null) return;

        int size = maze.length;
        cellSize = Math.min(getWidth(), getHeight()) / size;
        offsetX = (getWidth() - size * cellSize) / 2;
        offsetY = (getHeight() - size * cellSize) / 2;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int left = offsetX + x * cellSize;
                int top = offsetY + y * cellSize;

                int val = maze[y][x];

                // Ściany
                if ((val & 1) == 0) // dół
                    canvas.drawLine(left, top + cellSize, left + cellSize, top + cellSize, wallPaint);
                if ((val & 2) == 0) // góra
                    canvas.drawLine(left, top, left + cellSize, top, wallPaint);
                if ((val & 4) == 0) // prawo
                    canvas.drawLine(left + cellSize, top, left + cellSize, top + cellSize, wallPaint);
                if ((val & 8) == 0) // lewo
                    canvas.drawLine(left, top, left, top + cellSize, wallPaint);
            }
        }

        // Meta
        int gx = size - 1;
        int gy = size - 1;
        canvas.drawCircle(offsetX + gx * cellSize + cellSize / 2,
                offsetY + gy * cellSize + cellSize / 2,
                cellSize / 4, goalPaint);

        // Ścieżka
        if (visited != null) {
            for (List<Integer> p : visited) {
                int cx = p.get(0);
                int cy = p.get(1);
                canvas.drawCircle(offsetX + cx * cellSize + cellSize / 2,
                        offsetY + cy * cellSize + cellSize / 2,
                        cellSize / 6, pathPaint);
            }
        }

        // Gracz
        canvas.drawCircle(offsetX + playerX * cellSize + cellSize / 2,
                offsetY + playerY * cellSize + cellSize / 2,
                cellSize / 3, playerPaint);
    }
}
