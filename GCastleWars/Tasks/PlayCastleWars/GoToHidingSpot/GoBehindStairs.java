package GCastleWars.Tasks.PlayCastleWars.GoToHidingSpot;

import GCastleWars.Constants;
import GCastleWars.GCastleWars;
import GCastleWars.Task;
import Util.Util;
import org.powbot.api.Tile;
import org.powbot.api.rt4.Movement;

public class GoBehindStairs extends Task {
    private final Constants c = new Constants();
    GCastleWars main;

    public GoBehindStairs(GCastleWars main) {
        super();
        super.name = "GoBehindStairs";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return c.p().movementAnimation() == c.p().idleAnimation()
                && (Constants.ZAMORAK_BASE_TOP.contains(c.p().tile()) || Constants.SARA_BASE_TOP.contains(c.p().tile()))
                && (!Constants.ZAMORAK_HIDING_SPOTS.contains(c.p().tile()) && !Constants.SARA_HIDING_SPOTS.contains(c.p().tile())
        );
    }

    @Override
    public void execute() {
        GCastleWars.currentState = Util.state("Hiding behind stairs");
        if (Constants.ZAMORAK_BASE_TOP.contains(c.p().tile())) {
            System.out.println("Hiding behind zammy stairs");
            Tile hidingSpot = Constants.ZAMORAK_HIDING_SPOTS.getRandomTile();
            Movement.step(hidingSpot);
        } else if (Constants.SARA_BASE_TOP.contains(c.p().tile())) {
            System.out.println("Hiding behind sara stairs");
            Tile hidingSpot = Constants.SARA_HIDING_SPOTS.getRandomTile();
            Movement.step(hidingSpot);
        }
    }
}
