import mazegenerator.Maze;
import mazegenerator.MazeGenerator;
import pathfinding.AStar;
import pathfinding.BFS;
import pathfinding.DFS;
import pathfinding.internal.Node;
import pathfinding.internal.PathFinding;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
public class App extends JPanel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PathFinding");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            frame.add(new App());

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- //

    private App() {
        Visualizer visualizer = new Visualizer();
        Options options = new Options(visualizer);
        visualizer.setOptions(options);

        setLayout(new BorderLayout());
        add(visualizer, BorderLayout.CENTER);
        add(options, BorderLayout.SOUTH);
    }

    private static class Visualizer extends JPanel implements ActionListener {

        private static final int START = 0;
        private static final int END = 1;
        private static final int WALL = 2;
        private static final int PATH = 3;

        private static final int EMPTY = 0;
        private static final int RANDOM = 1;
        private static final int MAZE = 2;

        private static final int BFS = 0;
        private static final int DFS = 1;
        private static final int ASTAR = 2;

        // ---- ---- ---- ---- ---- ---- ---- //

        private Maze maze;
        private Node start;
        private Node end;

        private int scale = 20;
        private boolean mousePressed = true;
        private int blockType = WALL;
        private int algorithm = BFS;
        private int mazeType = EMPTY;

        private Dimension mazeSize;
        private PathFinding pathFinder = null;
        private List<Node> path = new ArrayList<>();
        private Timer timer = new Timer(10, this);

        private Options options;

        private Visualizer() {
            setBackground(Color.darkGray.brighter().brighter());
            setFocusable(true);

            // Mouse press and release events
            MouseAdapter mouseInput = mouseInput();
            addMouseMotionListener(mouseInput);
            addMouseListener(mouseInput);

            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_1 : blockType = START; break;
                        case KeyEvent.VK_2 : blockType = END; break;
                        case KeyEvent.VK_3 : blockType = WALL; break;
                        case KeyEvent.VK_4 : blockType = PATH; break;
                    }
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(pathFinder.foundPath()) {
                if(path.isEmpty()) {
                    path.add(pathFinder.currentNode());
                }else{
                    path.add(path.get(path.size()-1).parent);
                    if(path.get(path.size()-1).equals(start)) {
                        timer.stop();
                        options.pathFinished();
                    }
                }
            }else if(pathFinder.calcStep() && !pathFinder.foundPath()) {
                    timer.stop();
                    options.pathFinished();
                }

            repaint();
        }

        @Override
        protected void paintComponent(Graphics _g) {
            super.paintComponent(_g);
            Graphics2D g = (Graphics2D) _g;

            if(maze == null) { // temp
                setScale(scale);
                generateMaze(EMPTY);
            }

            double unit = getWidth() * 1.0 / (maze.getWidth());

            if(pathFinder != null) {

                g.setColor(Color.green);
                for(Node n : pathFinder.openSet()) {
                    g.fillRect((int)(n.x * unit), (int)(n.y * unit), (int)unit+1, (int)unit+1);
                }

                g.setColor(Color.red);
                for(Node n : pathFinder.closedSet()) {
                    g.fillRect((int)(n.x * unit),(int) (n.y * unit), (int)unit+1, (int)unit+1);
                }

                g.setColor(Color.blue);
                g.fillRect((int)(pathFinder.currentNode().x * unit), (int)(pathFinder.currentNode().y * unit), (int)unit+1, (int)unit+1);
                for(Node n : path) {
                    g.fillRect((int)(n.x * unit), (int)(n.y * unit),(int) unit+1,(int) unit+1);
                }
            }

            g.setColor(Color.black);
            for(Node[] row : maze.getData()) {
                for(Node n : row) {
                    if(n.blocked) {
                        g.fillRect((int)(n.x * unit), (int)(n.y * unit), (int)unit+1, (int)unit+1);
                    }
                }
            }

            if(start != null) {
                g.setColor(Color.white);
                g.fillRect((int)(start.x * unit), (int)(start.y * unit), (int)unit, (int)unit+1);
            }

            if(end != null) {
                g.setColor(Color.white);
                g.fillRect((int)(end.x * unit), (int)(end.y * unit), (int)unit, (int)unit+1);
            }

            g.setColor(Color.black);
            Node n = maze.getNode(0, maze.getHeight()-1);
            g.fillRect((int)(n.x*unit), (int)(n.y*unit), getWidth(), (int)(unit*5));
        }

        private MouseAdapter mouseInput() {
            return new MouseAdapter() {
                public void mousePressed(MouseEvent e) { action(e); }
                public void mouseDragged(MouseEvent e) { action(e); }

                private void action(MouseEvent e) {
                    if(timer.isRunning())
                        return;

                    int x = (int)(e.getX()*1.0/getWidth()*maze.getWidth());
                    int y = (int)(e.getY()*1.0/getHeight()*maze.getHeight());
                    Node n = maze.getNode(x, y);

                    if(n == null || x == 0 || x == maze.getWidth()-1 || y == 0 || y == maze.getHeight()-1)
                        return;

                    switch(blockType) {
                        case START : start = n; break;
                        case END : end = n; break;
                        case WALL : n.blocked = true; break;
                        case PATH : n.blocked = false; break;
                    }

                    stopVisuals();
                    requestFocus();
                }
            };
        }

        private void generateMaze(int mazeType) {
            if(!timer.isRunning()) {
                switch(mazeType) {
                    case EMPTY: maze = MazeGenerator.emptyMaze(mazeSize.width, mazeSize.height); break;
                    case RANDOM: maze = MazeGenerator.randomMaze(mazeSize.width, mazeSize.height, 0.35); break;
                    case MAZE: maze = MazeGenerator.generateMaze(mazeSize.width, mazeSize.height); break;
                }

                this.mazeType = mazeType;
                stopVisuals();
            }
        }

        private void setPathFinder(int pathType) {
            this.algorithm = pathType;
        }

        public void setOptions(Options options) {
            this.options = options;
        }

        private boolean startPathFinding() {
            if(timer.isRunning() || start == null || end == null)
                return false;

            path.clear();
            maze.resetNodeData();
            switch(algorithm) {
                case BFS : pathFinder = new BFS(maze, start, end); break;
                case DFS : pathFinder = new DFS(maze, start, end); break;
                case ASTAR : pathFinder = new AStar(maze, start, end); break;
            }

            timer.start();
            return true;
        }

        private void stopVisuals() {
            timer.stop();
            pathFinder = null;
            repaint();
        }

        private void setScale(int scale) {
            int w = getWidth()/scale;
            int h = getHeight()/scale;
            this.mazeSize = new Dimension(w % 2 == 1 ? w : w+1, h % 2 == 1 ? h : h+1);
            this.scale = scale;
            this.start = null;
            this.end = null;
        }

        private void setBlockType(int blockType) {
            this.blockType = blockType;
        }

        private void setFrameDelay(int delay) {
            timer.setDelay(delay);
        }

        public int getAlgorithm() {
            return algorithm;
        }
    }

    private static class Options extends JPanel {

        private static final Font labelFont = new Font("Helvetica", Font.BOLD, 20);
        private static final Font buttonFont = new Font("Helvetica", Font.BOLD, 12);
        private static final GridBagConstraints gbc = new GridBagConstraints();
        static { gbc.ipadx = 10; }

        private JButton solveButton;

        private Options(Visualizer visualizer) {
            // Width doesnt matter because it will fill
            setPreferredSize(new Dimension(0, 45));
            setLayout(new GridBagLayout());
            setBackground(Color.black);

            addLabel("Controls:");
            solveButton = addButton("SOLVE", 60, b -> {
                if(visualizer.timer.isRunning()) {
                    visualizer.stopVisuals();
                    solveButton.setText("START");
                }else {
                    if(visualizer.startPathFinding())
                        solveButton.setText("STOP");
                }
            });

            addLabel("Maze Type:");
            addButton("MAZE", 60, b -> visualizer.generateMaze(Visualizer.MAZE));
            addButton("RANDOM", 80, b -> visualizer.generateMaze(Visualizer.RANDOM));
            addButton("EMPTY", 60, b -> visualizer.generateMaze(Visualizer.EMPTY));

            addLabel("PathFinder:");
            addButton("BFS", 60, b -> {
                if(!visualizer.timer.isRunning()) {
                    int alg = visualizer.algorithm == Visualizer.BFS ? Visualizer.DFS : visualizer.algorithm == Visualizer.DFS ? Visualizer.ASTAR : Visualizer.BFS;
                    b.setText(alg == Visualizer.BFS ? "BFS" : alg == Visualizer.DFS ? "DFS" : "ASTAR");
                    visualizer.setPathFinder(alg);
                }
            });

            addLabel("Block Type:");
            addButton("START", 60, b -> visualizer.setBlockType(Visualizer.START));
            addButton("END", 60, b -> visualizer.setBlockType(Visualizer.START));
            addButton("WALL", 60, b -> visualizer.setBlockType(Visualizer.START));
            addButton("PATH", 60, b -> visualizer.setBlockType(Visualizer.START));

            addLabel("Scale:");
            addButton("+", 20, b -> {
                if(visualizer.scale < 20 && !visualizer.timer.isRunning()) {
                    visualizer.setScale(visualizer.scale + 5);
                    visualizer.generateMaze(visualizer.mazeType);
                }
            });
            addButton("-", 20, b -> {
                if(visualizer.scale > 5 && !visualizer.timer.isRunning()) {
                    visualizer.setScale(visualizer.scale - 5);
                    visualizer.generateMaze(visualizer.mazeType);
                }
            });
        }

        private JButton addButton(String name, int width, JButtonEvent e) {
            JButton button = new JButton(name);
            button.setPreferredSize(new Dimension(width, 20));
            button.addActionListener(_e -> e.action(button));
            button.setFont(buttonFont);

            add(button, gbc);
            return button;
        }

        private JLabel addLabel(String name) {
            JLabel label = new JLabel(" "+name);
            label.setForeground(Color.white.darker());
            label.setFont(labelFont);

            add(label, gbc);
            return label;
        }

        private void pathFinished() {
            solveButton.setText("SOLVE");
        }
    }

    private interface JButtonEvent {
        void action(JButton b);
    }
}