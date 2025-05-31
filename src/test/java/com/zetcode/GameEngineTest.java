// test/com/zetcode/GameEngineTest.java
package com.zetcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    static class StubPosGen implements GameEngine.PositionGenerator {
        private final Point[] pts; int idx = 0;
        StubPosGen(Point... pts) { this.pts = pts; }
        public Point next() {
            return idx < pts.length ? pts[idx++] : pts[pts.length - 1];
        }
    }

    private GameEngine engine;

    @BeforeEach
    void setUp() {
        engine = new GameEngine(300, 300, 10,
                new StubPosGen(new Point(50,50), new Point(100,100))
        );
        engine.init();
    }

    @Test
    void initialState() {
        List<Point> s = engine.getSnake();
        assertEquals(3, s.size());
        assertEquals(new Point(50,50), engine.getApple());
        assertEquals(GameEngine.Direction.RIGHT, engine.getDirection());
        assertFalse(engine.isGameOver());
    }

    // moves LEFT RIGHT UP DOWN
    @Test
    void moveRight() {
        engine.step();
        assertEquals(new Point(40,30), engine.getSnake().get(0));
    }

    @Test
    void moveUp() {
        engine.changeDirection(GameEngine.Direction.UP);
        engine.step();
        // started at (30,30) → up by 10 ⇒ (30,20)
        assertEquals(new Point(30,20), engine.getSnake().get(0));
    }

    @Test
    void moveDown() {
        engine.changeDirection(GameEngine.Direction.DOWN);
        engine.step();
        // started at (30,30) → down by 10 ⇒ (30,40)
        assertEquals(new Point(30,40), engine.getSnake().get(0));
    }

    @Test
    void moveLeft() {
        // Start by going UP so that LEFT becomes a legal turn, ORIGINAL DIRECTION=RIGHT
        engine.changeDirection(GameEngine.Direction.UP);
        // Now turn LEFT
        engine.changeDirection(GameEngine.Direction.LEFT);
        engine.step();
        // started at (30,30) → left by 10 ⇒ (20,30)
        assertEquals(new Point(20,30), engine.getSnake().get(0));
    }


    //check early exit before game is over
    @Test
    void noStepAfterGameOver() {
        // force a wall collision immediately
        engine.setSnake(Arrays.asList(
                new Point(290, 0),
                new Point(280, 0),
                new Point(270, 0)
        ));
        engine.changeDirection(GameEngine.Direction.UP);
        engine.step();  // hits y<0 → game over
        Point headAtDeath = engine.getSnake().get(0);
        engine.step();  // should do nothing
        assertEquals(headAtDeath, engine.getSnake().get(0));
    }

    @Test
    void cannotReverse() {
        engine.changeDirection(GameEngine.Direction.LEFT);
        engine.step();
        assertEquals(new Point(40,30), engine.getSnake().get(0));
    }

    @Test
    void appleEaten() {
        // stub apple placed directly in front
        GameEngine e2 = new GameEngine(300,300,10,
                new StubPosGen(new Point(40,30), new Point(123,123))
        );
        e2.init(); e2.step();
        assertEquals(4, e2.getSnake().size());
        assertEquals(new Point(123,123), e2.getApple());
    }

    //border collision
    @Test
    void rightWallCollision() {
        engine.setSnake(Arrays.asList(
                new Point(290,150),
                new Point(280,150),
                new Point(270,150)
        ));
        engine.step();
        assertTrue(engine.isGameOver());
    }

    @Test
    void topWallCollision() {
        engine.setSnake(Arrays.asList(
                new Point(30, 0),
                new Point(20, 0),
                new Point(10, 0)
        ));
        engine.changeDirection(GameEngine.Direction.UP);
        engine.step();
        assertTrue(engine.isGameOver());
    }

    @Test
    void leftWallCollision() {
        engine.setSnake(Arrays.asList(
                new Point(0, 30),
                new Point(10, 30),
                new Point(20, 30)
        ));
        engine.changeDirection(GameEngine.Direction.LEFT);
        engine.step();
        assertTrue(engine.isGameOver());
    }

    @Test
    void selfCollision() {
        engine.setSnake(Arrays.asList(
                new Point(50,50),
                new Point(60,50),
                new Point(70,50),
                new Point(80,50),
                new Point(90,50)
        ));
        engine.changeDirection(GameEngine.Direction.UP);
        engine.step();
        engine.changeDirection(GameEngine.Direction.RIGHT);
        engine.step();
        engine.changeDirection(GameEngine.Direction.DOWN);
        engine.step();
        assertTrue(engine.isGameOver());
    }

    //check locateApple() is called on demand
    @Test
    void testLocateAppleDirectly() {
        // new engine with a single-point stub
        GameEngine g = new GameEngine(100, 100, 10,
                new StubPosGen(new Point(77, 33))
        );
        g.init();             // first apple from stub (77,33)
        g.locateApple();      // second call should also yield (77,33)
        assertEquals(new Point(77,33), g.getApple());
    }

    //check valid direction change paths
    @Test
    void validDirectionChanges() {
        engine.changeDirection(GameEngine.Direction.DOWN);
        assertEquals(GameEngine.Direction.DOWN, engine.getDirection());
        engine.changeDirection(GameEngine.Direction.LEFT);
        assertEquals(GameEngine.Direction.LEFT, engine.getDirection());
        engine.changeDirection(GameEngine.Direction.UP);
        assertEquals(GameEngine.Direction.UP, engine.getDirection());
    }
}
