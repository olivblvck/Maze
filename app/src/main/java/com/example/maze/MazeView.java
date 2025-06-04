package com.example.maze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

//Custom View responsible for rendering the maze,
//player position, goal point and visited path.

public class MazeView extends View {

    private int[][] maze; // 2D representation of the maze
    private int playerX = 0, playerY = 0; // Current player position
    private int cellSize; // Size of a single maze cell
    private Paint wallPaint, playerPaint, pathPaint, goalPaint; // Paint objects for drawing
    private List<List<Integer>> visited; // List of visited coordinates
    private int offsetX = 0, offsetY = 0; // Maze offset for centering

    public MazeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint for maze walls
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(4);

        // Paint for player (red)
        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        // Paint for goal point (green)
        goalPaint = new Paint();
        goalPaint.setColor(Color.GREEN);

        // Paint for visited path (blue)
        pathPaint = new Paint();
        pathPaint.setColor(Color.BLUE);
        pathPaint.setStrokeWidth(2);
    }


    public void setMaze(int[][] maze) {
        this.maze = maze;
        invalidate();  // Request redraw
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

        // Draw maze walls
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int left = offsetX + x * cellSize;
                int top = offsetY + y * cellSize;

                int val = maze[y][x];

                // walls
                if ((val & 1) == 0) // bottom
                    canvas.drawLine(left, top + cellSize, left + cellSize, top + cellSize, wallPaint);
                if ((val & 2) == 0) // up
                    canvas.drawLine(left, top, left + cellSize, top, wallPaint);
                if ((val & 4) == 0) // right
                    canvas.drawLine(left + cellSize, top, left + cellSize, top + cellSize, wallPaint);
                if ((val & 8) == 0) // left
                    canvas.drawLine(left, top, left, top + cellSize, wallPaint);
            }
        }

        // Draw goal (bottom-right cell)
        int gx = size - 1;
        int gy = size - 1;
        canvas.drawCircle(offsetX + gx * cellSize + cellSize / 2,
                offsetY + gy * cellSize + cellSize / 2,
                cellSize / 4, goalPaint);

        // Draw visited path
        if (visited != null) {
            for (List<Integer> p : visited) {
                int cx = p.get(0);
                int cy = p.get(1);
                canvas.drawCircle(offsetX + cx * cellSize + cellSize / 2,
                        offsetY + cy * cellSize + cellSize / 2,
                        cellSize / 6, pathPaint);
            }
        }

        // Draw player position
        canvas.drawCircle(offsetX + playerX * cellSize + cellSize / 2,
                offsetY + playerY * cellSize + cellSize / 2,
                cellSize / 3, playerPaint);
    }
    // Alternate setter for visited list
    public void setVisitedPath(List<List<Integer>> visited) {
        this.visited = visited;
        invalidate();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We want a square view: width = height = min(width, height)
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        );
    }


}
