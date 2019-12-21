package pathfinding;

import mazegenerator.Maze;
import pathfinding.internal.Node;
import pathfinding.internal.PathFinding;

import java.util.*;

public class AStar implements PathFinding {

    private final Node start;
    private final Node end;
    private final Maze maze;

    private List<Node> openSet = new LinkedList<>();
    private List<Node> closedSet = new LinkedList<>();
    private Node currentNode;

    public AStar(Maze maze, Node start, Node end) {
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

        Node best = openSet.get(0);
        for(Node node : openSet) {
            if(node.f < best.f)
                best = node;

            if(node.f == best.f) {
                if(node.g > best.g)
                    best = node;
            }
        }

        currentNode = best;
        if(foundPath())
            return true;

        openSet.remove(currentNode);
        closedSet.add(currentNode);

        for(Node neighbor : neighbors(maze, currentNode)) {
            if(neighbor == null || closedSet.contains(neighbor))
                continue;

            double tempG = currentNode.g + Node.dist(currentNode, neighbor);
            if(!openSet.contains(neighbor))
                openSet.add(neighbor);
            else if(tempG >= neighbor.g)
                continue;

            neighbor.calcVals(start, end);
            neighbor.parent = currentNode;
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