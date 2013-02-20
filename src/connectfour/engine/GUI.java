package connectfour.engine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI extends MouseAdapter
{
    /**
     * Game disc icons *
     */
    private static final String EMPTY_ICON = "img/empty.png";
    private static final String RED_ICON   = "img/red.png";
    private static final String BLACK_ICON = "img/black.png";

    private static final int        IMG_SIZE = 50;
    private              GameEngine engine   = null;
    private JFrame frame;

    /**
     * array representation of the board of the board's images.
     * Each slot tells what kind of image is in the slot *
     */
    private JLabel[][] board = null;
    private JLabel score;
    private JLabel currentTurn = null;
    private JMenuBar menuBar;

    /**
     * Possible values for a disc dropped into a board column
     * Default value is None (empty)
     */
    private enum Disc
    {
        None,
        Player1,
        Player2
    }

    /**
     * Create a new graphical representation of the game. In other words,
     * Create the graphical interface for playing the game.
     *
     * @param p1 - player 1
     * @param p2 - player 2
     */
    public GUI(Player p1, Player p2)
    {
        frame = new JFrame();
        frame.setTitle("Connect Four!");
        score = new JLabel();
        menuBar = new JMenuBar();
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        engine = GameEngine.getInstance(p1, p2);
        createMenu();
    }

    /**
     * Start up the game by setting the board boundary, loading the images
     * and adding the proper input listeners.
     */
    public void startGame()
    {
        initBoard();
        frame.setSize(800, IMG_SIZE * 9);
        frame.addMouseListener(this);
    }

    /**
     * Create the top menu for the game.
     */
    public final void createMenu()
    {
        JMenu file = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ExitApp(frame));
        file.add(exitItem);
        menuBar.add(file);
        JMenu help = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("How to Play");
        helpItem.addActionListener(new HelpMenu(frame));
        helpItem.setSize(300, 200);
        help.add(helpItem);
        menuBar.add(help);
        frame.setJMenuBar(menuBar);
    }

    /**
     * Update the board locations with the proper images after a new
     * disc is added to the board.
     */
    public void updateBoard()
    {
        int[][] a = engine.getBoard().getBoardArray();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                Disc pos = Disc.values()[a[i][j]];
                switch (pos) {
                    case None:
                        board[i][j].setIcon(new ImageIcon(EMPTY_ICON ));
                        break;
                    case Player1:
                        board[i][j].setIcon(new ImageIcon(RED_ICON));
                        break;
                    case Player2:
                        board[i][j].setIcon(new ImageIcon(BLACK_ICON));
                        break;
                }
            }
        }
    }

    /**
     * Called when the game is over to determine what to do next.
     *
     * @param winner - the game winner (if there is one)
     */
    public boolean gameOver(Player winner)
    {
        String message = "Game Over, Draw.";
        if (winner.getInt() != 0) {
            message = String.format("Game Over, Player %d wins.", winner.getInt());
        }
        int playAgain = JOptionPane.showConfirmDialog(frame, message, "Game Over, Play Again?", JOptionPane.YES_NO_OPTION);

        if (playAgain == JOptionPane.YES_OPTION) {
            initBoard();
            updateBoard();

            return false;
        }
        return true;
    }

    public void updateScoreText()
    {
        int[] playerScores = engine.getScore();
        score.setText(String.format("Score: %s - %s", playerScores[0], playerScores[1]));
    }

    public void updateTurnText(Player currentPlayer)
    {
        currentPlayer = currentPlayer == null ? new Player(1) : currentPlayer;
        String color = currentPlayer.getInt() == 1 ? "red" : "black";
        currentTurn.setText(String.format("Current turn: Player %s (%s)", currentPlayer.getInt(), color));
    }

    /**
     * Add the player's disc to the game board.
     *
     * @param columnNumber - the column the disc should be added to
     * @return true if the disc was added (column was not full)
     *
     * @throws OutsideBoardException if the column is full or out of bounds
     */
    public boolean putDisc(int columnNumber) throws OutsideBoardException
    {
        boolean putIsDone = engine.putDisc(new Move(columnNumber));
        updateTurnText(engine.getCurrentPlayer());

        if (putIsDone) {
            Player p = engine.isGameOver();
            updateBoard();
            updateScoreText();
            if (p != null) {
                boolean noMoreGames = gameOver(p);

                if (noMoreGames) {
                    frame.removeMouseListener(this);
                }
            }
        }
        return putIsDone;
    }

    /**
     * Determine where the mouse was just clicked and put a disc in that column
     *
     * @param mouseEvent the mouse event
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent)
    {
        try {
            // get the column that was clicked and putDisc down the correct image
            if (mouseEvent.getY() < IMG_SIZE * (engine.getRowNumber() + 0.5f) &&
                mouseEvent.getX() < IMG_SIZE * engine.getColumnNumber()) {
                putDisc(mouseEvent.getX() / IMG_SIZE);
            }
        } catch (OutsideBoardException ignored) {
        }
    }

    /**
     * Initialize the board. Set all board positions to empty and load
     * the empty image icon for each position.
     */
    private void initBoard()
    {
        engine.clearBoard();
        frame.getContentPane().removeAll();
        board = new JLabel[engine.getRowNumber()][engine.getColumnNumber()];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new JLabel();
                board[i][j].setBounds(j * IMG_SIZE, i * IMG_SIZE, IMG_SIZE, IMG_SIZE);
                board[i][j].setIcon(new ImageIcon(EMPTY_ICON));
                frame.getContentPane().add(board[i][j]);
            }
        }
        currentTurn = new JLabel();
        currentTurn.setBounds(8 * IMG_SIZE, 0, 200, 20);
        score.setBounds(12 * IMG_SIZE, 0, 200, 20);
        updateScoreText();
        updateTurnText(null);
        frame.getContentPane().add(currentTurn);
        frame.getContentPane().add(score);
        frame.setVisible(true);
    }

    /**
     * Context Menu for quitting the app
     */
    private static class ExitApp implements ActionListener
    {
        private JFrame frame;

        ExitApp(JFrame frame)
        {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int playAgain = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?", "", JOptionPane.YES_NO_OPTION);

            if (playAgain == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    /**
     * Context Menu for bringing up the help menu
     */
    private static class HelpMenu implements ActionListener
    {
        private JFrame frame;

        HelpMenu(JFrame frame)
        {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            String msg = "Take turns dropping discs into the columns of the board. \n" +
                "Objective is get of your colored discs in a row (up/down, left/right or diagonally). \n" +
                "First player to get 4 in a row wins.\n";

            JOptionPane.showMessageDialog(frame, msg, "How to play", JOptionPane.OK_OPTION);
        }
    }
}
