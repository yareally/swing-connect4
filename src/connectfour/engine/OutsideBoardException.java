package connectfour.engine;

public class OutsideBoardException extends Exception
{
    private static final long serialVersionUID = 799526730449750418L;

    /**
     * A more friendly exception to determining if a move is valid or not
     * Invalid moves are outside of the board or when a column/board is full
     *
     * @return the message stating why the exeption was thrown
     */
    @Override
    public String getMessage()
    {
        return "The current move is outside the game board.";
    }
}
