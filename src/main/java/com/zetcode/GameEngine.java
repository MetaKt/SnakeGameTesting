// src/com/zetcode/GameEngine.java
package com.zetcode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    public interface PositionGenerator {
        Point next();
    }

    private final int width, height, dotSize;
    private final PositionGenerator posGen;

    private List<Point> snake;
    private Point apple;
    private Direction direction;
    private boolean inGame;

    public GameEngine(int width, int height, int dotSize, PositionGenerator posGen) {
        this.width = width; this.height = height; this.dotSize = dotSize; this.posGen = posGen;
    }

    public void init() {
        snake = new ArrayList<>();
        int startX = dotSize * 3, startY = dotSize * 3;
        for (int i = 0; i < 3; i++)
            snake.add(new Point(startX - i * dotSize, startY));
        direction = Direction.RIGHT;
        inGame = true;
        locateApple();
    }

    public void locateApple() {
        Point p = posGen.next();
        apple = new Point(p);
    }

    public void changeDirection(Direction newDir) {
        if ((direction == Direction.LEFT  && newDir == Direction.RIGHT) ||
                (direction == Direction.RIGHT && newDir == Direction.LEFT)  ||
                (direction == Direction.UP    && newDir == Direction.DOWN) ||
                (direction == Direction.DOWN  && newDir == Direction.UP))
            return;
        direction = newDir;
    }

    public void step() {
        if (!inGame) return;
        move(); checkApple(); checkCollision();
    }

    private void move() {
        // … copy body code …
        Point head = snake.get(0), n = new Point(head);
        switch (direction) {
            case UP:
                n.y -= dotSize;
                break;
            case DOWN:
                n.y += dotSize;
                break;
            case LEFT:
                n.x -= dotSize;
                break;
            case RIGHT:
                n.x += dotSize;
                break;
        }
        snake.set(0, n);
    }


    private void checkApple() {
        if (snake.get(0).equals(apple)) {
            snake.add(new Point(snake.get(snake.size() - 1)));
            locateApple();
        }
    }

    private void checkCollision() {
        Point head = snake.get(0);
        if (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height) {
            inGame = false; return;
        }
        for (int i = 1; i < snake.size(); i++)
            if (head.equals(snake.get(i))) {
                inGame = false; return;
            }
    }

    // ─── Getters ─────────────────────────────────

    public List<Point> getSnake()     { return new ArrayList<>(snake); }
    public Point getApple()           { return new Point(apple); }
    public Direction getDirection()   { return direction; }
    public boolean isGameOver()       { return !inGame; }

    // ─── Test Helpers ────────────────────────────

    public void setSnake(List<Point> s) { snake = new ArrayList<>(s); }
}
