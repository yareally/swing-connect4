package connectfour.main;

import connectfour.engine.Player;
import connectfour.engine.GUI;

public class Main
{
    public static void main(String... args)
    {
        Player p1 = new Player(1);
        Player p2 = new Player(2);
        GUI ui = new GUI(p1, p2);
        ui.startGame();
    }
}