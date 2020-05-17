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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import static java.lang.Math.*;

class Position {
    Position() {
        this.x = -1;
        this.y = -1;
    }

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Position other) {
        if (other == this) return true;

        return this.x == other.x && this.y == other.y;
    }

    public Integer x;
    public Integer y;
}

public class Board extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private Image ii;
    private final Color dotColor = new Color(192, 192, 0);
    private Color mazeColor;

    private boolean inGame = false;
    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int INF = 100000000;
    private final int NODES_COUNT = N_BLOCKS * N_BLOCKS;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 6;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int N_GHOSTS = 6;
    private int pacsLeft, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image ghost;
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

    private int[][] distance = new int[NODES_COUNT][NODES_COUNT];

    private final int MAX_VISIT_SCORE = 7;
    final int MOVES_PRECALC_COUNT = 5;
    private int step_count;
    private int[] last_visit;

    private final List<Direction> AllDirections = Arrays.asList(
            Direction.LEFT,
            Direction.RIGHT,
            Direction.UP,
            Direction.DOWN
    );

    final short BLANK = 0;
    final short VISITED = 2;
    final short POINT = 1;
    final short BL = 228; // block
    private final short levelData[] = {
            1,  1,  1,  1,  1,  1,  1, 1,  1,  1,  1,  1,  1,  1,  1,
            1,  BL, BL, BL, 1,  1,  1, 1,  1,  1,  1,  1,  1,  1,  1,
            1,  BL, BL, BL, 1,  1,  1, 1,  1,  1,  1,  1,  1,  1,  1,
            1,  BL, BL, BL, 1,  1,  1, 1,  1,  1,  1,  1,  1,  1,  1,
            1,  1,  1,  1,  1,  1,  1, BL, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1, BL, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1, BL, BL, BL, BL, BL, BL, BL, 1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1, BL, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1, BL, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1, BL, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1,  1, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1,  1, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1,  1, 1,  1,  1,  1,  1, BL,  1,
            BL, 1,  1,  1,  1,  1,  1,  1, 1,  1,  1,  1,  1,  1,  1,
            BL, BL, BL, BL, BL, BL, BL, 1, 1,  1,  1,  1,  1,  1,  1
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData, mazeData;
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

    private int convertToRawIndex(Position position) {
        final int x = position.x;
        final int y = position.y;
        final int pos = x + N_BLOCKS * y;
        return pos;
    }

    private boolean isAvailableCell(Position position) {
        final short ch = screenData[convertToRawIndex(position)];
        return (ch != BL);
    }

    private Boolean isValidCell(Position position) {
        return (position.x >= 0 && position.x < N_BLOCKS &&
                position.y >= 0 && position.y < N_BLOCKS);
    }

    private void buildGraph() {
        { // mem set distance
            for (int row_from = 0; row_from < N_BLOCKS; row_from++) {
                for (int col_from = 0; col_from < N_BLOCKS; col_from++) {
                    Position from = new Position(col_from, row_from);

                    for (int row_to = 0; row_to < N_BLOCKS; row_to++) {
                        for (int col_to = 0; col_to < N_BLOCKS; col_to++) {
                            Position to = new Position(col_to, row_to);

                            final int dist = (from.equals(to)) ? 0 : INF;

                            distance[convertToRawIndex(from)][convertToRawIndex(to)] = dist;
                            distance[convertToRawIndex(to)][convertToRawIndex(from)] = dist;
                        }
                    }

                    if (!isAvailableCell(from)) continue;

                    for (Direction direction : AllDirections) {
                        Position to = new Position(from.x + direction.getDx(), from.y + direction.getDy());

                        if (isValidCell(to) && isAvailableCell(to)) {
                            distance[convertToRawIndex(from)][convertToRawIndex(to)] = 1;
                            distance[convertToRawIndex(to)][convertToRawIndex(from)] = 1;
                        }
                    }
                }
            }
        }

        {// floyd_warshall_algorithm
            for (int k = 0; k < NODES_COUNT; k++) {
                for (int i = 0; i < NODES_COUNT; i++) {
                    for (int j = 0; j < NODES_COUNT; j++) {
                        if (distance[i][k] < INF && distance[k][j] < INF) {
                            distance[i][j] = min(distance[i][j], distance[i][k] + distance[k][j]);
                        }
                    }
                }
            }
        }
    }

    private void initVariables() {

        step_count = MAX_VISIT_SCORE;
        screenData = new short[N_BLOCKS * N_BLOCKS];
        last_visit = new int[N_BLOCKS * N_BLOCKS];
        mazeData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
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

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String s = "Press s to start.";
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

        for (i = 0; i < pacsLeft; i++) {
            g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if (screenData[i] == POINT) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {

        pacsLeft--;

        if (pacsLeft == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private Direction getBestGhostDirection(Position ghost_position, Position pacman_position) {
        final int dist = distance[convertToRawIndex(ghost_position)][convertToRawIndex(pacman_position)];

        for (Direction direction : AllDirections) {
            Position neighbour = new Position(ghost_position.x + direction.getDx(), ghost_position.y + direction.getDy());

            if (!isValidCell(neighbour)) continue;

            if (distance[convertToRawIndex(neighbour)][convertToRawIndex(pacman_position)] == dist - 1) {
                return direction;
            }
        }

        return Direction.DEFAULT;
    }

    private void moveGhosts(Graphics2D g2d) {

        short i;

        for (i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                Position ghost_pos = new Position(ghost_x[i] / BLOCK_SIZE, (ghost_y[i] / BLOCK_SIZE));
                Position pacman_pos = new Position(pacman_x / BLOCK_SIZE,  (pacman_y / BLOCK_SIZE));

                Direction ghost_direction = getBestGhostDirection(ghost_pos, pacman_pos);
                ghost_dx[i] = ghost_direction.getDx();
                ghost_dy[i] = ghost_direction.getDy();
            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {

        g2d.drawImage(ghost, x, y, this);
    }

    class Move {
        Move(Direction direction, int score) {
            this.direction = direction;
            this.score = score;
        }
        Direction direction;
        int score;
    }

    private Move getBestPacmanDirection(Position position, short[] local_screen_data, int[] local_last_visit, int moves) {
        if (moves == 0) {
            return new Move(Direction.DEFAULT, 0);
        }

        int local_score = 0;

        final int position_idx = convertToRawIndex(position);
        final short position_local_screen_data = screenData[position_idx];
        final int position_local_last_visit = local_last_visit[position_idx];

        if (position_local_screen_data == POINT) {
            local_score += pow(2, moves);
        }

        final int local_step_count = step_count + (MOVES_PRECALC_COUNT - moves);
        final int last_visit_benefit = (local_step_count - position_local_last_visit);
        if (last_visit_benefit >= MOVES_PRECALC_COUNT) {
            local_score += last_visit_benefit;
        }
        local_last_visit[position_idx] = local_step_count;

        local_screen_data[position_idx] = BLANK;

        int dist_to_ghost = INF;
        for (int i = 0; i < N_GHOSTS; i++) {
            final Position ghost_position = new Position(ghost_x[i] / BLOCK_SIZE, ghost_y[i] / BLOCK_SIZE);

            dist_to_ghost = min(dist_to_ghost, distance[convertToRawIndex(position)][convertToRawIndex(ghost_position)]);
        }

        if (dist_to_ghost <= 1) {
            local_score -= 5000;
        }

        Move best_move = new Move(Direction.DEFAULT, 0);

        for (Direction direction : AllDirections) {
            Position neighbour = new Position(position.x + direction.getDx(), position.y + direction.getDy());

            if (!isValidCell(neighbour) || !isAvailableCell(neighbour)) continue;

            Move neighbour_best_move = getBestPacmanDirection(neighbour, local_screen_data, local_last_visit, moves - 1);
            if (neighbour_best_move.score >= best_move.score) {
                best_move.direction = direction;
                best_move.score = neighbour_best_move.score;
            }
        }

        local_screen_data[position_idx] = position_local_screen_data;
        local_last_visit[position_idx] = position_local_last_visit;
        best_move.score += local_score;

        return best_move;
    }

    private Direction getBestPacmanDirection(Position pacman_position) {

        return getBestPacmanDirection(pacman_position, screenData, last_visit, MOVES_PRECALC_COUNT).direction;
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
            step_count++;
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);

            if (screenData[pos] == POINT) {
                mazeData[pos] = (short) (mazeData[pos] & 15);
                score++;
            }
            last_visit[pos] = step_count;
            step_count++;
            screenData[pos] = BLANK;

            Position pacman_pos = new Position(pacman_x / BLOCK_SIZE, pacman_y / BLOCK_SIZE);

            Direction pacman_direction = getBestPacmanDirection(pacman_pos);

            pacmand_x = pacman_direction.getDx();
            pacmand_y = pacman_direction.getDy();
            view_dx = pacmand_x;
            view_dy = pacmand_y;
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

                if ((mazeData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((mazeData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((mazeData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1, x+ BLOCK_SIZE - 1, y);
                }

                if ((mazeData[i] & 8) != 0) {
                    g2d.drawLine(x+ BLOCK_SIZE - 1, y+ BLOCK_SIZE - 1, x, y+ BLOCK_SIZE - 1);
                }

                if ((mazeData[i] & 16) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {

        pacsLeft = 3;
        score = 0;
        initLevel();
        N_GHOSTS = 6;
        currentSpeed = 3;
    }

    private void fillMazeData() {
        for (int row = 0; row < N_BLOCKS; row++) {
            for (int col = 0; col < N_BLOCKS; col++) {
                final int pos_idx = convertToRawIndex(new Position(col, row));

                if (screenData[pos_idx] != POINT) continue;

                mazeData[pos_idx] |= 16;

                {// check left
                    Position position = new Position(col + Direction.LEFT.getDx(), row + Direction.LEFT.getDy());
                    if (isValidCell(position) && !isAvailableCell(position)) {
                        mazeData[pos_idx] |= 1;
                    }
                }

                {// check up
                    Position position = new Position(col + Direction.UP.getDx(), row + Direction.UP.getDy());
                    if (isValidCell(position) && !isAvailableCell(position)) {
                        mazeData[pos_idx] |= 2;
                    }
                }

                {// check right
                    Position position = new Position(col + Direction.RIGHT.getDx(), row + Direction.RIGHT.getDy());
                    if (isValidCell(position) && !isAvailableCell(position)) {
                        mazeData[pos_idx] |= 4;
                    }
                }

                {// check down
                    Position position = new Position(col + Direction.DOWN.getDx(), row + Direction.DOWN.getDy());
                    if (isValidCell(position) && !isAvailableCell(position)) {
                        mazeData[pos_idx] |= 8;
                    }
                }
            }
        }
    }

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            last_visit[i] = 0;
            screenData[i] = levelData[i];
        }

        fillMazeData();
        buildGraph();
        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        int random;

        for (i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
        dying = false;
    }

    private void loadImages() {
        ghost = new ImageIcon(this.getClass().getResource("/images/ghost.png")).getImage();
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

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } else if (key == KeyEvent.VK_PAUSE) {
                    if (timer.isRunning()) {
                        timer.stop();
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == 's' || key == 'S') {
                    inGame = true;
                    initGame();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                req_dx = 0;
                req_dy = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
}
