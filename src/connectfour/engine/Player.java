package connectfour.engine;

public class Player
{
    private int playerNum;
    public int wins = 0;

    /**
     * A player object to keep track of game players
     *
     * @param playerNum - the id for the player
     */
    public Player(int playerNum)
    {
        this.playerNum = playerNum;
    }

    /**
     * Get this player's current ID
     *
     * @return the player ID (i.e. player 1 or player 2)
     */
    public int getInt()
    {
        return playerNum;
    }
}