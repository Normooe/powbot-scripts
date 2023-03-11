package GCastleWars.Tasks.PlayCastleWars.GoToHidingSpot;

import GCastleWars.Constants;
import GCastleWars.GCastleWars;
import GCastleWars.Task;
import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.GameObject;
import org.powbot.api.rt4.Objects;

public class GoUpstairs extends Task {
    private final Constants c = new Constants();
    GCastleWars main;

    public GoUpstairs(GCastleWars main) {
        super();
        super.name = "GoUpstairs";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return c.p().movementAnimation() == c.p().idleAnimation() &&
                (Constants.ZAMORAK_BASE_MIDDLE.contains(c.p().tile()) || Constants.SARA_BASE_MIDDLE.contains(c.p().tile()));
    }

    @Override
    public void execute() {
        GCastleWars.currentState = Util.state("Going up ladder");
        GameObject ladder = Objects.stream().name("Ladder")
                .filtered(i -> i.actions().contains("Climb-up")).nearest().first();
        if (ladder.valid() && ladder.interact("Climb-up", false)) {
            Condition.wait(() -> Constants.ZAMORAK_BASE_TOP.contains(c.p().tile())
                    || Constants.SARA_BASE_TOP.contains(c.p().tile()), 50, 20);
        }

    }
}
