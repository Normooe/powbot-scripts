package GFossilIsland;

import org.powbot.api.Area;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Player;
import org.powbot.api.rt4.Players;

import java.util.ArrayList;

public class Constants {

    public Constants() {
        super();
    }

    public ArrayList<String> userTaskList = new ArrayList<String>();

    public Player p() {
        return Players.local();
    }

    public static final int UNNOTED_GIANT_SEAWEED_ID = 21504;
    public static final int NOTED_GIANT_SEAWEED_ID = 21505;
}