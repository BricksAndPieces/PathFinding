package mazegenerator;

import pathfinding.internal.Node;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("All")
public class MazeGenerator {

    private static final int[] xDir = {1, -1, 0, 0};
    private static final int[] yDir = {0, 0, 1, -1};
    private static final Random rng = ThreadLocalRandom.current();

    public static Maze emptyMaze(int width, int height) {
        Maze maze = new Maze(width, height, false);
        addBorderWalls(maze);
        return maze;
    }

    public static Maze randomMaze(int width, int height, double wallChance) {
        Maze maze = new Maze(width, height, false);
        for(Node[] row : maze.getData()) for(Node n : row)
            n.blocked = Math.random() < wallChance;

        addBorderWalls(maze);
        return maze;
    }

    public static Maze generateMaze(int width, int height) {
        Maze maze = new Maze(width, height, true);
        createPath(maze, 1, 1);
        return maze;
    }

    private static void createPath(Maze maze, int x, int y) {
        for(int i = 0, d = rng.nextInt(4); i < 4; i++, d = (d+1) % 4) {
            Node n1 = maze.getNode(x + xDir[d], y + yDir[d]);
            if(n1 == null) continue;

            Node n2 = maze.getNode(n1.x+xDir[d], n1.y+yDir[d]);
            if(n2 != null && n1.blocked && n2.blocked) {
                n1.blocked = false;
                n2.blocked = false;
                createPath(maze, n2.x, n2.y);
            }
        }
    }

    private static void addBorderWalls(Maze maze) {
        for(int x = 0; x < maze.getWidth(); x++) {
            maze.getNode(x, 0).blocked = true;
            maze.getNode(x, maze.getHeight()-1).blocked = true;
        }

        for(int y = 0; y < maze.getHeight(); y++) {
            maze.getNode(0, y).blocked = true;
            maze.getNode(maze.getWidth()-1, y).blocked = true;
        }
    }
}