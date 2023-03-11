package GCastleWars.Tasks.PlayCastleWars.GetIntoGame;

import GCastleWars.GCastleWars;
import GCastleWars.Task;
import GCastleWars.Constants;

import Util.Util;
import org.powbot.api.Condition;
import org.powbot.api.rt4.*;

public class GoToWaitingRoom extends Task {
    private final Constants c = new Constants();
    GCastleWars main;

    public GoToWaitingRoom(GCastleWars main) {
        super();
        super.name = "GoToWaitingRoom";
        this.main = main;
    }
    @Override
    public boolean activate() {
        return c.p().movementAnimation() == c.p().idleAnimation()
                && Constants.CASTLE_WARS_OUTSIDE.contains(c.p().tile());
    }

    @Override
    public void execute() {
        GCastleWars.currentState = Util.state("Entering waitng room");
        GameObject portal = Objects.stream(20).type(GameObject.Type.INTERACTIVE).name("Guthix Portal").nearest().first();
        if (portal.valid()) {
            if (portal.inViewport() && portal.interact("Enter")) {
                Condition.wait(() -> Components.stream().widget(Constants.TIME_UNTIL_START_WIDGET_PARENT).first().visible(), 50, 20);
            }
        }
    }
}
