import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TicTacToeGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel gamePanel;
    private JPanel modePanel;
    private JButton[][] buttons = new JButton[3][3];
    private char currentPlayer = 'X';
    private boolean gameEnded = false;
    private boolean vsComputer = false;
    private Random random = new Random();
    private Timer animationTimer;
    private float animationProgress = 0f;
    private int[] lastMove = {-1, -1};

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TicTacToeGUI().initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initialize() {
        // Create the main frame
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 550);
        frame.setMinimumSize(new Dimension(400, 450));

        // Main panel with card layout for switching between modes and game
        mainPanel = new JPanel(new CardLayout());

        // Create mode selection panel
        createModePanel();

        // Create game panel
        createGamePanel();

        // Add panels to main panel
        mainPanel.add(modePanel, "mode");
        mainPanel.add(gamePanel, "game");

        frame.add(mainPanel);
        centerFrame();
        frame.setVisible(true);
    }

    private void centerFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
    }

    private void createModePanel() {
        modePanel = new JPanel(new BorderLayout());
        modePanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("Tic Tac Toe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 50, 100));
        buttonPanel.setOpaque(false);

        JButton pvpButton = createStyledButton("Player vs Player");
        JButton pvcButton = createStyledButton("Player vs Computer");

        pvpButton.addActionListener(e -> {
            vsComputer = false;
            startGame();
        });

        pvcButton.addActionListener(e -> {
            vsComputer = true;
            startGame();
        });

        buttonPanel.add(pvpButton);
        buttonPanel.add(pvcButton);

        modePanel.add(titleLabel, BorderLayout.NORTH);
        modePanel.add(buttonPanel, BorderLayout.CENTER);
        // Add exit button at bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        bottomPanel.add(exitButton);

        modePanel.add(bottomPanel, BorderLayout.SOUTH);

    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(240, 240, 240));

        // Header panel with title and reset button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel gameTitle = new JLabel("Tic Tac Toe", SwingConstants.CENTER);
        gameTitle.setFont(new Font("Arial", Font.BOLD, 24));
        gameTitle.setForeground(Color.WHITE);

        JButton backButton = new JButton("Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(new Color(240, 240, 240));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> showModeSelection());

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(gameTitle, BorderLayout.CENTER);

        // Game board panel
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boardPanel.setBackground(new Color(70, 130, 180));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
                buttons[i][j].setCursor(new Cursor(Cursor.HAND_CURSOR));

                int finalI = i;
                int finalJ = j;
                buttons[i][j].addActionListener(e -> handleButtonClick(finalI, finalJ));
                boardPanel.add(buttons[i][j]);
            }
        }

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusPanel.setBackground(new Color(240, 240, 240));

        gamePanel.add(headerPanel, BorderLayout.NORTH);
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(statusPanel, BorderLayout.SOUTH);
    }

    private void startGame() {
        resetGame();
        CardLayout cl = (CardLayout)(mainPanel.getLayout());
        cl.show(mainPanel, "game");

        // If playing against computer and computer goes first
        if (vsComputer && currentPlayer == 'O') {
            SwingUtilities.invokeLater(() -> {
                Timer timer = new Timer(500, e -> {
                    makeAIMove();
                    ((Timer)e.getSource()).stop();
                });
                timer.setRepeats(false);
                timer.start();
            });
        }
    }

    private void showModeSelection() {
        CardLayout cl = (CardLayout)(mainPanel.getLayout());
        cl.show(mainPanel, "mode");
    }

    private void handleButtonClick(int row, int col) {
        if (gameEnded || !buttons[row][col].getText().isEmpty()) {
            return;
        }

        makeMove(row, col);
    }

    private void makeMove(int row, int col) {
        lastMove[0] = row;
        lastMove[1] = col;

        // Start animation
        animationProgress = 0f;
        buttons[row][col].setText(String.valueOf(currentPlayer));
        buttons[row][col].setForeground(new Color(70, 130, 180, 0));
        buttons[row][col].setEnabled(false);

        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += 0.05f;
                if (animationProgress >= 1f) {
                    animationProgress = 1f;
                    animationTimer.stop();
                    checkGameState();
                    if (!gameEnded && vsComputer && currentPlayer == 'O') {
                        Timer timer = new Timer(500, evt -> makeAIMove());
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
                int alpha = (int)(animationProgress * 255);
                buttons[row][col].setForeground(new Color(70, 130, 180, alpha));
            }
        });
        animationTimer.start();
    }

    private void checkGameState() {
        if (checkWin()) {
            gameEnded = true;
            highlightWinningCells();
            showGameResult("Player " + currentPlayer + " wins!");
        } else if (isBoardFull()) {
            gameEnded = true;
            showGameResult("It's a draw!");
        } else {
            switchPlayer();
        }
    }

    private void showGameResult(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, message);
        });
    }

    private void makeAIMove() {
        if (gameEnded) return;

        // Simple AI: first try to win, then block opponent, then random move
        int[] move = findWinningMove('O'); // Try to win
        if (move == null) {
            move = findWinningMove('X'); // Try to block
        }
        if (move == null) {
            // No immediate win or block, make random move
            do {
                move = new int[]{random.nextInt(3), random.nextInt(3)};
            } while (!buttons[move[0]][move[1]].getText().isEmpty());
        }

        makeMove(move[0], move[1]);
    }

    private int[] findWinningMove(char player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(String.valueOf(player))
                    && buttons[i][1].getText().equals(String.valueOf(player))
                    && buttons[i][2].getText().isEmpty()) {
                return new int[]{i, 2};
            }
            if (buttons[i][0].getText().equals(String.valueOf(player))
                    && buttons[i][2].getText().equals(String.valueOf(player))
                    && buttons[i][1].getText().isEmpty()) {
                return new int[]{i, 1};
            }
            if (buttons[i][1].getText().equals(String.valueOf(player))
                    && buttons[i][2].getText().equals(String.valueOf(player))
                    && buttons[i][0].getText().isEmpty()) {
                return new int[]{i, 0};
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (buttons[0][j].getText().equals(String.valueOf(player))
                    && buttons[1][j].getText().equals(String.valueOf(player))
                    && buttons[2][j].getText().isEmpty()) {
                return new int[]{2, j};
            }
            if (buttons[0][j].getText().equals(String.valueOf(player))
                    && buttons[2][j].getText().equals(String.valueOf(player))
                    && buttons[1][j].getText().isEmpty()) {
                return new int[]{1, j};
            }
            if (buttons[1][j].getText().equals(String.valueOf(player))
                    && buttons[2][j].getText().equals(String.valueOf(player))
                    && buttons[0][j].getText().isEmpty()) {
                return new int[]{0, j};
            }
        }

        // Check diagonals
        if (buttons[0][0].getText().equals(String.valueOf(player))
                && buttons[1][1].getText().equals(String.valueOf(player))
                && buttons[2][2].getText().isEmpty()) {
            return new int[]{2, 2};
        }
        if (buttons[0][0].getText().equals(String.valueOf(player))
                && buttons[2][2].getText().equals(String.valueOf(player))
                && buttons[1][1].getText().isEmpty()) {
            return new int[]{1, 1};
        }
        if (buttons[1][1].getText().equals(String.valueOf(player))
                && buttons[2][2].getText().equals(String.valueOf(player))
                && buttons[0][0].getText().isEmpty()) {
            return new int[]{0, 0};
        }

        if (buttons[0][2].getText().equals(String.valueOf(player))
                && buttons[1][1].getText().equals(String.valueOf(player))
                && buttons[2][0].getText().isEmpty()) {
            return new int[]{2, 0};
        }
        if (buttons[0][2].getText().equals(String.valueOf(player))
                && buttons[2][0].getText().equals(String.valueOf(player))
                && buttons[1][1].getText().isEmpty()) {
            return new int[]{1, 1};
        }
        if (buttons[1][1].getText().equals(String.valueOf(player))
                && buttons[2][0].getText().equals(String.valueOf(player))
                && buttons[0][2].getText().isEmpty()) {
            return new int[]{0, 2};
        }

        return null;
    }

    private boolean checkWin() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty()
                    && buttons[i][0].getText().equals(buttons[i][1].getText())
                    && buttons[i][1].getText().equals(buttons[i][2].getText())) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (!buttons[0][j].getText().isEmpty()
                    && buttons[0][j].getText().equals(buttons[1][j].getText())
                    && buttons[1][j].getText().equals(buttons[2][j].getText())) {
                return true;
            }
        }

        // Check diagonals
        if (!buttons[0][0].getText().isEmpty()
                && buttons[0][0].getText().equals(buttons[1][1].getText())
                && buttons[1][1].getText().equals(buttons[2][2].getText())) {
            return true;
        }

        if (!buttons[0][2].getText().isEmpty()
                && buttons[0][2].getText().equals(buttons[1][1].getText())
                && buttons[1][1].getText().equals(buttons[2][0].getText())) {
            return true;
        }

        return false;
    }

    private void highlightWinningCells() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty()
                    && buttons[i][0].getText().equals(buttons[i][1].getText())
                    && buttons[i][1].getText().equals(buttons[i][2].getText())) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setBackground(new Color(144, 238, 144));
                }
                return;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (!buttons[0][j].getText().isEmpty()
                    && buttons[0][j].getText().equals(buttons[1][j].getText())
                    && buttons[1][j].getText().equals(buttons[2][j].getText())) {
                for (int i = 0; i < 3; i++) {
                    buttons[i][j].setBackground(new Color(144, 238, 144));
                }
                return;
            }
        }

        // Check diagonals
        if (!buttons[0][0].getText().isEmpty()
                && buttons[0][0].getText().equals(buttons[1][1].getText())
                && buttons[1][1].getText().equals(buttons[2][2].getText())) {
            buttons[0][0].setBackground(new Color(144, 238, 144));
            buttons[1][1].setBackground(new Color(144, 238, 144));
            buttons[2][2].setBackground(new Color(144, 238, 144));
            return;
        }

        if (!buttons[0][2].getText().isEmpty()
                && buttons[0][2].getText().equals(buttons[1][1].getText())
                && buttons[1][1].getText().equals(buttons[2][0].getText())) {
            buttons[0][2].setBackground(new Color(144, 238, 144));
            buttons[1][1].setBackground(new Color(144, 238, 144));
            buttons[2][0].setBackground(new Color(144, 238, 144));
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private void resetGame() {
        currentPlayer = 'X';
        gameEnded = false;

        if (animationTimer != null) {
            animationTimer.stop();
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
    }
}