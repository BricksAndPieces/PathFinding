package pathfinding;

import mazegenerator.Maze;
import pathfinding.internal.Node;
import pathfinding.internal.PathFinding;

import java.util.LinkedList;
import java.util.List;

public class BFS implements PathFinding {

    private final Node start;
    private final Node end;
    private final Maze maze;

    private final List<Node> openSet = new LinkedList<>();
    private final List<Node> closedSet = new LinkedList<>();
    private Node currentNode;

    public BFS(Maze maze, Node start, Node end) {
        this.maze = maze;
        this.start = start;
        this.end = end;

        this.start.blocked = false;
        this.end.blocked = false;
        this.currentNode = start;
        openSet.add(start);
    }

    @Override
    public boolean calcStep() {
        if(openSet.isEmpty())
            return true;

        currentNode = openSet.remove(0);
        closedSet.add(currentNode);

        if(foundPath())
            return true;

        for(Node n : neighbors(maze, currentNode)) {
            if(n == null || closedSet.contains(n))
                continue;

            n.parent = currentNode;
            if(!openSet.contains(n))
                openSet.add(n);
        }

        return false;
    }

    @Override
    public boolean foundPath() {
        return currentNode.equals(end);
    }

    @Override
    public List<Node> openSet() {
        return openSet;
    }

    @Override
    public List<Node> closedSet() {
        return closedSet;
    }

    @Override
    public Node currentNode() {
        return currentNode;
    }

    @Override
    public Node start() {
        return start;
    }

    @Override
    public Node end() {
        return end;
    }
}