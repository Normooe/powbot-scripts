package GEctofuntus;

import org.powbot.api.Area;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Player;
import org.powbot.api.rt4.Players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public Constants() {
        super();
    }

    public ArrayList<String> userTaskList = new ArrayList<String>();

    public Player p() {
        return Players.local();
    }


    // my stuff
    public static final Area bankArea = new Area(new Tile(3686, 3467, 0), new Tile(3690, 3466, 0));
    public static final Area PORT_PHASMATYS = new Area(new Tile(3659, 3507, 0), new Tile(3691, 3466, 0));
    public static final Area ALTAR_BOT_FLOOR = new Area(new Tile(3655, 3524, 0), new Tile(3664, 3514, 0));
    public static final Area BARRIER_ALTAR_SIDE = new Area(new Tile(3658, 3509, 0), new Tile(3660, 3511, 0));
    public static final Tile BARRIER_TILE_WEST = new Tile(3659, 3507, 0);
    public static final Tile BARRIER_TILE_EAST = new Tile(3660, 3507, 0);
    public static final Tile STUCK_BANK_TILE = new Tile(3688, 3471, 0);

    public static final int ECTO_TOKEN_ID = 4278;
}