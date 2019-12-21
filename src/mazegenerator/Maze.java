package mazegenerator;

import pathfinding.internal.Node;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("All")
public class Maze {

    private final Node[][] data;

    public Maze(int width, int height, boolean blocked) {
        this.data = new Node[width][height];
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                data[x][y] = new Node(x, y);
                data[x][y].blocked = blocked;
            }
        }
    }

    public void resetNodeData() {
        Arrays.stream(data).flatMap(Arrays::stream).forEach(n -> n.parent = null);
    }

    public Node getRandomPathNode() {
        List<Node> nodes = Arrays.stream(data).flatMap(Arrays::stream).filter(n -> n.blocked).collect(Collectors.toList());
        return nodes.get((int) (Math.random() * nodes.size()));
    }

    public Node getNode(int x, int y) {
        return x >= 0 && y >= 0 && x < data.length && y < data[0].length ? data[x][y] : null;
    }

    public Node[][] getData() {
        return data;
    }

    public int getWidth() {
        return data.length;
    }

    public int getHeight() {
        return data[0].length;
    }
}