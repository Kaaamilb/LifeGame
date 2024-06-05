import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameLife extends JFrame {
    private static final int MAP_SIZE = 30;
    private static final int CELL_SIZE = 20;
    private static final int TIMER_INTERVAL = 100;

    private int[][] currentState;
    private int[][] nextState;
    private JButton[][] cells;
    private boolean isPlaying;
    private Timer mainTimer;

    public GameLife() {
        currentState = new int[MAP_SIZE][MAP_SIZE];
        nextState = new int[MAP_SIZE][MAP_SIZE];
        cells = new JButton[MAP_SIZE][MAP_SIZE];
        isPlaying = false;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        initializeGame();
        setFormSize();
        buildMenu();
       // getContentPane().setBackground(Color.BLACK);
    }

    private void setFormSize() {
        Dimension gameFieldSize = new Dimension(MAP_SIZE * CELL_SIZE, (MAP_SIZE + 1) * CELL_SIZE);
        getContentPane().setPreferredSize(gameFieldSize);


        pack();

        setLocationRelativeTo(null);

        setResizable(false);
    }

    private void initializeGame() {
        isPlaying = false;
        initializeTimer();
        currentState = initializeMap();
        nextState = initializeMap();
        initializeCells();
    }

    private void initializeTimer() {
        mainTimer = new Timer(TIMER_INTERVAL, e -> updateStates());
    }

    private void clearGame() {
        isPlaying = false;
        initializeTimer();
        currentState = initializeMap();
        nextState = initializeMap();
        resetCells();
    }

    private void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        JMenuItem restartMenuItem = new JMenuItem("Начать заного");
        JMenuItem playMenuItem = new JMenuItem("Начать симуляцию");

        restartMenuItem.addActionListener(e -> {
            mainTimer.stop();
            clearGame();
        });

        playMenuItem.addActionListener(e -> {
            if (!isPlaying) {
                isPlaying = true;
                mainTimer.start();
            }
        });

        menu.add(playMenuItem);
        menu.add(restartMenuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void updateStates() {
        calculateNextState();
        displayMap();
        if (isGenerationDead()) {
            mainTimer.stop();
            JOptionPane.showMessageDialog(this, ":(");
        }
    }

    private boolean isGenerationDead() {
        for (int[] row : currentState) {
            for (int cell : row) {
                if (cell == 1)
                    return false;
            }
        }
        return true;
    }

    private void calculateNextState() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                int neighborsCount = countNeighbors(i, j);
                boolean isAlive = currentState[i][j] == 1;

                nextState[i][j] = isAlive
                        ? (neighborsCount == 2 || neighborsCount == 3 ? 1 : 0)
                        : (neighborsCount == 3 ? 1 : 0);
            }
        }

        currentState = nextState;
        nextState = initializeMap();
    }

    private void displayMap() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                cells[i][j].setBackground(currentState[i][j] == 1 ? Color.BLACK : Color.WHITE);
            }
        }
    }

    private int countNeighbors(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isInsideMap(i, j) && !(i == x && j == y) && currentState[i][j] == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInsideMap(int x, int y) {
        return x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE;
    }

    private int[][] initializeMap() {
        return new int[MAP_SIZE][MAP_SIZE];
    }

    private void initializeCells() {
        boolean[][] cellChanged = new boolean[MAP_SIZE][MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                cells[i][j] = createCell(i, j, cellChanged);
            }
        }
    }

    private JButton createCell(int x, int y, boolean[][] cellChanged) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        button.setBackground(Color.WHITE);
        button.setBounds(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        addCellListeners(button, x, y, cellChanged);
        add(button);
        return button;
    }

    private void addCellListeners(JButton button, int x, int y, boolean[][] cellChanged) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    toggleCellState(x, y);
                    cellChanged[x][y] = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                cellChanged[x][y] = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && !cellChanged[x][y]) {
                    toggleCellState(x, y);
                    cellChanged[x][y] = true;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cellChanged[x][y] = false;
            }
        });

        button.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    JButton btn = (JButton) e.getSource();
                    Point point = SwingUtilities.convertPoint(btn, e.getPoint(), btn.getParent());
                    int cellX = point.y / CELL_SIZE;
                    int cellY = point.x / CELL_SIZE;
                    if (!cellChanged[cellX][cellY]) {
                        toggleCellState(cellX, cellY);
                        cellChanged[cellX][cellY] = true;
                    }
                }
            }
        });
    }

    private void toggleCellState(int x, int y) {
        if (!isPlaying) {
            currentState[x][y] ^= 1;
            cells[x][y].setBackground(currentState[x][y] == 1 ? Color.BLACK : Color.WHITE);
        }
    }

    private void resetCells() {
        for (JButton[] row : cells) {
            for (JButton cell : row) {
                cell.setBackground(Color.WHITE);
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameLife frame = new GameLife();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}