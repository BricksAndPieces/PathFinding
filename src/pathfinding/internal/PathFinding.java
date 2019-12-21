package pathfinding.internal;

import mazegenerator.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("All")
public interface PathFinding {

    abstract boolean calcStep();

    abstract boolean foundPath();

    abstract List<Node> openSet();

    abstract List<Node> closedSet();

    abstract Node currentNode();

    abstract Node start();

    abstract Node end();

    default boolean calcPath() {
        while(!calcStep()) { }
        return foundPath();
    }

    default List<Node> getPath() {
        List<Node> path = new ArrayList<>();
        Node current = currentNode();
        while(current != null) {
            path.add(current);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    default Node[] neighbors(Maze maze, Node n) {
        Node[] neighbors = new Node[4];
        neighbors[0] = getNode(maze, n.x+1, n.y);
        neighbors[1] = getNode(maze, n.x, n.y+1);
        neighbors[2] = getNode(maze, n.x-1, n.y);
        neighbors[3] = getNode(maze, n.x, n.y-1);
        return neighbors;
    }

    default Node getNode(Maze maze, int x, int y) {
        Node n = maze.getNode(x, y);
        return n != null && !n.blocked ? n : null;
    }
}