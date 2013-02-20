package connectfour.engine;

public class GameEngine
{
    // game board starts at 0,0 in the top left corner
    private static final int TOP_ROW      = 0;
    private static final int FAR_LEFT_COL = 0;

    private static GameEngine engine = null;
    private GameBoard board;
    private Player    p1, p2, currentPlayer;
    // the column that was last clicked by a user
    private Move lastColumnClicked = null;

    /**
     * Denotes the different ways the board (or 2d array) can be searched from a given position.
     * Each value represents a step in a direction one can take when searching a direction.
     * In other words, currentPos += SearchDir.SomeValue (adds/subtracts 1)
     */
    private enum SearchDir
    {
        // game board starts at 0,0 in the top left corner
        None(0), // dont search either direction
        Left(-1),
        Right(1),
        Down(1), // the "last" row is first position filled and not last filled
        Up(-1); // the top of the board is the last open spot in the column
        private int value;

        SearchDir(int value) { this.value = value; }

        public int getValue() { return value; }
    }

    /**
     * Private constructor, use getInstance instead
     *
     * @param p1 - player 1
     * @param p2 - player 2
     */
    private GameEngine(Player p1, Player p2)
    {
        board = GameBoard.getInstance();
        this.p1 = p1;
        this.p2 = p2;
        currentPlayer = this.p1;
    }

    /**
     * Singleton for GameEngine. Gets an instance of the game engine or creates
     * a new one.
     *
     * @param p1 - player 1
     * @param p2 - player 2
     * @return a game instance
     */
    public static synchronized GameEngine getInstance(Player p1, Player p2)
    {
        if (engine == null) {
            engine = new GameEngine(p1, p2);
        }
        return engine;
    }

    /**
     * Add the player's disc to the selected column on the board
     *
     * @param move - spot to add the disc
     * @return true if the location was not full and added
     *
     * @throws OutsideBoardException on invalid (out of bounds or column full)
     */
    public boolean putDisc(Move move) throws OutsideBoardException
    {
        if (board.putDisc(currentPlayer.getInt(), move.getPosition())) {
            lastColumnClicked = move;
            nextTurn();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Switches turns to the next player
     */
    private void nextTurn()
    {
        currentPlayer = currentPlayer == p1 ? p2 : p1;
    }

    /**
     * Looks for a winner in all directions based on last clicked column
     *
     * @return true if a winner is found
     */
    private boolean declareWinner()
    {
        try {
            // search to the left and right for a connect four
            int rowPieces = findWinner(SearchDir.Right, SearchDir.None) + findWinner(SearchDir.Left, SearchDir.None) - 1;
            if (rowPieces >= 4) {
                return true;
            }
            // search up and down for a connect four
            int columnPieces = findWinner(SearchDir.None, SearchDir.Down) + findWinner(SearchDir.None, SearchDir.Up) - 1;
            if (columnPieces >= 4) {
                return true;
            }
            // search diagonally to the right for a connect four
            int backSlashPieces = findWinner(SearchDir.Right, SearchDir.Down) + findWinner(SearchDir.Left, SearchDir.Up) - 1;
            if (backSlashPieces >= 4) {
                return true;
            }
            // search diagonally to the left for a connect four
            int foreSlashPieces = findWinner(SearchDir.Right, SearchDir.Up) + findWinner(SearchDir.Left, SearchDir.Down) - 1;
            if (foreSlashPieces >= 4) {
                return true;
            }
        } catch (OutsideBoardException ignored) {

        }
        return false;
    }

    /**
     * Searches a given direction for a connect four on the game board
     *
     * @param horzSearchDir - the current horizontal direction to search (left or right or none)
     * @param vertSearchDir - the current vertical direction to search (up or down or none)
     * @return the number of like player discs in a direction (4 being a winner)
     *
     * @throws OutsideBoardException thrown if it tries to search out of bounds
     */
    private int findWinner(SearchDir horzSearchDir, SearchDir vertSearchDir) throws OutsideBoardException
    {
        int lastMoveColumn = lastColumnClicked.getPosition();
        int lastMoveRow = board.getRowNumber() - board.getColumnHeight(lastMoveColumn);

        int curRow = lastMoveRow;
        int curCol = lastMoveColumn;
        int count = 0;

        do {
            curRow += vertSearchDir.getValue();
            curCol += horzSearchDir.getValue();
            ++count;
        }
        while (count < 4 && !outOfBounds(curRow, curCol) && samePosition(lastMoveColumn, lastMoveRow, curRow, curCol));
        return count;
    }

    /**
     * Is the current position outside of the board?
     *
     * @param curRow - row in question
     * @param curCol - column in question
     * @return true if position is out of bounds for the row/column
     */
    private boolean outOfBounds(int curRow, int curCol)
    {
        return curRow < TOP_ROW || pastLastRow(curRow) || curCol < FAR_LEFT_COL || pastLastColumn(curCol);
    }

    /**
     * Is the current position the same as the last clicked position by the player?
     *
     * @param lastMoveColumn - the last column position clicked
     * @param lastMoveRow - the last row position clicked
     * @param curRow - the current row that is being searched
     * @param curCol - the current column that is being searched
     * @return true if the positions are identical
     *
     * @throws OutsideBoardException if either position is outside of the board
     */
    private boolean samePosition(int lastMoveColumn, int lastMoveRow, int curRow, int curCol) throws OutsideBoardException
    {
        return board.getBoardPos(curRow, curCol) == board.getBoardPos(lastMoveRow, lastMoveColumn);
    }

    /**
     * Is the current position past the far right in the row?
     *
     * @param curCol - the current column in question
     * @return true if this is the far right column
     */
    private boolean pastLastColumn(int curCol)
    {
        return curCol == board.getColumnNumber();
    }

    /**
     * Is the current position the past last (row empty) in the column?
     *
     * @param curRow - the current row in question
     * @return true if this is the last row
     */
    private boolean pastLastRow(int curRow)
    {
        return curRow == board.getRowNumber();
    }

    /**
     * Is the game over yet?
     *
     * @return the player that won if game over. If a draw (board full), return a new player with
     *         number zero. Otherwise, return null if game is not over.
     */
    public Player isGameOver()
    {
        if (board.isBoardFull()) {
            return new Player(0);
        }
        if (declareWinner()) {

            if (currentPlayer == p1) {
                ++p2.wins;
                return p2;
            }
            else {
                ++p1.wins;
                return p1;
            }
        }
        return null;
    }

    /**
     * Get the score of the game
     * @return number of wins by each player
     */
    public int[] getScore()
    {
        return new int[] {p1.wins, p2.wins};
    }

    /**
     * Fetches the current player on this turn
     *
     * @return the current player
     */
    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    /**
     * Get the total rows for the board.
     *
     * @return the total row length
     */
    public int getRowNumber()
    {
        return board.getRowNumber();
    }

    /**
     * Get the total columns for the board
     *
     * @return the total column length
     */
    public int getColumnNumber()
    {
        return board.getColumnNumber();
    }

    public void clearBoard()
    {
        board.clearBoard();
    }

    /**
     * Return an instance of the current game board
     *
     * @return the current game board
     */
    public GameBoard getBoard()
    {
        return board;
    }
}
