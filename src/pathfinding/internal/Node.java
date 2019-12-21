package pathfinding.internal;

@SuppressWarnings("All")
public class Node {

    public final int x, y;
    public double g = 0, h = 0, f = 0;
    public boolean blocked = false;
    public Node parent = null;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void calcVals(Node start, Node end) {
        g = dist(this, start);
        h = dist(this, end);
        f = g + h;
    }

    public static double dist(Node n1, Node n2) {
        return Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Node)) return false;
        return x == ((Node) obj).x && y == ((Node) obj).y;
    }

    @Override
    public String toString() {
        return "Node[x="+x+",y="+y+"]";
    }
}