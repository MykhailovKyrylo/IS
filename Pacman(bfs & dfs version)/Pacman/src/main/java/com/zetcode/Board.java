package com.zetcode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.zip.DeflaterInputStream;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    public class Pair<A, B> {
        private A first;
        private B second;

        public Pair(A first, B second) {
            super();
            this.first = first;
            this.second = second;
        }

        public int hashCode() {
            int hashFirst = first != null ? first.hashCode() : 0;
            int hashSecond = second != null ? second.hashCode() : 0;

            return (hashFirst + hashSecond) * hashSecond + hashFirst;
        }

        public boolean equals(Object other) {
            if (other instanceof Pair) {
                Pair otherPair = (Pair) other;
                return
                        ((  this.first == otherPair.first ||
                                ( this.first != null && otherPair.first != null &&
                                        this.first.equals(otherPair.first))) &&
                                (  this.second == otherPair.second ||
                                        ( this.second != null && otherPair.second != null &&
                                                this.second.equals(otherPair.second))) );
            }

            return false;
        }

        public String toString()
        {
            return "(" + first + ", " + second + ")";
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }
    }

    private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private Image ii;
    private final Color dotColor = new Color(210, 204, 41);
    private final Color destinyColor = new Color(192, 26, 41);
    private Color mazeColor;

    private boolean inGame = false;
    private int flag;
