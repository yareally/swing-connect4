package connectfour.engine;

public class Move
{
    private int position;

    /**
     * Create a wrapper for player moves
     *
     * @param position - the column for the move
     */
    public Move(int position)
    {
        this.position = position;
    }

    /**
     * Where is the move taking place?
     *
     * @return move position
     */
    public int getPosition()
    {
        return position;
    }
}
