import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLife extends JFrame {
    private static final int MAP_SIZE = 30;
    private static final int CELL_SIZE = 20;
    private static final int TIMER_INTERVAL = 100;

    private int[][] currentState = new int[MAP_SIZE][MAP_SIZE];
    private int[][] nextState = new int[MAP_SIZE][MAP_SIZE];
    private JButton[][] cells = new JButton[MAP_SIZE][MAP_SIZE];
    private boolean isPlaying = false;
    private Timer mainTimer;

    public GameLife() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        initializeGame();
        setFormSize();
        buildMenu();
        getContentPane().setBackground(Color.BLACK);
    }

    private void setFormSize() {
        setSize((MAP_SIZE + 1) * CELL_SIZE, (MAP_SIZE + 1) * CELL_SIZE + 42);
    }

    private void initializeGame() {
        isPlaying = false;
        initializeTimer();
        currentState = initializeMap();
        nextState = initializeMap();
        initializeCells();
    }

    private void initializeTimer() {
        mainTimer = new Timer(TIMER_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStates();
            }
        });
    }

    private void clearGame() {
        isPlaying = false;
        initializeTimer();
        currentState = initializeMap();
        nextState = initializeMap();
        resetCells();
    }

    private void resetCells() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                cells[i][j].setBackground(Color.WHITE);
            }
        }
    }

    private void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        JMenuItem restartMenuItem = new JMenuItem("Начать заного");
        JMenuItem playMenuItem = new JMenuItem("Начать симуляцию");

        restartMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainTimer.stop();
                clearGame();
            }
        });

        playMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    isPlaying = true;
                    mainTimer.start();
                }
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
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (currentState[i][j] == 1)
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
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                button.setBackground(Color.WHITE);
                button.setBounds(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cellClicked(e);
                    }
                });
                add(button);
                cells[i][j] = button;
            }
        }
    }

    private void cellClicked(ActionEvent e) {
        if (!isPlaying) {
            JButton button = (JButton) e.getSource();
            int x = button.getY() / CELL_SIZE;
            int y = button.getX() / CELL_SIZE;

            currentState[x][y] ^= 1;
            button.setBackground(currentState[x][y] == 1 ? Color.BLACK : Color.WHITE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GameLife frame = new GameLife();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