//    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int PACMAN_SPEED = 6;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int score;
    private int[] dx, dy;

    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;

    private enum Direction {

        LEFT(-1, 0),
        RIGHT(1, 0),
        UP(0, -1),
        DOWN(0, 1),
        DEFAULT(0, 0);

        private final Integer dx;
        private final Integer dy;

        Direction(Integer dx_, Integer dy_) {
            this.dx = dx_;
            this.dy = dy_;
        }

        public Integer getDx() {
            return dx;
        }
        public Integer getDy() {
            return dy;
        }
    }

    private final List<Direction> AllDirections = Arrays.asList(
            Direction.LEFT,
            Direction.RIGHT,
            Direction.UP,
            Direction.DOWN
    );

    private HashMap<Pair<Integer, Integer>, Direction> dx_dy = new HashMap<>();

    private final short levelData[] = {
            19,  26,  26,  26,  18,  18,  18,  18,  18,  18,  18,  18,  18,  18,  22,
            21,  0,   0,   0,   17,  16,  16,  16,  16,  16,  16,  16,  16,  16,  20,
            21,  0,   0,   0,   17,  16,  16,  16,  16,  16,  16,  16,  16,  16,  20,
            21,  0,   0,   0,   17,  16,  16,  24,  16,  16,  16,  16,  16,  16,  20,
            17,  18,  18,  18,  16,  16,  20,  0,   17,  16,  16,  16,  16,  16,  20,
            17,  16,  16,  16,  16,  16,  20,  0,   17,  16,  16,  16,  16,  24,  20,
            25,  16,  16,  16,  24,  24,  28,  0,   25,  24,  24,  16,  20,  0,   21,
            1,   17,  16,  20,  0,   0,   0,   0,   0,   0,   0,   17,  20,  0,   21,
            1,   17,  16,  16,  18,  18,  22,  0,   19,  18,  18,  16,  20,  0,   21,
            1,   17,  16,  16,  16,  16,  20,  0,   17,  16,  16,  16,  20,  0,   21,
            1,   17,  16,  16,  16,  16,  20,  0,   17,  16,  16,  16,  20,  0,   21,
            1,   17,  16,  16,  16,  16,  16,  18,  16,  16,  16,  16,  20,  0,   21,
            1,   17,  16,  16,  16,  16,  16,  16,  16,  16,  16,  16,  20,  0,   21,
            1,   25,  24,  24,  24,  24,  24,  24,  24,  24,  16,  16,  16,  18,  20,
            9,   8,   8,   8,   8,   8,   8,   8,   8,   8,   25,  24,  24,  24,  28
    };

    private short[] screenData;
    private Timer timer;

    public Board() {

        loadImages();
        initVariables();
        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }

    private void doAnim() {

        pacAnimCount--;

        if (pacAnimCount <= 0) {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {
        movePacman();
        drawPacman(g2d);
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(210, 204, 41));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String s = "Press to start : \n b - bfs, \n d - dsf";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

    }

    private void movePacman() {

        int pos;
        short ch;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
                if ((ch & 32) != 0) {
                    initGame();
                }
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            Pair<Integer, Integer> current_position = new Pair<>(pacman_x / BLOCK_SIZE, pacman_y / BLOCK_SIZE);

            pacmand_x = dx_dy.get(current_position).dx;
            pacmand_y = dx_dy.get(current_position).dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0) // ch & 1 - lefy wall
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0) // ch & 4 - right wall
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0) // ch & 2 - up wall
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) { // ch & 8 - down wall
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {

        if (view_dx == -1) {
            drawPacnanLeft(g2d);
        } else if (view_dx == 1) {
            drawPacmanRight(g2d);
        } else if (view_dy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }

    private void drawPacmanUp(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                if ((screenData[i] & 32) != 0) {
                    g2d.setColor(destinyColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {
        score = 0;
        initLevel();
    }

    private int convertToRawIndex(Pair<Integer, Integer> position) {
        final int x = position.getFirst();
        final int y = position.getSecond();
        final int pos = x + N_BLOCKS * y;
        return pos;
    }

    private boolean isAvailableCell(Pair<Integer, Integer> position) {
        final short ch = screenData[convertToRawIndex(position)];
        return ((ch & 16) != 0);
    }

    private Boolean isValidCell(Pair<Integer, Integer> position) {
        return (position.getFirst() >= 0 && position.getFirst() < N_BLOCKS &&
                position.getSecond() >= 0 && position.getSecond() < N_BLOCKS);
    }

    private List<Pair<Pair<Integer, Integer>, Direction>> getNeightboursCells(Pair<Integer, Integer> current_position) {
        List<Pair<Pair<Integer, Integer>, Direction>> result = new ArrayList<>();

        for (Direction direction : AllDirections) {
            Pair<Integer, Integer> position = new Pair<>(current_position.getFirst() + direction.dx,
                                                         current_position.getSecond() + direction.dy);
            if (isValidCell(position)) {
                result.add(new Pair<>(position, direction));
            }
        }

        return result;
    }

    private Direction getOppositeDirection(Direction direction) {
        Direction opposite_direction;

        switch (direction) {
            case LEFT:
                opposite_direction = Direction.RIGHT;
                break;
            case RIGHT:
                opposite_direction = Direction.LEFT;
                break;
            case UP:
                opposite_direction = Direction.DOWN;
                break;
            case DOWN:
                opposite_direction = Direction.UP;
                break;
            default:
                opposite_direction = Direction.DEFAULT;
        }

        return opposite_direction;
    }

    private void runPacmanBFS(Pair<Integer, Integer> final_position) {

        screenData[convertToRawIndex(final_position)] = (short) (screenData[convertToRawIndex(final_position)] | 32);

        dx_dy.clear();
        dx_dy.put(final_position, Direction.DEFAULT);

        HashMap<Pair<Integer, Integer>, Pair<Integer, Pair<Integer, Integer>>> distance = new HashMap<>();
        final Pair<Integer, Integer> BASE = new Pair<>(-1, -1);
        distance.put(final_position, new Pair<>(0, BASE));

        Queue<Pair<Integer, Integer>> bfs = new LinkedList<>();
        bfs.add(final_position); // adding starting point

        while (!bfs.isEmpty()) {
            Pair<Integer, Integer> current_position = bfs.remove();
            int min_distance = distance.get(current_position).getFirst();

            List<Pair<Pair<Integer, Integer>, Direction>> neightbours = getNeightboursCells(current_position);

            for (Pair<Pair<Integer, Integer>, Direction> neightbour : neightbours) {
                if (isAvailableCell(neightbour.getFirst())) {
                    if (distance.containsKey(neightbour.getFirst())) {
                        int current_min_distance = distance.get(neightbour.getFirst()).getFirst();
                        if (current_min_distance > min_distance + 1) {
                            distance.put(neightbour.getFirst(), new Pair<>(min_distance + 1, current_position));
                            bfs.add(neightbour.getFirst());
                            dx_dy.put(neightbour.getFirst(), getOppositeDirection(neightbour.getSecond()));
                        }
                    } else {
                        distance.put(neightbour.getFirst(), new Pair<>(min_distance + 1, current_position));
                        bfs.add(neightbour.getFirst());
                        dx_dy.put(neightbour.getFirst(), getOppositeDirection(neightbour.getSecond()));
                    }
                }
            }
        }
    }

    private void runPacmanDFS(Pair<Integer, Integer> final_position) {
        screenData[convertToRawIndex(final_position)] = (short) (screenData[convertToRawIndex(final_position)] | 32);

        dx_dy.clear();
        dx_dy.put(final_position, Direction.DEFAULT);

        HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> distance = new HashMap<>();
        final Pair<Integer, Integer> BASE = new Pair<>(-1, -1);
        distance.put(final_position, BASE);

        Stack<Pair<Integer, Integer>> bfs = new Stack<>();
        bfs.add(final_position); // adding starting point

        while (!bfs.isEmpty()) {
            Pair<Integer, Integer> current_position = bfs.pop();

            List<Pair<Pair<Integer, Integer>, Direction>> neightbours = getNeightboursCells(current_position);

            for (Pair<Pair<Integer, Integer>, Direction> neightbour : neightbours) {
                if (isAvailableCell(neightbour.getFirst())) {
                    if (!distance.containsKey(neightbour.getFirst())) {
                        distance.put(neightbour.getFirst(), current_position);
                        bfs.add(neightbour.getFirst());
                        dx_dy.put(neightbour.getFirst(), getOppositeDirection(neightbour.getSecond()));
                    }
                }
            }
        }
    }

    private Pair<Integer, Integer> getRandomDestinyPosition() {
        int x = (int) (Math.random() * N_BLOCKS);
        int y = (int) (Math.random() * N_BLOCKS);

        while ( (screenData[convertToRawIndex(new Pair<>(x, y))] & 16) == 0 || (x == 0 && y == 0)) {
            x = (int) (Math.random() * N_BLOCKS);
            y = (int) (Math.random() * N_BLOCKS);
        }

        return new Pair<>(x, y);
    }

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        switch (flag) {
            case 1:
                runPacmanBFS(getRandomDestinyPosition());
                break;
            case 2:
                runPacmanDFS(getRandomDestinyPosition());
                break;
            default:
                runPacmanBFS(getRandomDestinyPosition());
        }
    }

    private void loadImages() {
        pacman1 = new ImageIcon(this.getClass().getResource("/images/pacman.png")).getImage();
        pacman2up = new ImageIcon(this.getClass().getResource("/images/up1.png")).getImage();
        pacman3up = new ImageIcon(this.getClass().getResource("/images/up2.png")).getImage();
        pacman4up = new ImageIcon(this.getClass().getResource("/images/up3.png")).getImage();
        pacman2down = new ImageIcon(this.getClass().getResource("/images/down1.png")).getImage();
        pacman3down = new ImageIcon(this.getClass().getResource("/images/down2.png")).getImage();
        pacman4down = new ImageIcon(this.getClass().getResource("/images/down3.png")).getImage();
        pacman2left = new ImageIcon(this.getClass().getResource("/images/left1.png")).getImage();
        pacman3left = new ImageIcon(this.getClass().getResource("/images/left2.png")).getImage();
        pacman4left = new ImageIcon(this.getClass().getResource("/images/left3.png")).getImage();
        pacman2right = new ImageIcon(this.getClass().getResource("/images/right1.png")).getImage();
        pacman3right = new ImageIcon(this.getClass().getResource("/images/right2.png")).getImage();
        pacman4right = new ImageIcon(this.getClass().getResource("/images/right3.png")).getImage();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (!inGame) {
                if (key == 'b' || key == 'B') {
                    flag = 1;
                    inGame = true;
                    initGame();
                }
                if (key == 'd' || key == 'D') {
                    flag = 2;
                    inGame = true;
                    initGame();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
}
